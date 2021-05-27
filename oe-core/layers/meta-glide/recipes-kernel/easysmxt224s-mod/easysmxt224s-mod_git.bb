SUMMARY = "Driver for easy I2C mXT224S Controller (from DATA MODUL)"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

PV .= "+git${SRCPV}"

inherit module

SRC_URI = " \
    git://github.com/ubx/easy_mXT224S_i2c.git;protocol=git;branch=master;tag=x1.0.1 \
    "

S = "${WORKDIR}/git"

export KERNELDIR="${KERNEL_SRC}"