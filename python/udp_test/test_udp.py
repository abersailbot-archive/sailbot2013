from socket import *
import sys, time

host = "localhost"
textport = "4321"

#s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = int(textport)

s = socket(AF_INET, SOCK_DGRAM)
s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
s.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
#s.sendto('This is a test', ('255.255.255.255', 54545))
s.connect(('<broadcast>', 4321)) 

print "Enter data to transmit: "
data = sys.stdin.readline().strip()
s.sendall(data)
s.shutdown(1)

print "Looking for replies; press Ctrl-C or Ctrl-Break to stop."
while 1:
    buf = s.recv(2048)
    if not len(buf):
        break
    print "Received: %s" % buf
