import serial
from threading import Lock

class Arduino(object):
    """The arduino and basic communications with devices attached to it"""
    def __init__(self, port='/dev/ttyACM0'):
        try:
            self.port = serial.Serial(port)
            self.port.open()
            self._lock = Lock()
        except Exception:
            raise Exception('Cannot connect to arduino on %s' % port)


    def __sendCommand(self, c):
        """
        Send a short command, and return a single line response. Prevents
        other threads interweaving requests by locking on self._lock
        """
        with self._lock:
            print 'I sent', c
            self.port.flushInput()
            self.port.write(c)
            return self.port.readline()

    def getCompass(self):
        return self.__sendCommand('c')

if __name__ == '__main__':
    import time
    a = Arduino() #create a test device on the arduino
    a.getCompass()

    #s=serial.Serial('/dev/ttyACM0')
    #s.open()
    #while True:
    #    s.write('aa')
    #    time.sleep(1)
    #    print s.readline()

