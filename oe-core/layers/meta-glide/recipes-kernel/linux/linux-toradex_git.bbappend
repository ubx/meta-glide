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
     echo "dummy" > /dev/null
}

do_configure_prepend () {
    config_script
}

SRC_URI_append += " \
	file://can_interrupt.patch \
"