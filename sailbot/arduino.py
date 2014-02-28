import serial
from threading import Lock
import config
import time

class Arduino(object):
    """The arduino and basic communications with devices attached to it"""
    def __init__(self, port=None):
        try:
            if port is None:
                #use the port defined in the config file
                port = config.arduinoSerialport
            self.port = serial.Serial(port)
            self.port.open()
            self._lock = Lock()
        except Exception:
            raise Exception('Cannot connect to arduino on %s' % port)
        time.sleep(2)
        self.rudderAngle = 0
        self.sailAngle = 0

    def __sendCommand(self, c):
        """
        Send a short command, and return a single line response. Prevents
        other threads interweaving requests by locking on self._lock
        """
        with self._lock:
            self.port.flushInput()
            self.port.write(c + '\n')
            return self.port.readline()

    def get_wind(self):
        """Get the angle from the wind sensor relative to the boat"""
        return int(self.__sendCommand('w'))

    def get_compass(self):
        """Get the heading from the compass"""
        return float(self.__sendCommand('c'))

    def get_thermometer(self):
        """Get the temperature in degrees Celsius"""
        return float(self.__sendCommand('t'))

    def set_rudder(self, angle):
        """Set the rudder servo to a value between 1060 and 1920"""
        self.rudderAngle = angle
        angle = ((angle-135)*(430/45))+1060
        if angle < 1060:
            angle = 1060
        elif angle > 1920:
            angle = 1920
        self.__sendCommand('r%03d' % angle)

    def set_sail(self, angle):
        """Set the sail servo to a value between 1050 and 1930"""
        if angle < 180:
            offset = angle
        elif angle >= 180:
            offset = 360 - angle
        self.sailAngle = offset
        offset = ((offset)*(880/72))+1050
        self.__sendCommand('s%03d' % offset)

    def calibrate_wind_sensor(self):
        """
        Send a message to the arduino to save the offset of the wind sensor
        """
        return self.__sendCommand('o').startswith('1')

if __name__ == '__main__':
    import time
    a = Arduino() #create a test device on the arduino
    time.sleep(2)
    print a.set_rudder(0)
