# Copyright (C) 2014 Unknow User <unknow@user.org>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "XCSoar glide computer"
HOMEPAGE = "www.xcsoar.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
SECTION = "base/app"
PR = "r50"

DEPENDS = "	\
		gcc \
		boost \
		curlpp \
		pkgconfig \
		libxslt-native \
		librsvg-native \
		imagemagick \
		imagemagick-native \
		libinput \
		libsdl2 \
		lua \
		udev \
		ttf-dejavu \
		jpeg \
		freetype \
        glm \
        virtual/egl \
        virtual/mesa \
        virtual/libgles1 \
        virtual/libgles2 \
        alsa-lib \
        curlpp \
        libsocketcan \
        libsodium \
        c-ares \
"

RDEPENDS_${PN} = "\
		ttf-dejavu-sans-condensed \
		xrandr \
		fbida \
		xinput \
		xinput-calibrator \
		unclutter \
"

S = "${WORKDIR}/git"

LC_LOCALE_PATH = "/usr/share/locale"

BB_STRICT_CHECKSUM = "0"

SRC_URI = " \
	git://github.com/ubx/XCSoar.git;protocol=git;branch=can-bus-gcc8;tag=t20-v7.13_rc02 \
    file://0005-Adapted-toolchain-prefixes-for-cross-compile.patch \
	file://0001_no_version_lua.patch \
	file://0001-avoid-tail-cut.patch \
	file://0001-Increase-refresh-intervall.patch \
	file://0008-Zoom-in-out.patch \
	file://0009-Max-alternates.patch \
	file://0010-Enable-touch-screen.patch \
	file://xcsoar.service \
	file://run_xcsoar.sh \
	file://xcsoar-640x480-shutdown.ppm \
    file://init.lua \
    file://keys.lua \
    https://www.flarmnet.org/static/files/wfn/data.fln \
    file://default.prf \
    file://user1.prf \
    file://Switzerland.cup \
    file://Switzerland_Airspace.txt \
    http://download.xcsoar.org/maps/ALPS_HighRes.xcm;name=alpsmap \
"

SRC_URI[alpsmap.md5sum] = "1216ad8222dad27024f77e799ceb9b67"
SRC_URI[alpsmap.sha256sum] = "851ec5a90fb4b32b991b79293faaf087419c0d790c44aa202cb12a726fbf9c8f"

inherit pkgconfig update-alternatives

inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"

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
	make -j$(nproc) TARGET=UNIX OPENGL=n ENABLE_SDL=y USE_SDL2=y GEOTIFF=n WERROR=n
}


do_install() {
	echo "Installing ..."
	install -d ${D}/opt/XCSoar/bin
	install -m 0755 ${S}/output/UNIX/bin/xcsoar ${D}/opt/XCSoar/bin
	install -m 0755 ${S}/output/UNIX/bin/vali-xcs ${D}/opt/XCSoar/bin

	install -d ${D}/home/root
	install -m u+x ${WORKDIR}/run_xcsoar.sh ${D}/home/root/run_xcsoar.sh
	install -m u+x ${WORKDIR}/xcsoar-640x480-shutdown.ppm ${D}/home/root/xcsoar-640x480-shutdown.ppm
	install -d ${D}${systemd_system_unitdir}
	install -m 644 ${WORKDIR}/xcsoar.service ${D}${systemd_system_unitdir}/xcsoar.service

    ## todo -- move to another layer
	install -d ${D}/home/root/.xcsoar/
	install -m 0755 ${WORKDIR}/data.fln ${D}/home/root/.xcsoar/data.fln
	install -m 0755 ${WORKDIR}/default.prf ${D}/home/root/.xcsoar/default.prf
	install -m 0755 ${WORKDIR}/user1.prf ${D}/home/root/.xcsoar/user1.prf
	install -m 0755 ${WORKDIR}/Switzerland.cup ${D}/home/root/.xcsoar/Switzerland.cup
	install -m 0755 ${WORKDIR}/Switzerland_Airspace.txt ${D}/home/root/.xcsoar/Switzerland_Airspace.txt
	install -m 0755 ${WORKDIR}/ALPS_HighRes.xcm ${D}/home/root/.xcsoar/ALPS_HighRes.xcm
	install -d ${D}/home/root/.xcsoar/lua/
	install -m 0755 ${WORKDIR}/init.lua ${D}/home/root/.xcsoar/lua/init.lua
	install -m 0755 ${WORKDIR}/keys.lua ${D}/home/root/.xcsoar/lua/keys.lua

	install -d ${D}${LC_LOCALE_PATH}/de/LC_MESSAGES
	install -m 0755 ${S}/output/po/de.mo ${D}${LC_LOCALE_PATH}/de/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/cs/LC_MESSAGES
	install -m 0755 ${S}/output/po/cs.mo ${D}${LC_LOCALE_PATH}/cs/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/da/LC_MESSAGES
	install -m 0755 ${S}/output/po/da.mo ${D}${LC_LOCALE_PATH}/da/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/el/LC_MESSAGES
	install -m 0755 ${S}/output/po/el.mo ${D}${LC_LOCALE_PATH}/el/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/es/LC_MESSAGES
	install -m 0755 ${S}/output/po/es.mo ${D}${LC_LOCALE_PATH}/es/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/fr/LC_MESSAGES
	install -m 0755 ${S}/output/po/fr.mo ${D}${LC_LOCALE_PATH}/fr/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/he/LC_MESSAGES
	install -m 0755 ${S}/output/po/he.mo ${D}${LC_LOCALE_PATH}/he/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/hr/LC_MESSAGES
	install -m 0755 ${S}/output/po/hr.mo ${D}${LC_LOCALE_PATH}/hr/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/hu/LC_MESSAGES
	install -m 0755 ${S}/output/po/hu.mo ${D}${LC_LOCALE_PATH}/hu/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/it/LC_MESSAGES
	install -m 0755 ${S}/output/po/it.mo ${D}${LC_LOCALE_PATH}/it/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/ja/LC_MESSAGES
	install -m 0755 ${S}/output/po/ja.mo ${D}${LC_LOCALE_PATH}/ja/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/ko/LC_MESSAGES
	install -m 0755 ${S}/output/po/ko.mo ${D}${LC_LOCALE_PATH}/ko/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/lt/LC_MESSAGES
	install -m 0755 ${S}/output/po/lt.mo ${D}${LC_LOCALE_PATH}/lt/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/nb/LC_MESSAGES
	install -m 0755 ${S}/output/po/nb.mo ${D}${LC_LOCALE_PATH}/nb/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/nl/LC_MESSAGES
	install -m 0755 ${S}/output/po/nl.mo ${D}${LC_LOCALE_PATH}/nl/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/pl/LC_MESSAGES
	install -m 0755 ${S}/output/po/pl.mo ${D}${LC_LOCALE_PATH}/pl/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/pt/LC_MESSAGES
	install -m 0755 ${S}/output/po/pt.mo ${D}${LC_LOCALE_PATH}/pt/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/ro/LC_MESSAGES
	install -m 0755 ${S}/output/po/ro.mo ${D}${LC_LOCALE_PATH}/ro/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/ru/LC_MESSAGES
	install -m 0755 ${S}/output/po/ru.mo ${D}${LC_LOCALE_PATH}/ru/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/sk/LC_MESSAGES
	install -m 0755 ${S}/output/po/sk.mo ${D}${LC_LOCALE_PATH}/sk/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/sl/LC_MESSAGES
	install -m 0755 ${S}/output/po/sl.mo ${D}${LC_LOCALE_PATH}/sl/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/sr/LC_MESSAGES
	install -m 0755 ${S}/output/po/sr.mo ${D}${LC_LOCALE_PATH}/sr/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/sv/LC_MESSAGES
	install -m 0755 ${S}/output/po/sv.mo ${D}${LC_LOCALE_PATH}/sv/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/tr/LC_MESSAGES
	install -m 0755 ${S}/output/po/tr.mo ${D}${LC_LOCALE_PATH}/tr/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/uk/LC_MESSAGES
	install -m 0755 ${S}/output/po/uk.mo ${D}${LC_LOCALE_PATH}/uk/LC_MESSAGES/xcsoar.mo
	install -d ${D}${LC_LOCALE_PATH}/vi/LC_MESSAGES
	install -m 0755 ${S}/output/po/vi.mo ${D}${LC_LOCALE_PATH}/vi/LC_MESSAGES/xcsoar.mo
}

FILES_${PN} = " \
	/opt/XCSoar/bin/xcsoar \
	/opt/XCSoar/bin/vali-xcs \
	/opt/conf/default/ov-xcsoar.conf \
	/opt/conf/ov-xcsoar.conf \
	${LC_LOCALE_PATH}/de/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/cs/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/da/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/el/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/es/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/fr/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/he/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/hr/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/hu/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/it/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/ja/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/ko/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/lt/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/nb/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/nl/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/pl/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/pt/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/ro/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/ru/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/sk/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/sl/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/sr/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/sv/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/tr/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/uk/LC_MESSAGES/xcsoar.mo \
	${LC_LOCALE_PATH}/vi/LC_MESSAGES/xcsoar.mo \
"

FILES_${PN} += " \
	/home/root/run_xcsoar.sh \
	/home/root/poweroff.sh \
	${systemd_system_unitdir}/xcsoar.service \
	/home/root/xcsoar-640x480-shutdown.ppm \
"

FILES_${PN} += " \
	/home/root/.xcsoar/lua/init.lua \
	/home/root/.xcsoar/lua/keys.lua \
"
FILES_${PN} += " \
	/home/root/.xcsoar/data.fln \
	/home/root/.xcsoar/default.prf \
	/home/root/.xcsoar/user1.prf \
	/home/root/.xcsoar/Switzerland.cup \
	/home/root/.xcsoar/Switzerland_Airspace.txt \
	/home/root/.xcsoar/ALPS_HighRes.xcm \
"