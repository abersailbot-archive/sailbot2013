from point import Point

class Waypoints(object):
    def __init__(self, points=None):
        self._points = points if points is not None else []
        self._currentWaypoint = 0
        self.currentLoc = Point(52.41389, -4.09098)

    def add_point(self, point):
        """Add a waypoint to the end of the current list of waypoints"""
        self._points += [point]

    @property
    def next(self):
        """Return the next waypoint"""
        return self._points[self._currentWaypoint]

    def distance(self):
        """Return the distance to the next waypoint in meters"""
        return self.currentLoc.distance_to(self.next)

    def bearing(self):
        """Return the bearing to the nextwaypoint"""
        return self.currentLoc.bearing_to(self.next)

    def set_next(self):
        """Assign the next waypoint to the subsequent point in the list"""
        self._currentWaypoint = min(self._currentWaypoint + 1,
                                    len(self._points) - 1)

if __name__ == '__main__':
    w = Waypoints()
    w.add_point(Point(52.42459, -4.08339))
    w.add_point(Point(52.42459, -3.08339))
    w.add_point(Point(53.42459, -3.08339))
    print w.distance()
    print w.bearing()
    w.set_next()
    print w.distance()
    print w.bearing()
    w.set_next()
    print w.distance()
    print w.bearing()
