#!/bin/sh

### start can device
ip link set can0 type can bitrate 500000
ip link set can0 txqueuelen 1000
ip link set can0 up

echo 0 > /sys/class/graphics/fbcon/cursor_blink

if lsblk -o TRAN -nr | grep -q '^usb$'; then
  X &
  sleep 1

  export DISPLAY=:0
  xrandr -o left

  ### run menu
  xterm -geometry 48x30+0+0 -fn -misc-fixed-medium-r-normal--20-*-*-*-*-*-*-* -e /home/root/menu.py $DATAPATH
  pkill X
  sleep 2
fi

### run SteFly Remote-Knüppelgriff speed command switch handler
/home/root/speed-control-handler.py &

### run XCSoar
/opt/XCSoar/bin/xcsoar -datapath=/media/mmcblk0p2/xcsoar-data/ -fly

fbi -vt 1 -noverbose /home/root/xcsoar-640x480-shutdown.ppm
sleep 3

sync
poweroff
