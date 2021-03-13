SUMMARY = "arrowkey-mod_0.1.bb"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"
SRCREV = "${AUTOREV}"
BB_STRICT_CHECKSUM = "0"

inherit module

SRC_URI = " \
  git://github.com/lwfinger/rtl8723bu.git;protocol=git;branch=master \
  "
S = "${WORKDIR}/git"

export KERNELDIR="${KERNEL_SRC}"

