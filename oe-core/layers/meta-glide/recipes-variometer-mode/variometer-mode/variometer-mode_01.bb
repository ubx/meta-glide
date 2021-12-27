# Copyright (C) 2021 Andreas Lüthi <andreas.luethi@gmx.net>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "meta-glide handle SteFly Remote-Knüppelgriff speed command switch"
HOMEPAGE = "www.xcsoar.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
SECTION = "base/app"
PR = "r1"

RDEPENDS_${PN} = "\
        python3-can \
		python3-aenum \
		python3-evdev \
		python3-setuptools \
"

S = "${WORKDIR}"

SRC_URI = " \
	file://speed-control-handler.py \
"

## do nothing
do_compile() {
}

## avoid "package speed-control-handler /usr/bin/python3"
##do_package_qa() {
##}

do_install() {
	install -d ${D}/home/root
	install -m u+x ${WORKDIR}/speed-control-handler.py ${D}/home/root/
}

PACKAGES = "${PN}"
FILES_${PN} = " \
	/home/root/speed-control-handler.py \
"
