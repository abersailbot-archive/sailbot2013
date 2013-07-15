import config
import serial

class Xbee(object):
    """An xbee device"""
    def __init__(self, serialPortName='/dev/ttyAMA0', baudRate=None):
        if baudRate is None:
            baudRate = config.xbeeBaudRate
        self._xbeeSerial = serial.Serial(config.xbeeSerialport, baudRate)

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '$')

    def recieve(self):
        """Recieve a message from the xbee"""
        return self._xbeeSerial.read(1)
        
