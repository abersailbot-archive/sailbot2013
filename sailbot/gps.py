from point import Point
import config
import serial

class AttributeDict(dict):
    """Access elements of the dict as attributes"""
    def __getattr__(self, attr):
        return self[attr]

    def __setattr__(self, attr, value):
        self[attr] = value

class Gps(object):
    """A GPS receiver"""
    def __init__(self):
        self._gpsSerial = serial.Serial(config.gpsSerialport, 4800, timeout=0.5)

    def position(self):
        """Return a Point containing the current coordinates from the GPS"""
        line = self._gpsSerial.readline(None)
        if self._checksum(line):
            fields = self._name_fields(line)
            if fields.id == 'GPGGA':
                lat = self._parse_degrees(fields.lat)
                long = self._parse_degrees(fields.long)
                return Point(lat, long)
        else:
            raise ValueError('Checksum failed')

    def get_gga_line(self, attempts=5):
        for i in range(attempts):
            line = self._gpsSerial.readline(None)
            if line.startswith('$GPGGA'):
                return line
        raise Exception('GPS didn\'t give a gga string in time')

    def _parse_degrees(self, degrees):
        """
        Return the decimal representation of a combined degree/minute string
        """
        pointIndex = degrees.find('.') - 2
        return (
                float(degrees[:pointIndex]) +
                float(degrees[pointIndex:]) / 60
               )

    def _checksum(self, line):
        """Return True if the checksum passed"""
        x = 0
        for c in line[1:-3]:
            x ^= ord(c)
        x = str(hex(x))[2:].upper()
        check_digits = line[-2:].upper()
        return check_digits == x

    def _name_fields(self, line):
        """Return an AttributeDict containing the more important GGA fields"""
        fields = line[1:-3].split(',')[:8]
        names = [
                    'id',
                    'time',
                    'lat',
                    'lat_direction',
                    'long',
                    'long_direction',
                    'fix_quality',
                    'satellite_number',
                    'hdop',
                    'altitude'
                ]
        d = AttributeDict()
        for i in range(len(fields)):
            print i, names[i], fields[i]
            d[names[i]] = fields[i]
        return d

if __name__ == '__main__':
    demoline = "$GPGGA,113245.000,5223.9915,N,00352.1781,W,1,08,1.0,329.0,M,50.9,M,,0000*4A"
    gps = Gps()
    print gps.position()
