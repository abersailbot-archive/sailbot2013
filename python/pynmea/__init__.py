__VERSION__ =  (0, 3, 0)

import nmea, streamer, exceptions, utils
def get_version():
    return '.'.join([str(x) for x in __VERSION__])
