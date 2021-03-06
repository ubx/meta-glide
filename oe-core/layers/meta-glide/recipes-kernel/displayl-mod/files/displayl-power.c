/*
 *  displayl-power.c - Display-L power and backlight driver
 *
 */

#define DEBUG

#define pr_fmt(fmt) KBUILD_MODNAME ": " fmt

#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/slab.h>
#include <linux/i2c.h>
#include <linux/delay.h>
#include <linux/syscalls.h>
#include <linux/device.h>
#include <linux/input.h>

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

/* defile brightness */
#define AUTO_MAX 0xFFF
#define AUTO_MIN 0x100
#define MAN_INC  0x040
#define IDLE     0x0A0

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
"sysfs (lux) for isl29003 ambient light sensor");

static unsigned long delay;

struct displayl_power_data {
    struct i2c_client *client;
    struct delayed_work dwork;
};
static struct displayl_power_data *displayl_power_gbl;

#pragma pack(push, 1)
struct attiny_register_set {
    u8 command;
    u16 lcd_backlight_pwm;
    u16 lcd_backlight_pwm_offset;
    u16 lcd_backlight_pwm_slope;
    u8 version;
    u16 temperatur;
    u16 eeprom_write_cnt;
    u16 tmp_adc_read_cnt;
    u8 spare[4];
};
#pragma pack(pop)

static struct attiny_register_set attiny_regs;

static volatile bool do_rmo = false;
static struct file *file;

static int lux = AUTO_MIN;
static u16 lcd_backlight_pwm;
static u16 lcd_backlight_manual_pwm = AUTO_MIN;

static int displayl_power_read_lux() {
    char buf[8] = {0};
    int x = 0;
    kernel_read(file, 0, buf, sizeof(buf));
    kstrtol(buf, 10, &x);
    return x;
}

static void displayl_power_schedule(struct displayl_power_data *displayl_power) {
    schedule_delayed_work(&displayl_power->dwork, delay);
}

static u16 lux_to_lcd_backlight_pwm(int lux) {
    return (lux * 1000) / 8000;
}

static int displayl_power_set_backlight(struct displayl_power_data *displayl_power) {
    int x;

    x = displayl_power_read_lux();
    lux = (lux + x) / 2; // small lowpass filter
    if (abs(lux - x) > 20) {
        delay = msecs_to_jiffies(50);
    } else {
        delay = msecs_to_jiffies(250);
    }

    /* auto adjustment */
    lcd_backlight_pwm = max(min(max(lux_to_lcd_backlight_pwm(lux), AUTO_MIN), AUTO_MAX), lcd_backlight_manual_pwm);
    if (attiny_regs.lcd_backlight_pwm != lcd_backlight_pwm) {
        attiny_regs.lcd_backlight_pwm = lcd_backlight_pwm;
        i2c_master_send(displayl_power->client, &attiny_regs,
                        sizeof(attiny_regs.command) + sizeof(attiny_regs.lcd_backlight_pwm));
        //pr_debug("%s, new attiny_regs.lcd_backlight_pwm=0x%x\n", __func__, attiny_regs.lcd_backlight_pwm);
    }
    return 0;
}

static void displayl_power_reset_hard_power_off(struct displayl_power_data *displayl_power) {
    int sts;
    sts = i2c_master_recv(displayl_power->client, &attiny_regs.command, sizeof(attiny_regs.command));
    if (sts == 1) {
        attiny_regs.command |= (1 << RMO);
        i2c_master_send(displayl_power->client, &attiny_regs.command, sizeof(attiny_regs.command));
        attiny_regs.command &= ~(1 << RMO);
        i2c_master_send(displayl_power->client, &attiny_regs.command, sizeof(attiny_regs.command));
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
    int ret;
    ret = i2c_master_recv(displayl_power->client, &attiny_regs.command, sizeof(attiny_regs.command));
    if (ret == 1) {
        pr_debug("displayl_power_information: attiny_regs.command=%u\n", attiny_regs.command);
        ret = 0;
    }
    return ret;
}

static int displayl_power_setup(struct displayl_power_data *displayl_power) {
    int ret;
    u8 last_command;
    ret = i2c_master_recv(displayl_power->client, &attiny_regs.command, sizeof(attiny_regs.command));
    if (ret == 1) {
        last_command = attiny_regs.command;
        attiny_regs.command |= (1 << TGR);
        if (last_command != attiny_regs.command) {
            attiny_regs.command |= (1 << SAV);
            ret = i2c_master_send(displayl_power->client, &attiny_regs.command, sizeof(attiny_regs.command));
            if (ret == 1) {
                ret = 0;
            }
        }
        ret = 0;
    }
    return ret;
}

static void displayl_power_do_poweroff(void) {
    i2c_master_recv(displayl_power_gbl->client, &attiny_regs.command, sizeof(attiny_regs.command));
    attiny_regs.command |= (1 << POF);
    i2c_master_send(displayl_power_gbl->client, &attiny_regs.command, sizeof(attiny_regs.command));
}

static const struct input_device_id input_event_ids[] = {
        {.driver_info = 1},    /* Matches all devices */
        {},                    /* Terminating zero entry */
};

MODULE_DEVICE_TABLE(input, input_event_ids
);

static void input_event5(struct input_handle *handle, unsigned int type, unsigned int code, int value) {
    //printk(KERN_DEBUG pr_fmt("Event. Dev: %s, Type: %d, Code: %d, Value: %d\n"), dev_name(&handle->dev->dev), type, code, value);
    if (type == EV_KEY) {
        if (code == KEY_BRIGHTNESSUP && value == 1) {
            lcd_backlight_manual_pwm = min(lcd_backlight_manual_pwm + MAN_INC, AUTO_MAX);
        } else if (code == KEY_BRIGHTNESSDOWN && value == 1) {
            lcd_backlight_manual_pwm = max(lcd_backlight_manual_pwm - MAN_INC, AUTO_MIN);
        }
    }
}

static bool input_event_match(struct input_handler *handler, struct input_dev *dev) {
    return true;
}


static int input_event_connect(struct input_handler *handler, struct input_dev *dev, const struct input_device_id *id) {
    struct input_handle *handle;
    int error;

    handle = kzalloc(sizeof(struct input_handle), GFP_KERNEL);
    if (!handle)
        return -ENOMEM;

    handle->dev = dev;
    handle->handler = handler;
    handle->name = "input_events";

    error = input_register_handle(handle);
    if (error)
        goto err_free_handle;

    error = input_open_device(handle);
    if (error)
        goto err_unregister_handle;

    printk(KERN_DEBUG
    pr_fmt("Connected device: %s (%s at %s)\n"),
            dev_name(&dev->dev),
            dev->name ?: "unknown",
            dev->phys ?: "unknown");

    return 0;

    err_unregister_handle:
    input_unregister_handle(handle);
    err_free_handle:
    kfree(handle);
    return error;
}

static void input_event_disconnect(struct input_handle *handle) {
    printk(KERN_DEBUG
    pr_fmt("Disconnected device: %s\n"), dev_name(&handle->dev->dev));
    input_close_device(handle);
    input_unregister_handle(handle);
    kfree(handle);
}

static struct input_handler input_event_handler = {
        .event =    input_event5,
        .match =    input_event_match,
        .connect =    input_event_connect,
        .disconnect =    input_event_disconnect,
        .name =        "input_events",
        .id_table =    input_event_ids,
};

static int displayl_power_probe(struct i2c_client *client,
                                const struct i2c_device_id *id) {
    struct displayl_power_data *displayl_power;
    int error;

    dev_dbg(&client->dev, "adapter=%d, client irq: %d\n",
            client->adapter->nr, client->irq);

    error = input_register_handler(&input_event_handler);
    if (error == 0) {
        printk(KERN_INFO pr_fmt("loaded.\n"));
    } else {
        return error;
    }

    /* Check if the I2C function is ok in this adaptor */
    if (!i2c_check_functionality(client->adapter, I2C_FUNC_I2C))
        return -ENXIO;

    displayl_power = kzalloc(sizeof(struct displayl_power_data), GFP_KERNEL);
    displayl_power->client = client;

    /* open isl29003 ambient lux sensor */
    file = filp_open(isl29003_path, O_RDONLY, 0);
    if (IS_ERR(file)) {
        dev_err(&client->dev, "%s: error open '%s'\n", __func__, isl29003_path);
        file = NULL;
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
    if (file != NULL) {
        filp_close(file, NULL);
    }
    input_unregister_handler(&input_event_handler);
    kfree(displayl_power);
    return 0;
}

/* external called procedures */
static void power_reset_hard_power_off(bool reset) {
    //pr_debug("power_reset_hard_power_off: reset=%u\n", reset);
    do_rmo = reset;
}

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

EXPORT_SYMBOL(power_reset_hard_power_off);

module_i2c_driver(displayl_powerdriver);

MODULE_AUTHOR("Andreas Lüthi <andreas.luethi@gmx.net>");
MODULE_DESCRIPTION("Display-L power and backlight driver");
MODULE_LICENSE("GPL");
MODULE_ALIAS("displayl_poweri2c-driver");
