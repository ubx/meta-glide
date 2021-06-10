#!/bin/sh

### Synchronize cached writes to persistent storage
sync

### Tell the ATtiny to power off the hardware.
i2cset -y 0 0x10 0x01 # POF
