# Copyright (C) 2014 Unknow User <unknow@user.org>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "XCSoar glide computer"
HOMEPAGE = "www.xcsoar.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
SECTION = "base/app"
PR="r11"

DEPENDS = " \
        gcc \
        boost \
        curlpp \
        pkgconfig \
        libxslt-native \
        librsvg-native \
        imagemagick-native \
        libinput \
        libsdl2 \
        udev \
        ttf-dejavu \
        jpeg \
        freetype \
        libpng \
        glm \
        virtual/egl \
        virtual/mesa \
        virtual/libgles1 \
        virtual/libgles2 \
        alsa-lib \
"

RDEPENDS_${PN} = "\
        ttf-dejavu-sans-condensed \
"

S = "${WORKDIR}/git"

LC_LOCALE_PATH = "/usr/share/locale"

SRC_URI = " \
    git://github.com/XCSoar/XCSoar.git;protocol=git;tag=v${PV} \
    file://0005-Adapted-toolchain-prefixes-for-cross-compile_6.8.patch \
    file://0001-Adapted-Flags-for-compiler-and-linker-for-cross-comp.patch \
    file://0001-Disable-warnings-as-errors.patch \
    file://0001-avoid-tail-cut.patch \
    file://0007-Disable-touch-screen-auto-detection-6.8.patch \
    file://ov-xcsoar.conf \
"

inherit pkgconfig update-alternatives

addtask do_package_write_ipk after do_package after do_install

do_compile() {
    echo $CC
    $CC --version
    echo "Create missing symlinks ..."
    echo '${STAGING_DIR_TARGET}'
    echo '${PATH}'
    export PATH=$PATH:/usr/bin
    echo '${PATH}'
    export FONTCONFIG_PATH=/etc/fonts
    echo "Making .."
    echo '${WORKDIR}'
    cd ${WORKDIR}/git
    make -j$(nproc) TARGET=UNIX OPENGL=n ENABLE_SDL=y USE_SDL2=y GEOTIFF=n
}

do_install() {
    echo "Installing ..."
    install -d ${D}/opt/XCSoar/bin
    install -m 0755 ${S}/output/UNIX/bin/xcsoar ${D}/opt/XCSoar/bin/xcsoar_${PV}
    install -m 0755 ${S}/output/UNIX/bin/vali-xcs ${D}/opt/XCSoar/bin/vali-xcs_${PV}
}

FILES_${PN} = " \
    /opt/XCSoar/bin/xcsoar_${PV} \
    /opt/XCSoar/bin/vali-xcs_${PV} \
"

