from pynmea.streamer import NMEAStream
from pynmea import nmea
from pynmea.nmea import NMEASentence
import pynmea
import serial


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
 

serialport = serial.Serial("/dev/ttyUSB0", 4800, timeout=0.5)


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

while 1:
    response = serialport.readline(None)

    try:
        data_obs = streamer.get_objects(data=response)
        data_obs += streamer.get_objects(data='')
        print data_obs
        #nmea_ob = nmea.parse(data_obs)
        nmea_ob = None
        if nmea_ob is not None:
            print "type is " + nmea_ob.sen_type
        else:
            print "type is none response was " + response
        #data_file = '../tests/test_data/test_datarev.gps'
                

        if len(data_obs) > 0:
            if data_obs[0].sen_type == "GPGGA":
                print "got a GGA string"
                printGGA(data_obs[0])
            
    except pynmea.exceptions.NoDataGivenError:
        print 'error no data'

