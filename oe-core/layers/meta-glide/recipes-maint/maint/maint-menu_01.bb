# Copyright (C) 2021 Andreas Lüthi <andreas.luethi@gmx.net>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "meta-glide maintenance menu"
HOMEPAGE = "www.xcsoar.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
SECTION = "base/app"
PR = "r1"

RDEPENDS_${PN} = "\
        xterm \
		font-misc-misc \
		rsync \
"

S = "${WORKDIR}"

SRC_URI = " \
	file://menu.py \
	file://run_canlogger.sh \
	file://save_journalctl.sh \
"

## do nothing
do_compile() {
}

## avoid "package maint-menu requires /usr/bin/python3"
do_package_qa() {
}

do_install() {
	install -d ${D}/home/root
	install -m u+x ${WORKDIR}/menu.py ${D}/home/root/menu.py
	install -m u+x ${WORKDIR}/run_canlogger.sh ${D}/home/root/run_canlogger.sh
	install -m u+x ${WORKDIR}/save_journalctl.sh ${D}/home/root/save_journalctl.sh
}

PACKAGES = "${PN}"
FILES_${PN} = " \
	/home/root/menu.py \
	/home/root/run_canlogger.sh \
	/home/root/save_journalctl.sh \
"
