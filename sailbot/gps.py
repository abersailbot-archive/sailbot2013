from point import Point
import config
import serial

class Gps(object):
    def __init__(self):
        #self._gpsSerial = serial.Serial(config.gpsSerialport, 4800, timeout=0.5)
        pass

    def get_coords(self):
        """Return the current coordinates from the GPS"""
        #response = self._gpsSerial.readline(None)
        pass

    def _split_response(self, response):
        r = response
        return r[r.find('$')+1:r.find('*')].split(',')

if __name__ == '__main__':
    demoresponse = "$GPGGA,113245.000,5223.9915,N,00352.1781,W,1,08,1.0,329.0,M,50.9,M,,0000*4A"
    gps = Gps()
    print gps._split_response(demoresponse)
