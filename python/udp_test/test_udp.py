import socket, traceback
import time


s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
s.bind((host, port))

#message = "lat=52.4064 long=-4.0764 spd=1.2000 bhead=90 thead=90 whead=90 wind=90 spos=128 rpos=130 nwn=2 nwlat=52.4064 nwlon=-4.0779 time=1336476465"
rest=" spd=1.2000 bhead=90 thead=90 whead=90 wind=90 spos=128 rpos=130 nwn=2 nwlat=52.4064 nwlon=-4.0779"
lat=52.41156
lon=-4.08975

while 1:
    try:
      	message="lat="+str(lat)+" lon="+str(lon)+rest+" time="+str(time.time())
	print message 
        s.sendto(message, ("<broadcast>", 4321))
	time.sleep(2)
	lat+=0.00005
	lon-=0.00005
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        traceback.print_exc()
