DESCRIPTION="Set U-boot serial console to UARTB"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append += " \
	file://0001-cosole_UARTA.patch \
"
SRC_URI_append += "${@'file://0002-cosole_UARTA.patch' if d.getVar('EVAL_BOARD') == 'n' else ''}"
