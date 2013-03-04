from pynmea.streamer import NMEAStream
from pynmea import nmea
from pynmea.nmea import NMEASentence
import pynmea
import serial
import math

#reads waypoints from file to list
with open('../tests/test_data/waypoints.txt') as f:
    waypoints = f.readlines()

points = []
for p in waypoints:
    l = p.split(",")
    points += [(float(l[0]), float(l[1]))]

def calc_dist(start,end):
    lon1 = start[0]*(math.pi/180)
    lon2 = end[0]*(math.pi/180)
    lat1 = start[1]*(math.pi/180)
    lat2 = end[1]*(math.pi/180)
    d = math.acos(math.sin(lat1)*math.sin(lat2)+math.cos(lat1)*math.cos(lat2)*math.cos(lon1-lon2))
    d = d*180*60/math.pi
    d = d/0.54
    return d

def calc_bearing(start,end):
    lon1 = start[0]*(math.pi/180)
    lon2 = end[0]*(math.pi/180)
    lat1 = start[1]*(math.pi/180)
    lat2 = end[1]*(math.pi/180)
    b = math.atan2(math.sin(lon2-lon1)*math.cos(lat2), math.cos(lat1)*math.sin(lat2)-math.sin(lat1)*math.cos(lat2)*math.cos(lon2-lon1));
    b = b*(180/math.pi)
    if b<0:
        b = b+360
    return b

def printGGA(nmea_ob):
    lat_deg = float(nmea_ob.latitude[:2])
    
    lat_min = float(nmea_ob.latitude[2:9])
    
    lat_min_dec = lat_min/60
    lat = lat_deg + lat_min_dec
    if nmea_ob.lat_direction == "S":
        lat = -lat
    
    lon_deg = float(nmea_ob.longitude[:3])
    
    lon_min = float(nmea_ob.longitude[3:10])
    
    lon_min_dec = lon_min/60
    lon = lon_deg + lon_min_dec
    if nmea_ob.lon_direction == "W":
        lon = -lon
    print str(lon) + ", " + str(lat) #could switch round lon and lat (discuss)
    return lon, lat

#serialport = serial.Serial("/dev/ttyUSB0", 4800, timeout=0.5)


#with open(data_file, 'r') as data_file_fd:
#    nmea_stream = NMEAStream(stream_obj=data_file_fd)
#    next_data = nmea_stream.get_objects()
#    nmea_objects = []
#    while next_data:
#        nmea_objects += next_data
#        next_data = nmea_stream.get_objects()
parse_map = (("Latitude" , "lat"),
             ("Direction", "lat_dir"),
             ("Longitude", "lon"),
             ("Direction", "lon_dir"))


nmea = NMEASentence(parse_map)
streamer = NMEAStream()

for i in range(1,3):
    #response = serialport.readline(None)
    #response = "$GPGGA,184337.07,1929.361,S,02410.411,E,1,04,1.8,100.00,M,-33.9,M,,0000*60"
    response = "$GPGGA,113245.000,5223.9915,N,00352.1781,W,1,08,1.0,329.0,M,50.9,M,,0000*4A"
    waypoint = -4.0653,52.416
    try:
        data_obs = streamer.get_objects(data=response)
        data_obs += streamer.get_objects(data='')
        print data_obs
        print "response was " + response
        #data_file = '../tests/test_data/test_datarev.gps'
                

        if len(data_obs) > 0:
            if data_obs[0].sen_type == "GPGGA":
                if data_obs[0].check_chksum():
                    print "got a GGA string"
                    location = printGGA(data_obs[0])
                    dist = calc_dist(location,waypoint)
                    bear = calc_bearing(location,waypoint)
                    print str(dist)+"km to waypoint"
                    print str(bear)+" degrees"
                else:
                    print "checksum failed"
    except Exception, e:
        print e

print points
print waypoints
