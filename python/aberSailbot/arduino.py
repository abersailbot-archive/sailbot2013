import serial
from threading import Lock

class ArduinoDevice(object):
    """
    A device attached to an Arduino. The protocol is to send a single character
    representing the device ID, then another (if necessary) to specify what
    information should be retrieved.
    For example:

    ArduinoDevice('G').request('r', 10) -> Gr(10)
    ArduinoDevice('C').request('h') -> Ch

    The Arduino then returns a single line response. The line may be empty
    """
    def __init__(self, id, arduino=None):
        """Construct a device with a given id"""
        self.arduino = arduino or Arduino.get()
        self.id = id

    def request(self, thing='', *args):
        """Request something from the arduino"""
        command = self.id + thing
        if args:
            command += '(' + ','.join(str(r) for r in args) + ')'

        return self.arduino.sendCommand(command)


class Arduino(object):
    """The arduino itself, and basic communications with it"""
    def __init__(self, port):
        try:
            self.port = serial.Serial(port)
            self.port.open()
            self._lock = Lock()
        except Exception:
            raise Exception('Cannot connect to arduino on %s' % port)


    def sendCommand(self, c):
        """
        Send a short command, and return a single line response. Prevents
        other threads interweaving requests by locking on self._lock
        """
        with self._lock:
            self.port.flushInput()
            self.port.write(c)
            return self.port.readline()

    _mainArduino = None
            
    @classmethod
    def get(cls):
        """
        A lazy singleton, using the default port. This should be used instead of
        calling the constructor, to prevent the serial port being opened twice
        """
        if not cls._mainArduino:
            _mainArduino = cls('/dev/ttyUSB0')
        return _mainArduino 
