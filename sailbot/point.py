class Point(object):
    def __init__(self, latitude, longitude):
        self._lat = latitude
        self._long = longitude

#do a couple of tests
if __name__ == '__main__':
    castle = Point(52.41389, -4.09098) #aber castle
    hill = Point(52.42459, -4.08339) #Constitution hill
    #distance should be ~1.29844 km
