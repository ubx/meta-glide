# In order to add overrides, we need to do it later then in the layers.conf
# the only standard way is using an inherit, which requires a bbclass

# Add the necessary override
TOOLCHAINOVERRIDES = ":toolchain-${TOOLCHAIN}"
TOOLCHAINOVERRIDES[vardepsexclude] = "TOOLCHAIN"

OVERRIDES .= "${TOOLCHAINOVERRIDES}"
OVERRIDES[vardepsexclude] += "TOOLCHAINOVERRIDES"

require conf/tc-${SECONDARYTC}.conf
