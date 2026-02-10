# Update to XCSoar Version 7.44-Linux

> [!CAUTION]
> This XCSoar version is a development snapshot!

Updating the statically linked XCSoar build for Display-L.
This XCSoar build runs directly on the framebuffer and does not require an X11 server.

[See XCSoar source for this version](https://github.com/ubx/XCSoar/blob/596ec2463031ff4428ae1344ff3cf8f2a2a57f4e/)

Update Procedure

1. Start the Display-L and ensure it is connected to the internet.
2. Do not start XCSoar!
3. Copy all files from this directory to the Display-L:
```
scp menu.py root@colibri-t20:/home/root/
scp run_xcsoar.sh root@colibri-t20:/home/root/
scp xcsoar root@colibri-t20:/opt/XCSoar/bin/
```
or use tftp to copy the files:
```
tftp -g -r menu.py colibri-t20:/home/root/
tftp -g -r run_xcsoar.sh colibri-t20:/home/root/
tftp -g -r xcsoar colibri-t20:/opt/XCSoar/bin/
```
4. Login with ssh root@colibri-t20
5. On the running system do:
```
systemctl disable getty@tty1.service
systemctl disable ofono.service
reboot
```

## todo
- cleanup menu.py
- automate update procedure
