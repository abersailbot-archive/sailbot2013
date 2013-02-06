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
            print 'I sent "%s"' % c
            self.port.flushInput()
            self.port.write(c + '\n')
            return self.port.readline()

    def getCompass(self):
        """
        Get the heading from the compass
        """
        return self.__sendCommand('c')

    def setRudder(self, angle):
        """
        Set the rudder servo to an angle between 0 and 255
        """
        return self.__sendCommand('r%03d' % angle)

    def setSail(self, angle):
        """
        Set the sail servo to an angle between 0 and 255
        """
        return self.__sendCommand('s%03d' % angle)

if __name__ == '__main__':
    import time
    a = Arduino() #create a test device on the arduino
    time.sleep(2)
    print a.setRudder(0)
