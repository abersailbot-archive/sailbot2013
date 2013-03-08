import math
from math import sin as sin
from math import cos as cos

EARTH_RADIUS = 6371009 #in meters

class Point(object):
    """A point on the face of the earth"""
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
        """Return an iterator containing the lat and long"""
        return iter([self.lat, self.long])

    @property
    def lat(self):
        """Return the latitude in degrees"""
        return self._lat

    @property
    def long(self):
        """Return the longitude in degrees"""
        return self._long

    def _to_radians(self, degrees):
        return degrees * (math.pi/180)

    @property
    def lat_radians(self):
        """Return the latitude in radians"""
        return self._to_radians(self.lat)

    @property
    def long_radians(self):
        """Return the longitude in radians"""
        return self._to_radians(self.long)

    def spherical_law_of_cosines(self, point):
        """Return the distance between two points in meters"""
        angle = math.acos(
                sin(self.lat_radians) * sin(point.lat_radians) +
                cos(self.lat_radians) * cos(point.lat_radians) *
                cos(self.long_radians - point.long_radians)
            )
        return angle * EARTH_RADIUS

    def haversine(self, point):
        delta_lat = point.lat_radians - self.lat_radians
        delta_long = point.long_radians - self.long_radians

        a = (sin(delta_lat/2) * sin(delta_lat/2) +
            cos(self.lat_radians) * cos(point.lat_radians) * 
            sin(delta_long/2) * sin(delta_long/2))
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        return c * EARTH_RADIUS

#do a couple of tests
if __name__ == '__main__':
    castle = Point(52.41389, -4.09098) #aber castle
    small = Point(52.413877, -4.091050)
    hill = Point(52.42459, -4.08339) #Constitution hill
    print hill.lat, hill.long
    print hill.lat_radians, hill.long_radians

    #distance should be ~1.29844 km
    print castle.spherical_law_of_cosines(small)
    print castle.haversine(small)
