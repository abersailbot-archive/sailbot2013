import config
import serial
import threading
import time

class Xbee(object):
    """An xbee device"""
    class ReadLines(threading.Thread):
        def __init__(self, xbee, queue, lock):
            threading.Thread.__init__(self)
            self._stop = False
            self.xbee = xbee
            self.queue = queue
            self.lock = lock
            
        def run(self):
            line = ''
            while not self._stop:
                with self.lock:
                    for c in self.xbee:
                        print 'got', c
                        if c == '$':
                            self.queue.append(line)
                            line = ''
                        else:
                            line += c
            time.sleep(0.01)

    def __init__(self, serialPortName='/dev/ttyAMA0', baudRate=None):
        if baudRate is None:
            baudRate = config.xbeeBaudRate
        self._xbeeSerial = serial.Serial(config.xbeeSerialport, baudRate)
        self.queue = []
        self.threadLock = threading.Lock()
        self._thread = self.ReadLines(self._xbeeSerial, self.queue,
                self.threadLock)
        self._thread.start()

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '$')
