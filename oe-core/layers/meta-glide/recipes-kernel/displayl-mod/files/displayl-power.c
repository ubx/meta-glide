/*
 *  displayl-power.c - Display-L power and backlight driver
 *
 */

#define DEBUG

#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/slab.h>
#include <linux/i2c.h>
#include <linux/delay.h>
#include <linux/syscalls.h>

#define DISPLAYL_POWER_NAME      "displayl_power"

/* i2c register */
#define CNTL  0x00 // command
#define POF 0 // power off
#define TGR 1 // tagra colibri module
#define CPC 2 //CAN power control, allow power ON on CAN bus RE
#define PUP 3 // backup power source available (two power source, set by T27X driver)
#define PDN 4 // power down (save power as much as possible)
#define EXP 5 // external power enable state
#define SAV 6 // save CPC and TGR to eeprom
#define RMO 7 // reset hard power OFF

/* Polling Rate */
static int scan_rate = 1;
module_param(scan_rate,
int, 0644);
MODULE_PARM_DESC(scan_rate,
"Polling rate in times/sec. Default = 1");

static char *isl29003_path = "/sys/class/i2c-dev/i2c-0/device/0-0044/lux";
module_param(isl29003_path, charp,
0660);
MODULE_PARM_DESC(isl29003_path,
"sysfs (lux) for isl29003 ambient sensor");

static unsigned long delay;

struct displayl_power_data {
    struct i2c_client *client;
    struct delayed_work dwork;
};
static struct displayl_power_data *displayl_power_gbl;

struct backlight_power {
    u8 cmd;
    u16 pwr;
    u8 filler;
};

static volatile bool do_rmo = false;
static struct file *file;

static long displayl_power_read_lux() {
    char buf[8] = {0};
    long lux = 0;
    kernel_read(file, 0, buf, sizeof(buf));
    kstrtol(buf, 10, &lux);
    //pr_debug("displayl_power_read_lux: lux=%u\n", lux);
    return lux;
}

static void displayl_power_schedule(struct displayl_power_data *displayl_power) {
    schedule_delayed_work(&displayl_power->dwork, delay);
}

static int displayl_power_set_backlight(struct displayl_power_data *displayl_power) {
    struct backlight_power blp;
    u8 cmd;
    int ret;
    ret = i2c_master_recv(displayl_power->client, &cmd, 1);
    blp.cmd = cmd;
    /* todo -- calculate power */
    blp.pwr = displayl_power_read_lux() * 30;
    ret = i2c_master_send(displayl_power->client, &blp, sizeof(blp));
    ret = i2c_master_recv(displayl_power->client, &blp, sizeof(blp));
    pr_debug("%s, new pwr=%u\n", __func__, blp.pwr);
    return 0;
}

static void displayl_power_reset_hard_power_off(struct displayl_power_data *displayl_power) {
    u8 cmd;
    int sts;
    sts = i2c_master_recv(displayl_power->client, &cmd, 1);
    if (sts == 1) {
        //pr_debug("displayl_power_reset_hard_power_off: cmd-0=%u\n", cmd);
        cmd |= (1 << RMO);
        i2c_master_send(displayl_power->client, &cmd, 1);
        cmd &= ~(1 << RMO);
        i2c_master_send(displayl_power->client, &cmd, 1);
    }
}

static void displayl_power_worker(struct work_struct *work) {
    struct displayl_power_data *displayl_power = container_of(work,
    struct displayl_power_data, dwork.work);
    if (do_rmo) {
        displayl_power_reset_hard_power_off(displayl_power);
    }
    displayl_power_set_backlight(displayl_power);
    displayl_power_schedule(displayl_power);
}

static int displayl_power_information(struct displayl_power_data *displayl_power) {
    u8 cmd;
    int ret;
    pr_debug("displayl_power_information");
    ret = i2c_master_recv(displayl_power->client, &cmd, 1);
    if (ret == 1) {
        pr_debug("displayl_power_information: command=%u\n", cmd);
        ret = 0;
    }
    return ret;
}

static int displayl_power_setup(struct displayl_power_data *displayl_power) {
    u8 cmd;
    int ret;
    pr_debug("displayl_power_setup");
    ret = i2c_master_recv(displayl_power->client, &cmd, 1);
    if (ret == 1) {
        cmd |= (1 << TGR);
        ret = i2c_master_send(displayl_power->client, &cmd, 1);
        if (ret == 1) {
            ret = 0;
        }
    }
    return ret;
}

static void displayl_power_do_poweroff(void) {
    u8 cmd;
    i2c_master_recv(displayl_power_gbl->client, &cmd, 1);
    pr_debug("displayl_power_do_poweroff: cmd-0=%u\n", cmd);
    cmd |= (1 << POF);
    i2c_master_send(displayl_power_gbl->client, &cmd, 1);
}

static int displayl_power_probe(struct i2c_client *client,
                                const struct i2c_device_id *id) {
    struct displayl_power_data *displayl_power;
    int error;

    dev_dbg(&client->dev, "adapter=%d, client irq: %d\n",
            client->adapter->nr, client->irq);

    /* Check if the I2C function is ok in this adaptor */
    if (!i2c_check_functionality(client->adapter, I2C_FUNC_I2C))
        return -ENXIO;

    displayl_power = kzalloc(sizeof(struct displayl_power_data), GFP_KERNEL);
    displayl_power->client = client;

    /* open isl29003 ambient lux sensor */
    file = filp_open(isl29003_path, O_RDONLY, 0);
    if (IS_ERR(file)) {
        dev_err(&client->dev, "%s: error open '%s'\n", __func__, isl29003_path);
    }

    if (pm_power_off != NULL) {
        dev_err(&client->dev,
                "%s: pm_power_off function was already registered\n", __func__);
    }
    pm_power_off = &displayl_power_do_poweroff;
    displayl_power_gbl = displayl_power;

    i2c_set_clientdata(client, displayl_power);

    error = displayl_power_setup(displayl_power);
    if (error) {
        dev_err(&client->dev, "%s: failed to setup: %d\n", __func__, error);
        return error;
    }

    error = displayl_power_information(displayl_power);
    if (error) {
        dev_err(&client->dev, "%s: failed to read information: %d\n", __func__, error);
        return error;
    }

    INIT_DELAYED_WORK(&displayl_power->dwork, displayl_power_worker);
    delay = msecs_to_jiffies(MSEC_PER_SEC / scan_rate);
    displayl_power_schedule(displayl_power);
    return 0;
}

static int displayl_power_remove(struct i2c_client *client) {
    struct displayl_power_data *displayl_power = i2c_get_clientdata(client);
    cancel_delayed_work_sync(&displayl_power->dwork);
    kfree(displayl_power);
    return 0;
}

/* external called procedures */
static void power_reset_hard_power_off(bool reset) {
    //pr_debug("power_reset_hard_power_off: reset=%u\n", reset);
    do_rmo = reset;
}

EXPORT_SYMBOL(power_reset_hard_power_off);

static const struct i2c_device_id displayl_powerdev_id[] = {
        {DISPLAYL_POWER_NAME, 0,},
        {}
};

MODULE_DEVICE_TABLE(i2c, displayl_powerdev_id
);

static struct i2c_driver displayl_powerdriver = {
        .driver = {
                .name    = DISPLAYL_POWER_NAME,
        },
        .id_table    = displayl_powerdev_id,
        .probe       = displayl_power_probe,
        .remove      = displayl_power_remove,
};

module_i2c_driver(displayl_powerdriver);

MODULE_AUTHOR("Andreas Lüthi <andreas.luethi@gmx.net>");
MODULE_DESCRIPTION("Display-L power and backlight driver");
MODULE_LICENSE("GPL");
MODULE_ALIAS("displayl_poweri2c-driver");
