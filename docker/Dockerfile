FROM ubuntu:18.04

RUN dpkg --add-architecture i386
RUN apt-get update
RUN apt-get install -y g++-5-multilib
RUN apt-get install -y curl dosfstools gawk g++-multilib gcc-multilib lib32z1-dev libcrypto++6:i386 libcrypto++-dev:i386 liblzo2-dev:i386 lzop libsdl1.2-dev libstdc++-5-dev:i386 libusb-1.0-0:i386 libusb-1.0-0-dev:i386 uuid-dev:i386 texinfo chrpath

RUN apt-get install -y cpio diffstat wget

RUN apt-get install -y python git

RUN apt-get install -y locales vim quilt imagemagick ffmpeg mtools parted sudo

RUN useradd -ms /bin/bash  yocto && \
    /usr/sbin/locale-gen en_US.UTF-8
RUN adduser yocto sudo
RUN echo 'yocto:blub' | chpasswd

USER yocto
RUN mkdir ~/bin
RUN curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
RUN chmod a+x ~/bin/repo
## todo -- why not just "apt-get install -y repo"

ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8

ENV PATH="~/bin:${PATH}"

WORKDIR /home/yocto/oe-core
CMD /bin/bash

