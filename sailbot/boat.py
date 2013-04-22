from arduino import Arduino
from gps import Gps
import time

class Boat(object):
    def __init__(self):
        """Constructor for the boat object"""
        self.arduino = Arduino()
        self._gps = Gps()

    def log(self, logfilename='logfile'):
        """Log the output of most of the sensors"""
        l = 'Time: {time}, Heading: {head}, Wind: {wind}, Position: {pos}\n'.format(
                time = int(time.time()),
                head = self.arduino.get_compass(),
                wind = self.arduino.get_wind(),
                pos = self._gps.position()
            )
        with open(logfilename, 'a') as f:
            f.write(l)

if __name__ == '__main__':
    b = Boat()
    time.sleep(1)
    while True:
        b.arduino.set_rudder(1000)
        time.sleep(10)
        b.arduino.set_sail(1000)
        b.arduino.set_rudder(2000)
        time.sleep(10)
        b.arduino.set_sail(2000)
