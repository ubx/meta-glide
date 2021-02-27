#!/bin/sh

### start can device
ip link set can0 type can bitrate 500000
ip link set can0 txqueuelen 1000
ip link set can0 up

### TEMP install kernel module
cd /media/sda1/
insmod arrowkey.ko
cp xrandr /usr/bin/
cp libXrandr.so.2 /usr/lib/

echo "Run X, rotate -90 and start XCSoar"
X -nocursor &
sleep 1

export DISPLAY=:0
xrandr -o left

### run xcsoar
/opt/XCSoar/bin/xcsoar -portrait -fly -datapath=/media/sda1/xcsoar/ -profile=/media/sda1/xcsoar/default.prf
