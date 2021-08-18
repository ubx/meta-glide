### Start CAN Logger

LOG_MEDIA=$1

if  mount | grep $LOG_MEDIA > /dev/null; then
   DATAPATH=$LOG_MEDIA/canlogs
   if [ -d $DATAPATH ]; then
      if ! pgrep -x "candump" > /dev/null; then
         cd $DATAPATH
         candump -l can0 &
         echo "*** CAN Logger started"
     fi
   fi
fi
