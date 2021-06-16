DESCRIPTION="Set U-boot serial console to UARTB"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_${MACHINE} = " \
	file://0001-consol_to_UARTB.patch \
	file://0002-usb_high_speed.patch \
	file://0003-consoleblank.patch \
"
