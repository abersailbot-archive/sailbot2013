import math
from math import sin as sin
from math import cos as cos

EARTH_RADIUS = 6371009 #in meters

class Point(object):
    def __init__(self, latitude, longitude):
        self._lat = latitude
        self._long = longitude

    def __getitem__(self, key):
        if key == 0:
            return self._lat
        elif key == 1:
            return self._long
        else:
            raise IndexError('Point objects can only have two coordinates')

    def __iter__(self):
        return iter([self.lat, self.long])

    @property
    def lat(self):
        return self._lat

    @property
    def long(self):
        return self._long

    def _to_radians(self, degrees):
        return degrees * (math.pi/180)

    @property
    def lat_radians(self):
        return self._to_radians(self.lat)

    @property
    def long_radians(self):
        return self._to_radians(self.long)

    def distance_to(self, point):
        angle = math.acos(
                sin(self.lat_radians) * sin(point.lat_radians) +
                cos(self.lat_radians) * cos(point.lat_radians) *
                cos(self.long_radians - point.long_radians)
            )
        return angle * EARTH_RADIUS

#do a couple of tests
if __name__ == '__main__':
    castle = Point(52.41389, -4.09098) #aber castle
    hill = Point(52.42459, -4.08339) #Constitution hill
    print hill.lat, hill.long
    print hill.lat_radians, hill.long_radians

    #distance should be ~1.29844 km
    print hill.distance_to(castle)
