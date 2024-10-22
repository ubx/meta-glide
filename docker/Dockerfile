FROM ubuntu:18.04

RUN dpkg --add-architecture i386
RUN apt-get update

RUN apt-get install -y build-essential
RUN apt-get install -y checkinstall
RUN apt-get install -y libncursesw5-dev
RUN apt-get install -y libssl-dev
RUN apt-get install -y wget
RUN apt-get install -y libsqlite3-dev
RUN apt-get install -y libgdbm-dev
RUN apt-get install -y libc6-dev
RUN apt-get install -y libbz2-dev
RUN apt-get install -y libffi-dev

RUN apt-get install -y python

RUN apt-get install -y g++-multilib
RUN apt-get install -y curl
RUN apt-get install -y dosfstools
RUN apt-get install -y gawk g++-multilib
RUN apt-get install -y gcc-multilib
RUN apt-get install -y lib32z1-dev

####### libcrypto++6:i386 libcrypto++-dev:i386
##RUN apt-get install -y libcrypto++8t64
RUN apt-get install -y libcrypto++-dev

RUN apt-get install -y liblzo2-dev:i386
RUN apt-get install -y lzop libsdl1.2-dev
RUN apt-get install -y libstdc++-5-dev:i386
RUN apt-get install -y libusb-1.0-0:i386
RUN apt-get install -y libusb-1.0-0-dev:i386
RUN apt-get install -y uuid-dev:i386
RUN apt-get install -y texinfo
RUN apt-get install -y chrpath
RUN apt-get install -y cpio diffstat
RUN apt-get install -y git
RUN apt-get install -y locales vim nano quilt imagemagick ffmpeg mtools parted sudo
RUN apt-get install -y zstd
RUN apt-get install -y liblz4-tool
RUN apt-get install -y sox
RUN apt-get install -y gpg
RUN useradd -ms /bin/bash  yocto && /usr/sbin/locale-gen en_US.UTF-8
RUN adduser yocto sudo
RUN echo 'yocto:blub' | chpasswd

USER yocto
RUN mkdir ~/bin
RUN curl https://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
RUN chmod a+x ~/bin/repo
## todo -- why not just "apt-get install -y repo"

ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8

ENV PATH="~/bin:${PATH}"

WORKDIR /home/yocto/oe-core
CMD /bin/bash