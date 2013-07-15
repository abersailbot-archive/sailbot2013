import config
import serial
import threading

class Xbee(threading.Thread):
    """An xbee device"""
    def __init__(self, serialPortName='/dev/ttyAMA0', baudRate=None):
        if baudRate is None:
            baudRate = config.xbeeBaudRate
        self._xbeeSerial = serial.Serial(config.xbeeSerialport, baudRate)

    def run(self):
        while True:
            commandChar = recieve()
            if commandChar is 'l':
                send(self.logs)

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '$')

    def recieve(self):
        """Recieve a message from the xbee"""
        return self._xbeeSerial.read(1)
        
    def updateLog(self, log):
        """Updates the stored log"""
        self.logs = log
