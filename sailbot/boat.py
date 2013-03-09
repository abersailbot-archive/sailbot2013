from arduino import Arduino
from gps import Gps
import time

class Boat(object):
    def __init__(self):
        """Constructor for the boat object"""
        self._arduino = Arduino()
        self._gps = Gps()

    def log(self, logfilename='logfile'):
        """Log the output of most of the sensors"""
        l = 'Time: {time}, Heading: {head}, Wind: {wind}, Position: {pos}\n'.format(
                time = time.asctime(),
                head = self._arduino.get_compass(),
                wind = self._arduino.get_wind(),
                pos = self._gps.position()
            )
        with open(logfilename, 'a') as f:
            f.write(l)
