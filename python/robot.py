from arduino import Arduino
import time
from pynmea.streamer import NMEAStream
from pynmea import nmea
from pynmea.nmea import NMEASentence
import serial
import math

class Robot(object):
    def __init__(self):
        self._arduino = Arduino()
        self._serialport = serial.Serial("/dev/ttyUSB0", 4800, timeout=0.5)

        parse_map = (('Latitude' , 'lat'),
                     ('Direction', 'lat_dir'),
                     ('Longitude', 'lon'),
                     ('Direction', 'lon_dir'))

        self.nmea = NMEASentence(parse_map)
        self.streamer = NMEAStream()

    def log(self):
        """Log the output of most of the sensors"""
        l = 'Time: {time}, Heading: {head}, Wind: {wind}, Coords: {coords}\n'.format(
                time = time.asctime(),
                head = self._arduino.getCompass(),
                wind = self._arduino.getCompass(),
                coords = self.getCoords()
            )
        with open('logfile', 'a') as f:
            f.write(l)

    def getCoords(self):
        """Return the current coordinates from the GPS"""
        response = self._serialport.readline(None)
        try:
            data_obs = self.streamer.get_objects(data=response)
            data_obs += self.streamer.get_objects(data='')

            if len(data_obs) > 0:
                if data_obs[0].sen_type == "GPGGA":
                    if data_obs[0].check_chksum():
                        location = self._getGGA(data_obs[0])
                        return location
                    else:
                        print "checksum failed"
        except Exception, e:
            print e

    def _getGGA(self, nmeaOb):
        """Return the coordinates from a nmea object"""
        lat_deg = float(nmeaOb.latitude[:2])
        lat_min = float(nmeaOb.latitude[2:9])
        lat_min_dec = lat_min/60
        lat = lat_deg + lat_min_dec
        if nmeaOb.lat_direction == "S":
            lat = -lat
    
        lon_deg = float(nmeaOb.longitude[:3])
        lon_min = float(nmeaOb.longitude[3:10])
        lon_min_dec = lon_min/60
        lon = lon_deg + lon_min_dec
        if nmeaOb.lon_direction == "W":
            lon = -lon
        return lon, lat
