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
    def __init__(self):
        #self._gpsSerial = serial.Serial(config.gpsSerialport, 4800, timeout=0.5)
        pass

    def get_coords(self):
        """Return the current coordinates from the GPS"""
        #line = self._gpsSerial.readline(None)
        pass

    def _parse_degrees(self, degrees):
        pointIndex = degrees.find('.') - 2
        return (
                float(degrees[:pointIndex]) +
                float(degrees[pointIndex:]) / 60
               )


    def _name_fields(self, line):
        fields = line[line.find('$')+1:line.find('*')].split(',')[1:8]
        names = [
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
    f=gps._name_fields(demoline)
    print gps._parse_degrees(f.lat)
