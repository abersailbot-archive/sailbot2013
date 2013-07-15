import config
import serial
import threading

class Xbee(threading.Thread):
    """An xbee device"""
    def __init__(self, serialPortName=None, baudRate=None):
        if baudRate is None:
            baudRate = config.xbeeBaudRate
        if serialPortName is None:
            serialPortName = config.xbeeSerialport

        self._xbeeSerial = serial.Serial(serialPortName, baudRate)

    def run(self):
        while True:
            commandChar = self.recieve()
            if commandChar is 'l':
                self.send(self.logs)

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '$')

    def recieve(self):
        """Recieve a message from the xbee"""
        return self._xbeeSerial.read(1)
        
    def update_log(self, log):
        """Updates the stored log"""
        self.logs = log
