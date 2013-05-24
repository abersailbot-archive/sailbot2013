from arduino import Arduino
from bearing import Bearing
from gps import Gps
from xbee import Xbee
from waypoints import Waypoints
import config

import time
import traceback

class Boat(object):
    def __init__(self, waypointFile=None):
        """Constructor for the boat object"""
        self.arduino = Arduino()
        self.gps = Gps()
        self._xbee = Xbee()
        self._waypointN = 0
        self._waypointE = 0
        self._waypointNumber = 0

        self.windreadings = []

    def log(self, logfilename='logfile'):
        """
        Log the output of the sensors to stdout, a logfile and down the xbee
        """
        try:
            l = 'time={time}\
 bhead={head}\
 wind={wind}\
 lat={pos.lat}\
 lon={pos.long}\
 nwlat={wpn}\
 nwlon={wpe}\
 nwn={num}\n\r'.format(
                    time = int(time.time()),
                    head = self.arduino.get_compass(),
                    wind = self.get_wind_average(),
                    pos = self.gps.position,
                    wpn = self._waypointN,
                    wpe= self._waypointE,
                    num = self._waypointNumber
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
        w = wind + bearing
        self.windreadings += [int(w)]
        if len(self.windreadings) > config.maxWindReadings:
            self.windreadings = self.windreadings[:-1]
        return w

    def get_wind_average(self):
        self.get_wind_bearing()
        print(self.windreadings)
        return sum(self.windreadings) / len(self.windreadings)

    def set_waypoint_northing(self, v):
        self._waypointN = v

    def set_waypoint_easting(self, v):
        self._waypointE = v

    def get_waypoint_number(self):
        return self._waypointNumber

    def set_waypoint_number(self, v):
        self._waypointNumber = v

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
