# Copyright (C) 2014 Unknow User <unknow@user.org>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "XCSoar glide computer"
HOMEPAGE = "www.xcsoar.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=801f80980d171dd6425610833a22dbe6"
SECTION = "base/app"
PR = "r11"

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
		libsdl \
		lua \
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
        curlpp \
        libsocketcan \
        libsodium \
        c-ares \
"

RDEPENDS_${PN} = "\
		ttf-dejavu-sans-condensed \
"

S = "${WORKDIR}/git"

LC_LOCALE_PATH = "/usr/share/locale"

BB_STRICT_CHECKSUM = "0"

SRC_URI = " \
	git://github.com/ubx/XCSoar.git;protocol=git;branch=can-bus;tag=t30-test-25 \
	file://0005-Adapted-toolchain-prefixes-for-cross-compile.patch \
	file://0001-Adapted-Flags-for-compiler-and-linker-for-cross-comp.patch \
	file://0001-Disable-warnings-as-errors.patch \
	file://0001_no_version_lua.patch \
	file://0001-avoid-tail-cut.patch \
	file://0001-Increase-refresh-intervall.patch \
	https://www.flarmnet.org/static/files/wfn/data.fln \
	file://run_xcsoar.sh \
	file://xcsoar.service \
"

inherit pkgconfig update-alternatives

inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} = "xcsoar.service"

addtask do_package_write_ipk after do_package after do_install

do_compile() {
	echo $CC
	$CC --version
	echo "Create missing symlinks ..."
	echo '${STAGING_DIR_TARGET}'
	echo '${PATH}'
	export PATH=$PATH:/usr/bin
	echo '${PATH}'
	#ln -s ${STAGING_DIR_TARGET}/usr/bin/convert.im7 ${STAGING_DIR_TARGET}/usr/bin/convert
	export FONTCONFIG_PATH=/etc/fonts
	echo "Making .."
	echo '${WORKDIR}'
	cd ${WORKDIR}/git
	make -j$(nproc) DEBUG=y DEBUG_GLIBCXX=n USE_LIBINPUT=y GEOTIFF=n USE_FB=y OPENGL=n
	##  *** USE_FB and EGL are mutually exclusive
}


do_install() {
	echo "Installing ..."
	install -d ${D}/opt/XCSoar/bin
	install -m 0755 ${S}/output/UNIX/bin/xcsoar ${D}/opt/XCSoar/bin
	install -m 0755 ${S}/output/UNIX/bin/vali-xcs ${D}/opt/XCSoar/bin

	install -d ${D}/home/root
	install -m u+x ${WORKDIR}/run_xcsoar.sh ${D}/home/root/run_xcsoar.sh
	install -d ${D}${systemd_system_unitdir}
	install -m 644 ${WORKDIR}/xcsoar.service ${D}${systemd_system_unitdir}/xcsoar.service

	install -d ${D}/home/root/.xcsoar/
	install -m 0755 ${WORKDIR}/data.fln ${D}/home/root/.xcsoar/data.fln

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
	${systemd_system_unitdir}/xcsoar.service \
"
FILES_${PN} += " \
	/home/root/.xcsoar/data.fln \
"
