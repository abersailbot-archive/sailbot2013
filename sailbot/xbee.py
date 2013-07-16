import config
import serial
import threading

class Xbee():
    """An xbee device"""
    def __init__(self, serialPortName=None, baudRate=None):
        if baudRate is None:
            baudRate = config.xbeeBaudRate
        if serialPortName is None:
            serialPortName = config.xbeeSerialport

        self._xbeeSerial = serial.Serial(serialPortName, baudRate)
	_xbeeThread = XbeeThread()
	XbeeThread.start()

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '$')

    def recieve(self):
        """Recieve a message from the xbee"""
        return self._xbeeSerial.read(1)
        
    def update_log(self, log):
        """Updates the stored log"""
	    with XbeeThread.logLock:
            XbeeThread.logs = log

class XbeeThread(threading.Thread):
    def run(self):
        logLock = threading.Lock()
        while True:
            commandChar = self.recieve()
            if commandChar is 'l':
                with logLock:
                    Xbee.send(self.logs)

