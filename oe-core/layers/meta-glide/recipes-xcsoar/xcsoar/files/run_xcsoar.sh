#!/bin/sh

### start can device
ip link set can0 type can bitrate 500000
ip link set can0 txqueuelen 1000
ip link set can0 up

echo "Run X, rotate -90 and start XCSoar"
X -nocursor &
sleep 1

export DISPLAY=:0
xrandr -o left

### run xcsoar
sts=-1
while [ $sts -ne 0 ]
do
    /opt/XCSoar/bin/xcsoar -portrait -fly -datapath=/home/root/.xcsoar/
    echo $?
    sts=$?
    ### echo Keep running on crash
    sleep 1
done

### shutdown and power off
fbi -vt 1 /home/root/xcsoar-640x480-shutdown.ppm
sleep 3
shutdown now
