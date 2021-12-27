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

### If inserted, copy XCSoar data to the sd card
SD_MEDIA=/media/mmcblk0p2
DATAPATH=/home/root/.xcsoar

if mount | grep $SD_MEDIA > /dev/null; then
   size=$(df -PT $SD_MEDIA | awk 'NR==2 {print int($5)}') 
   if [ $size -gt 4000000 ];                               
   then
      echo "*** Use SD Card for XCSoar Data"
      DATAPATH=$SD_MEDIA/xcsoar-data
      if [ ! -d $DATAPATH ]; then
        mkdir $DATAPATH
        echo "*** Coyp data to $DATAPATH"
        cp -r /home/root/.xcsoar/* $DATAPATH
      fi
   else
      echo "*** Use .xcsoar foe XCSoar Data "
   fi
fi

### run SteFly Remote-Knüppelgriff speed command switch handler
/home/root/speed-control-handler.py &

### run menu/xcsoar
xterm -geometry 48x30+0+0 -fn -misc-fixed-medium-r-normal--20-*-*-*-*-*-*-* -e /home/root/menu.py $DATAPATH
sleep 3
fbi -vt 1 -noverbose /home/root/xcsoar-640x480-shutdown.ppm
sleep 3

### shutdown and power off
sync
poweroff
