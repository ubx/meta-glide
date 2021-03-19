DESCRIPTION="Enable CAN"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

config_script () {
     bbdebug 1 "EVAL_BOARD=$EVAL_BOARD"
     echo "CONFIG_CAN=m" >> ${B}/.config
     echo "CONFIG_CAN_VCAN=m" >> ${B}/.config
     echo "CONFIG_CAN_RAW=m" >> ${B}/.config
     echo "CONFIG_CAN_BCM=m"  >> ${B}/.config
     echo "CONFIG_CAN_DEV=m" >> ${B}/.config
     echo "CONFIG_CAN_MCP251X=m"  >> ${B}/.config
     echo "CONFIG_FRAMEBUFFER_CONSOLE=y"  >> ${B}/.config
     echo "CONFIG_INPUT_GPIO_ROTARY_ENCODER=y" >> ${B}/.config
     echo "CONFIG_TEGRA_CAMERA=n" >> ${B}/.config
     if [$EVAL_BOARD == "n"]; then
         echo "### NO backlight support"
         echo "CONFIG_BACKLIGHT_LCD_SUPPORT=n" >> ${B}/.config
         echo "CONFIG_LCD_CLASS_DEVICE=n" >> ${B}/.config
         echo "CONFIG_BACKLIGHT_CLASS_DEVICE=n" >> ${B}/.config
         echo "CONFIG_BACKLIGHT_GENERIC=n" >> ${B}/.config
         echo "CONFIG_BACKLIGHT_PWM=n" >> ${B}/.config
         echo "CONFIG_BACKLIGHT_GPIO=n" >> ${B}/.config
         ### For rotate LCD
         echo "CONFIG_LCD_ROTATION=y" >> ${B}/.config
         echo "CONFIG_FRAMEBUFFER_CONSOLE_ROTATION=y" >> ${B}/.config
     fi
     echo "CONFIG_LOGO_CUSTOM_CLUT224=y" >> ${B}/.config
     echo "CONFIG_R8188EU=m" >> ${B}/.config
     echo "CONFIG_USB_SERIAL_CH34x=y" >> ${B}/.config
     echo "CONFIG_USB_SERIAL_CP210X=y" >> ${B}/.config
     echo "CONFIG_USB_SERIAL_FTDI_SIO=y" >> ${B}/.config
     echo "dummy" > /dev/null
}

do_configure_prepend () {
     config_script
}

## Change/add pins for Display-L board
SRC_URI_append += " \
	file://0001-displayl.patch \
	file://0001-logo.patch \
"

SRC_URI_append += "${@'file://0002-no-eval_board.patch' if d.getVar('EVAL_BOARD') == 'n' else ''}"
