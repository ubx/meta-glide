DESCRIPTION="Enable CAN"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

config_script () {
     echo "CONFIG_CAN=y" >> ${B}/.config
     echo "CONFIG_CAN_VCAN=y" >> ${B}/.config
     echo "CONFIG_CAN_RAW=y" >> ${B}/.config
     echo "CONFIG_CAN_BCM=y"  >> ${B}/.config
     echo "CONFIG_CAN_DEV=y" >> ${B}/.config
     echo "CONFIG_CAN_MCP251X=y"  >> ${B}/.config
     echo "CONFIG_FRAMEBUFFER_CONSOLE=n"  >> ${B}/.config
     echo "CONFIG_INPUT_GPIO_ROTARY_ENCODER=y" >> ${B}/.config
     echo "CONFIG_TEGRA_CAMERA=n" >> ${B}/.config
     echo "dummy" > /dev/null
     if [$EVAL_BOARD = "y"]; then
        bbdebug "*** Build is for Colibri Evaluation Board."
        sed -i 's\+//#define EVAL_BOARD\+#define EVAL_BOARD\' ${THISDIR}/${PN}/0001-displayl.patch
     fi
}

do_configure_prepend () {
    config_script
}

## Change/add pins for Display-L board
SRC_URI_append += " \
	file://0001-displayl.patch \
"