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
                    c = self.xbee.read(1)
                    if c:
                        print c
                    if c == '$':
                        print 'appending'
                        if len(line) > 0:
                            self.queue.append(line)
                            line = ''
                    else:
                        line += c
                time.sleep(0.01)

        def stop(self):
            self._stop = True

    def __init__(self, serialPortName='/dev/ttyAMA0', baudRate=None):
        if baudRate is None:
            baudRate = config.xbeeBaudRate
        self._xbeeSerial = serial.Serial(config.xbeeSerialport, baudRate, timeout=0.1)
        self.queue = []
        self.threadLock = threading.Lock()
        self.thread = self.ReadLines(self._xbeeSerial, self.queue, self.threadLock)
        print 'starting thread'
        self.thread.start()
        print 'thread started'

    def send(self, message):
        """Send a message to the xbee"""
        self._xbeeSerial.write(message + '$')
