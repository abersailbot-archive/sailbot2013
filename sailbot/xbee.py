import config
import serial

class Xbee(object):
    """An xbee device"""
    def __init__(self, serialPortName='/dev/USB0'):
        self._xbeeSerial = serial.Serial(config.xbeeSerialport)

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '\n')
