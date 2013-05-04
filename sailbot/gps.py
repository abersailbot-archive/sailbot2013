from point import Point
import config
import serial
import time

class AttributeDict(dict):
    """Access elements of the dict as attributes"""
    def __getattr__(self, attr):
        return self[attr]

    def __setattr__(self, attr, value):
        self[attr] = value

def _float_or_none(value):
    """Return None if the input value is empty, else return the value converted
    into a float"""
    if value:
        try:
            return float(value)
        except ValueError:
            return None
    else:
        return None

class Gps(object):
    """A GPS receiver"""
    def __init__(self):
        self._gpsSerial = serial.Serial(config.gpsSerialport, 
                                        4800,
                                        parity=serial.PARITY_NONE,
                                        bytesize=serial.EIGHTBITS,
                                        stopbits=serial.STOPBITS_ONE,
                                        timeout=0.5)
        time.sleep(0.25)
        for c in [
                '$PSRF103,05,00,00,01*21',
                '$PSRF103,04,00,00,01*20',
                '$PSRF103,03,00,00,01*27',
                '$PSRF103,02,00,00,01*26',
                '$PSRF103,01,00,00,01*25',
                '$PSRF103,00,00,00,01*24']:
            self._send_command(c)
            time.sleep(0.25)
        time.sleep(0.25)
        self._gpsSerial.flushInput()
        self._gpsSerial.flushOutput()
        time.sleep(0.5)

    def _send_command(self, command):
        print 'sending:', command + '\n'
        self._gpsSerial.write(command)

    @property
    def position(self):
        """Return a Point containing the current coordinates from the GPS"""
        try:
            line = self.get_gga_line()
	    print line
        except IOError:
            return Point(-1, -1)

        if self.checksum(line):
            fields = self._name_fields(line)
            if fields.id == 'GPGGA':
                lat = self._parse_degrees(fields.lat)
                long = self._parse_degrees(fields.long)
		
                if fields.lat_direction == 'S':
                    lat = -lat
                if fields.long_direction == 'W':
                    long = -long
                if lat is None:
                    lat = -1
                if long is None:
                    long = -1
                return Point(lat, long)
        else:
            raise ValueError('Checksum failed on "{}"'.format(line))

    def get_gga_line(self):
        self._gpsSerial.flushInput()
        self._gpsSerial.flushOutput()
        self._gpsSerial.write('$PSRF103,00,01,00,01*25\n')
        time.sleep(0.5)
        return self._gpsSerial.readline(None).strip()

    def get_rmc_line(self):
        self._gpsSerial.flushInput()
        self._gpsSerial.flushOutput()
        self._gpsSerial.write('$PSRF103,04,01,00,01*21\r\n')
        time.sleep(0.2)
        return self._gpsSerial.readline(None).strip()

    def _parse_degrees(self, strDegrees):
        """
        Return the decimal representation of a combined degree/minute string
        """
        if not strDegrees:
            #return none if the input is empty
            return None

        pointIndex = strDegrees.find('.') - 2
        degrees = _float_or_none(strDegrees[:pointIndex])
        minutes = _float_or_none(strDegrees[pointIndex:])
        return (degrees + minutes / 60)

    def checksum(self, line):
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
            d[names[i]] = fields[i]
        return d

if __name__ == '__main__':
    gps = Gps()
    print gps.position
