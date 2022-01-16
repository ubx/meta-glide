# Implement blacklist/whitelist functionality for alternative toolchains
# Based on the oe-core blacklist bclass
#
# To add a blacklisted package:
#   TCBLACKLIST[pn] = 'toolchain'
#
# To blacklist everything, and whitelist a package:
#   TCBLACKLIST = 'toolchain'
#   TCWHITELIST[pn] = 'toolchain'

include conf/tc-${SECONDARYTC}-blacklist.conf

python () {
    print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
    tc = d.getVar('TOOLCHAIN', True)
    print('tc=', tc)
    pn = d.getVar('PN', True)
    print('pn=', pn)

    blacklist = d.getVarFlag('TCBLACKLIST', pn, True)
    print('blacklist0=' ,blacklist)

    if blacklist and blacklist == tc:
        raise bb.parse.SkipPackage("1111111111111 Recipe '%s' is blacklisted for the '%s' toolchain" % (pn, tc))

    blacklist = d.getVar('TCBLACKLIST', True)
    print('blacklist1=', blacklist)

    whitelist = d.getVarFlag('TCWHITELIST', pn, True)
    print('whitelist=', whitelist)
    if whitelist:
        print("whitelistwhitelistwhitelistwhitelistwhitelistwhitelist")

    if blacklist and (not whitelist or whitelist != tc):
        print("22222222222222222222222222222222222")
        raise bb.parse.SkipPackage("2222222222222 Recipe '%s' is blacklisted for the '%s' toolchain" % (pn, tc))
}