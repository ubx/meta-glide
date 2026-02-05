# Update to XCSoar Version 7.44-Linux

Updating the statically linked XCSoar build for Display-L.
This XCSoar build runs directly on the framebuffer and does not require an X11 server.

[See XCSoar source for this version](https://github.com/ubx/XCSoar/blob/596ec2463031ff4428ae1344ff3cf8f2a2a57f4e/)

Update Procedure

1. Start the Display-L device and ensure it is connected to the internet.
2. Copy all files from this directory to the Display-L system.
```
sftp menu.py root@colibri-t20:/home/root/
sftp run_xcsoar.sh root@colibri-t20:/home/root/
sftp xcsoar root@colibri-t20:/opt/XCSoar/bin/xcsoar
```
3. Restart Display-L

## todo
- cleanup menu.py
- automate update procedure