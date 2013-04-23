import config
import serial

class Xbee(object):
    """An xbee device"""
    def __init__(self, serialPortName='/dev/ttyAMA0', baudRate=115200):
        self._xbeeSerial = serial.Serial(config.xbeeSerialport, baudRate)

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '\n')
