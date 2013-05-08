from point import Point

class Waypoints(object):
    def __init__(self, points=None):
        self._points = points if points is not None else []
        self._currentWaypoint = 0

    def add_point(self, point):
        """Add a waypoint to the end of the current list of waypoints"""
        self._points += [point]

    @property
    def next(self):
        """Return the next waypoint"""
        return self._points[self._currentWaypoint]

    def distance_to(self):
        """Return the distance to the next waypoint in meters"""
        return self.currentLoc.distance_to(self.next)

if __name__ == '__main__':
    w = Waypoints()
    w.add_point(Point(52.42459, -4.08339))
    print w.distance_to()
