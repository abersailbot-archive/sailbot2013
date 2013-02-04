import socket, traceback
import time

host = ''                               # Bind to all interfaces
port = 4321

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
s.bind((host, port))

message = "lat=52.4064 long=-4.0764 spd=1.2000 bhead=90 thead=90 whead=90 wind=90 spos=128 rpos=130 nwn=2 nwlat=52.4064 nwlon=-4.0779 time=1336476465"

while 1:
    try:
        #message, address = s.recvfrom(8192)
        #print "Got data from", address
        # Acknowledge it.
        s.sendto("I am here", ("<broadcast>", 4321))
	time.sleep(2)
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        traceback.print_exc()
