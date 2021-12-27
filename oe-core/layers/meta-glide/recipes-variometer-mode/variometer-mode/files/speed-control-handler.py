#!/usr/bin/python3
import selectors
import evdev
from struct import pack
import can

SPEED_COMMAND_ID = 1510
NODE_ID = 49
DATA_TYPE = 10
SERVICE_CODE = 0
MSG_CODE_IDX_FIRST = 0
VARIO = 0
SPEED = 1

MSG_CODE_IDX = 3
DATA_IDX = 4

EVENT_KBD = '/dev/input/by-id/usb-Arduino_LLC_Arduino_Leonardo_HIDFG-if02-event-mouse'

def main():
    selector = selectors.DefaultSelector()
    keybd = evdev.InputDevice(EVENT_KBD)
    selector.register(keybd, selectors.EVENT_READ)

    canas_data = bytearray(pack('BBBBB', NODE_ID, DATA_TYPE, SERVICE_CODE, MSG_CODE_IDX_FIRST, VARIO))

    bus = can.interface.Bus(bustype='socketcan', channel='can0', bitrate=500000)
    msg = can.Message(arbitration_id=SPEED_COMMAND_ID, data=canas_data, is_extended_id=False)

    while True:
        for key, mask in selector.select():
            device = key.fileobj
            for event in device.read():
                cmd = None
                if event.type == 1 and event.value == 0:
                    if event.code == 31:
                        cmd = VARIO
                    elif event.code == 47:
                        cmd = SPEED
                if cmd is not None:
                    canas_data[DATA_IDX] = cmd
                    if canas_data[MSG_CODE_IDX] == 255:
                        canas_data[MSG_CODE_IDX] = 0
                    else:
                        canas_data[MSG_CODE_IDX] += 1
                    try:
                        bus.send(msg)
                    except can.CanError:
                        pass

if __name__ == '__main__':
    main()
