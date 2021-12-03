SUMMARY = "Python3 Package Kivy"
inherit pypi setuptools3

DEPENDS = "python3-cython-native mesa libsdl2 libsdl2-image libsdl2-ttf libsdl2-mixer"

PYPI_PACKAGE = "Kivy"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=70245b0153f62e27619400a6e88e1263"

SRC_URI[sha256sum] = "7ce9e88b75de47a3f1d52cbe6924c18cafc83fa102e54f6794d241746e93fdff"

RDEPENDS_${PN} = "mesa \
                  libsdl2 \
                  libsdl2-image \
                  libsdl2-ttf \
                  libsdl2-mixer \
"