# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f4b026880834eb01c035c5e5cb47ccac"

BB_DONT_STRICTLY_TRACK_COMPATIBLE_VERSIONS = "1"

SRC_URI = "\
    https://github.com/c-ares/c-ares/archive/refs/tags/cares-1_13_0.tar.gz;striplevel=2;subdir=git \
    file://cmake-install-libcares.pc.patch \
"
SRC_URI[md5sum] = "cdb21052a7eb85261da22f83c0654cfd"
SRC_URI[sha256sum] = "7c48c57706a38691041920e705d2a04426ad9c68d40edd600685323f214b2d57"

S = "${WORKDIR}/git/c-ares-cares-1_13_0"

inherit cmake pkgconfig

PACKAGES =+ "${PN}-utils"

FILES_${PN}-dev += "${libdir}/cmake"
FILES_${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native"