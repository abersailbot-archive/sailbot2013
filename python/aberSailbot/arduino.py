import serial
import time
import threading
import Queue

class Arduino(object):
    # Arduino will probably be on one of these devices :
    # /dev/ttyUSB0 ttyUSB1 ttyUSB2 ttyUSB3 ttyS0 ttyS1 ttyS2 ttyS3
    device = '/dev/ttyUSB0'
    baudRate = 9600
    def __init__(self):
        self.__queue = QueueManager(self.device, self.baudRate)
        self.__queue.connect()
        time.sleep(1) # sleep for a second to give the arduino time to reset

    def send_message(self, message):
        self.__queue.out.put(message)

    def read(self):
        try:
            return self.__queue.out.get_nowait()
        except Queue.Empty:
            return None

class QueueManager(object):
    def __init__(self, device, baudRate=9600):
        self.inp = Queue.Queue()
        self.out = Queue.Queue()
        self.device = device
        self.baudRate = baudRate

        self.keepRunning = True
        self.activeConnection = False

    def runInput(self):
        print 'from input thread'
        while self.keepRunning:
            try:
                inputline = self.serialPort.readline()
                self.inp.put(inputline.rstrip())
            except Exception, e:
                # connection closed
                print e

    def runOutput(self):
        print 'from output thread'
        while self.keepRunning:
            if not self.out.empty():
                outputline = self.out.get_nowait()
                print 'sending message', outputline
                self.serialPort.write(outputline + 'qweweq\n')

    def connect(self):
        #self.outputThread.join()

        if self.activeConnection:
            self.close()
            self.activeConnection = False
        #try:
        self.serialPort = serial.Serial(self.device, self.baudRate)
        #except serial.SerialException:
        #    print 'error'
        #    self.inp.put('error, can\'t connect')
        #     return
        self.keepRunning = True
        self.inputThread = threading.Thread(target=self.runInput)
        self.inputThread.daemon = True
        self.inputThread.start()
        self.outputThread = threading.Thread(target=self.runOutput)
        self.outputThread.daemon = True
        self.outputThread.start()
        self.activeConnection = True

    def close(self):
        self.keepRunning = False;
        self.serialPort.close()
        self.out.put(' ')
        self.inputThread.join()
        self.outputThread.join()
        self.inp.put('IOHALT')

if __name__ == '__main__':
    a = Arduino()
    for i in range(5):
        a.send_message('hi there')
