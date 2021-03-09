#define pr_fmt(fmt) KBUILD_MODNAME ": " fmt

#include <linux/slab.h>
#include <linux/module.h>
#include <linux/input.h>
#include <linux/init.h>
#include <linux/device.h>
#include <linux/reboot.h>

MODULE_AUTHOR("andreas.luethi@gmx.net");
MODULE_DESCRIPTION("Shutdown after holding <key> pressed for <delay> seconds");
MODULE_ALIAS("shutdown-keyhold");
MODULE_LICENSE("GPL");

static int delay = 3000;
module_param(delay, int, S_IRUGO);

static int key = KEY_1;
module_param(key, int, S_IRUGO);

static char *devicename = "gpio-keys";
static struct input_dev *button_dev;

static struct timer_list timer;

static void send_key(int key) {
    printk(KERN_INFO pr_fmt("send_key: %d"), key);
    input_report_key(button_dev, key, 1);
    input_sync(button_dev);
    input_report_key(button_dev, key, 0);
    input_sync(button_dev);
}

void timer_callback( unsigned long data ) {
    printk(KERN_INFO pr_fmt("kernel timer callback executing, data is %ld\n"), data);
    send_key(KEY_9);
}

static void gpio_keys_event(struct input_handle *handle, unsigned int type, unsigned int code, int value) {
    printk(KERN_INFO
    pr_fmt("Event. Dev: %s, Type: %d, Code: %d, Value: %d\n"), dev_name(&handle->dev->dev), type, code, value);
    if (type == EV_KEY) {
        if (code == key) {
            if (value == 0) {
                printk(KERN_INFO pr_fmt("setup_timer"));
                mod_timer(&timer, jiffies + msecs_to_jiffies(delay));
            } else if (value == 1) {
                printk(KERN_INFO pr_fmt("del_timer"));
                del_timer(&timer);
            }
        }
    }
}

static int gpio_keys_connect(struct input_handler *handler, struct input_dev *dev, const struct input_device_id *id) {
    struct input_handle *handle;
    int error;

    handle = kzalloc(sizeof(struct input_handle), GFP_KERNEL);
    if (!handle)
        return -ENOMEM;

    handle->dev = dev;
    handle->handler = handler;
    handle->name = "gpio_arrowkey";

    error = input_register_handle(handle);
    if (error)
        goto err_free_handle;

    error = input_open_device(handle);
    if (error)
        goto err_unregister_handle;

    printk(KERN_INFO pr_fmt("Connected device: %s (%s at %s)\n"),
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

bool startsWith(const char *pre, const char *str) {
    size_t lenpre = strlen(pre),
            lenstr = strlen(str);
    return lenstr < lenpre ? false : strncmp(pre, str, lenpre) == 0;
}

static bool gpio_keys_match(struct input_handler *handler, struct input_dev *dev) {
    return startsWith(devicename, dev->name);
}

static void gpio_keys_disconnect(struct input_handle *handle) {
    printk(KERN_INFO pr_fmt("Disconnected device: %s\n"), dev_name(&handle->dev->dev));
    input_close_device(handle);
    input_unregister_handle(handle);
    kfree(handle);
}

static const struct input_device_id gpio_keys_ids[] = {
        {.driver_info = 1},    /* Matches all devices */
        {},                    /* Terminating zero entry */
};

MODULE_DEVICE_TABLE(input, gpio_keys_ids);

static struct input_handler gpio_keys_handler = {
        .event =	gpio_keys_event,
        .match =	gpio_keys_match,
        .connect =	gpio_keys_connect,
        .disconnect =	gpio_keys_disconnect,
        .name =		"shutdown_keyhold",
        .id_table =	gpio_keys_ids,
};

static int __init shutdown_keyhold_init(void) {
    int error;
    int i;

    if (input_register_handler(&gpio_keys_handler) == 0) {
        printk(KERN_INFO pr_fmt("loaded.\n"));
        setup_timer(&timer, timer_callback, 0);
    } else {
        return 1;
    }

    button_dev = input_allocate_device();
    if (!button_dev) {
        printk(KERN_ERR pr_fmt("Not enough memory\n"));
        return  -ENOMEM;
    }

    button_dev->name = "Shutdown Key";
    button_dev->evbit[0] = BIT_MASK(EV_KEY); // | BIT_MASK(EV_MSC);
    set_bit(KEY_9, button_dev->keybit);

    error = input_register_device(button_dev);
    if (error) {
        printk(KERN_ERR pr_fmt("Failed to register device\n"));
        goto err_free_dev;
    }
    return 0;
    err_free_dev:
    input_free_device(button_dev);
    return error;

}

static void __exit shutdown_keyhold_exit(void) {
    del_timer(&timer);
    input_unregister_device(button_dev);
    input_free_device(button_dev);
    input_unregister_handler(&gpio_keys_handler);
    input_free_device(&gpio_keys_handler);
}

module_init(shutdown_keyhold_init);
module_exit(shutdown_keyhold_exit);
