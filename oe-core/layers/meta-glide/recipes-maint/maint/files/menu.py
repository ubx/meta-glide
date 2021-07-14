#!/usr/bin/python3
import urwid
import subprocess
import os
import shutil
import sys
from collections import OrderedDict


def get_usb_devices():
    with open("/proc/partitions", "r") as f:
        devices = []

        for line in f.readlines()[2:]:
            words = [word.strip() for word in line.split()]
            minor_number = int(words[1])
            device_name = words[3]

            if (minor_number % 16) == 0:
                path = "/sys/class/block/" + device_name

                if os.path.islink(path):
                    if os.path.realpath(path).find("/usb") > 0:
                        devices.append("/dev/" + device_name)

        return devices


def get_usb_mount(dev):
    mount = None
    with open("/proc/mounts", "r") as fin:
        for line in fin:
            if dev in line:
                mount = line.split(' ')[1]
                break
    return mount


class Menu:
    def __init__(self):
        self.menu_items = OrderedDict([('Start XCSoar', self.start_xcsoar),
                                       ('Shutdown', self.shutdown)])

        self.menu_items_usb = OrderedDict([('Update XCSoar', self.update_xcsoar),
                                           ('Sync to USB-Stick', self.sync_to_usb_stick)])

        self.title = 'Glide Menu'
        self.view = None
        self.loop = None
        self.main_menu = None

        usb_dev = get_usb_devices()
        if usb_dev:
            self.usb_device = usb_dev[0]
        else:
            self.usb_device = None
        self.usb_present = any(get_usb_devices())

    def creat_view(self):
        body = [urwid.Text(self.title), urwid.Divider()]

        menuit = self.menu_items.copy()
        if self.usb_present is True:
            menuit.update(self.menu_items_usb)

        for desc, callb in menuit.items():
            button = urwid.Button(desc)
            urwid.connect_signal(button, 'click', callb)
            body.append(urwid.AttrMap(button, None, focus_map='reversed'))

        menu_list = urwid.ListBox(urwid.SimpleFocusListWalker(body))
        self.main_menu = urwid.Padding(menu_list, left=2, right=2)

        top = urwid.Overlay(self.main_menu, urwid.SolidFill(u'\N{MEDIUM SHADE}'),
                            align='center', width=('relative', 80),
                            valign='middle', height=('relative', 80),
                            min_width=20, min_height=9)
        self.view = top

    def main(self):
        self.creat_view()
        self.loop = urwid.MainLoop(
            self.view, palette=[('body', 'dark cyan', '')], )
        self.loop.set_alarm_in(1, self.check_usb)
        self.loop.run()

    def check_usb(self, loop=None, data=None):
        if self.usb_present != any(get_usb_devices()):
            self.usb_present = not self.usb_present
            self.creat_view()
            self.loop.widget = self.view

        self.loop.set_alarm_in(1, self.check_usb)

    def start_xcsoar(self, args):
        self.loop.screen.stop()
        with open(os.devnull, 'w') as fp:
            subprocess.run(
                ['/opt/XCSoar/bin/xcsoar', '-portrait', '-fly', '-datapath=' + sys.argv[1]],
                shell=False, stdout=fp, stderr=fp)
        sys.exit()

    def shutdown(self, args):
        sys.exit()

    def update_xcsoar(self, args):
        dev = self.usb_device.split('/')[-1]
        mount_point = get_usb_mount(dev)
        shutil.copyfile(mount_point + '/xcsoar', '/opt/XCSoar/bin/xcsoar')

    def sync_to_usb_stick(self, args):
        dev = self.usb_device.split('/')[-1]
        mount_point = get_usb_mount(dev)
        with open(os.devnull, 'w') as fp:
            subprocess.run(
                ['rsync', '-a', sys.argv[1], mount_point],
                shell=False, stdout=fp, stderr=fp)
        sys.exit()

if __name__ == '__main__':
    menu = Menu()
    menu.main()
