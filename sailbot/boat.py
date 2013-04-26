from arduino import Arduino
from bearing import Bearing
from gps import Gps
from xbee import Xbee

import time
import traceback

class Boat(object):
    def __init__(self):
        """Constructor for the boat object"""
        self.arduino = Arduino()
        self._gps = Gps()
        self._xbee = Xbee()

    def log(self, logfilename='logfile'):
        """
        Log the output of the sensors to stdout, a logfile and down the xbee
        """
        try:
            l = 'time={time} bhead={head} wind={wind} lat={pos.lat} lon={pos.long}\n\r'.format(
                    time = int(time.time()),
                    head = self.arduino.get_compass(),
                    wind = self.arduino.get_wind(),
                    pos = self._gps.position()
                )

            # write to log file
            with open(logfilename, 'a') as f:
                f.write(l)

            # write to xbee
            self._xbee.send(l)
            
            # write to console
            print l

        except:
            trace = traceback.format_exc()
            with open('errors', 'a') as f:
                f.write(str(time.time()) + ':\n' + trace + '\n')

    def get_wind_bearing(self):
        """Return the absolute bearing of the wind"""
        wind = Bearing(self.arduino.get_wind())
        bearing = Bearing(self.arduino.get_compass())
        return wind + bearing

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
