SUMMARY = "Use a rotary encoder as an arrow control"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

inherit module

SRC_URI = "file://Makefile \
           file://arrowkey.c \
           file://displayl-power.c \
           file://COPYING \
          "
S = "${WORKDIR}"

export KERNELDIR="${KERNEL_SRC}"