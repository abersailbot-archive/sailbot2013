import serial
import time
import threading
import Queue

class Arduino(object):
    # Arduino will probably be on one of these devices :
    # /dev/ttyUSB0 ttyUSB1 ttyUSB2 ttyUSB3 ttyS0 ttyS1 ttyS2 ttyS3
    device = '/dev/ttyUSB0'
    baudRate = 9600
    class InputThread(threading.Thread):
        def __init__(self, queue, serial):
            threading.Thread.__init__(self)
            self.queue = queue
            self.serial = serial

        def run(self):
            while True:
                inputline = self.serial.readline()
                self.queue.put(inputline.rstrip())

    def __init__(self):
        self.serialPort = serial.Serial(self.device, self.baudRate)
        time.sleep(1) # sleep for a second to give the arduino time to reset
        self.inputQueue = Queue.Queue()
        self.inp = self.InputThread(self.inputQueue, self.serialPort)
        self.inp.setDaemon(True)
        self.inp.start()

    def send_message(self, message):
        self.serialPort.write(message + '\n')

    def read(self):
        if not self.queue.empty():
            inputline = self.queue.get_nowait()
            self.inp.put(inputline.rstrip())

if __name__ == '__main__':
    #test it
    a = Arduino()
    for i in range(5):
        a.send_message('hello usb port')
        print 'got', a.read()
        time.sleep(3)
