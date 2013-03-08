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

    @property
    def lat(self):
        return self._lat

    @property
    def long(self):
        return self._long

#do a couple of tests
if __name__ == '__main__':
    castle = Point(52.41389, -4.09098) #aber castle
    hill = Point(52.42459, -4.08339) #Constitution hill
    #distance should be ~1.29844 km
    print hill.lat, hill.long
    print hill[0], hill[1]
