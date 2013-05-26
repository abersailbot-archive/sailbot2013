from arduino import Arduino
from bearing import Bearing
from gps import Gps
from xbee import Xbee
from math import sin, cos, atan2
import math
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
        self._waypointDist = 0
        self._waypointHeading = 0

        self.s = 0
        self.c = 0
        self.r = 250

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
 nwn={num}\
 spos={sail}\
 rpos={rudder}\n\r'.format(
                    time = int(time.time()),
                    head = self.arduino.get_compass(),
                    wind = self.get_wind_average(),
                    pos = self.gps.position,
                    wpn = self._waypointN,
                    wpe= self._waypointE,
                    num = self._waypointNumber,
                    sail = self.arduino.sailAngle,
                    rudder = self.arduino.rudderAngle
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

    def get_wind_average(self):
        self.s += (sin(math.radians(self.get_wind_bearing())) - self.s) / self.r
        self.c += (cos(math.radians(self.get_wind_bearing())) - self.c) / self.r
        a = int(math.degrees(atan2(self.s, self.c)))
        if a < 0:
            return a + 360
        else:
            return a

    def set_waypoint_northing(self, v):
        self._waypointN = v

    def set_waypoint_easting(self, v):
        self._waypointE = v

    def get_waypoint_number(self):
        return self._waypointNumber

    def set_waypoint_number(self, v):
        self._waypointNumber = v

    def set_waypoint_distance(self, v):
        self._waypointDist = v

    def set_waypoint_heading(self, v):
        self._waypointHeading

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
