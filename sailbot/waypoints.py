class Waypoints(object):
    def __init__(self, points=None):
        self._points = points if points is not None else []
        self._currentWaypoint = 0

    def add_point(self, point):
        """Add a waypoint to the end of the current list of waypoints"""
        self._points += point
