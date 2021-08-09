DESCRIPTION = "Urwid is a console user interface library for Python"
SECTION = "devel/python"
LICENSE = "LGPLv2.1+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

PYPI_PACKAGE = "urwid"
PYPI_PACKAGE_EXT = "tar.gz"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "f7f4e6bed9ba38965dbd619520f39287"
SRC_URI[sha256sum] = "588bee9c1cb208d0906a9f73c613d2bd32c3ed3702012f51efe318a3f2127eae"

inherit pypi