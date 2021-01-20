#### Build the docker image & run it:

 ``````docker build -f Dockerfile . -t toradex-yotco``````
 
 ``````./run_dock.sh``````

#### Get the toradex configurations (in the docker 
image at /home/yocto/oe-core, this is the mounted folder if you run with ``````./run_dock.sh``````):

``````
git config --global user.email "you@example.com"
git config --global user.name "Your Name"
repo init -u http://git.toradex.com/toradex-bsp-platform.git -b LinuxImageV2.8
``````

#### Update/fetch the repos:

``````repo sync``````

#### Setup the bitbake environemnt:

``````. export``````

#### Now one can build the toradex colibri t30 image:

``````bitbake console-tdx-image``````

or for colibri t20 image:

`MACHINE="colibri-t20" bitbake console-tdx-image`

or build only xcsoar:

``````bitbake xcsoar-7.0``````
 
>This is all heavily inspired / copy and pasted from:
> 1. <https://developer.toradex.com/knowledge-base/board-support-package/openembedded-(core)>
> 2. the webinars found within the above page
> 3. <https://github.com/Openvario/meta-openvario>


#### Then you can populate the sdk, so one can cross-compile by hand (I think):

``````bitbake angstrom-lxde-image -c populate_sdk``````

You can now also generate the SD Card to deploy onto the board:
1. unpack the generated image found here (as sudo to maintain the folder structure)
  $BASEDIR/oe-core/deploy/images/colibri-t30/
2. go to the directory you unpacked it and run the update script:

``````./update.sh``````

Or if your target HW has an Ethernet connection, and you have an TFTP Server on the development machine,
you can deploy the generated image via TFTP:
1. unpack the generated image found here (as sudo to maintain the folder structure)
   $BASEDIR/oe-core/deploy/images/colibri-t30/
2. go to the directory you unpacked it and run the update script:
``````
./update.sh -o /tftp
``````
where "/tftp" is your TFTP_DIRECTORY.

3. On the target's HW-console do:
``````
usb start
setenv autoload 0
dhcp
setenv serverip <server-ip address> (setenv serverip 192.168.1.104)
ping ${serverip}
run setupdate
run update
``````




