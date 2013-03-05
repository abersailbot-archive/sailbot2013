from arduino import Arduino
import time

class Robot(object):
    def __init__(self):
        self._arduino = Arduino()

    def log(self):
        '''Log the output of most of the sensors'''
        l = 'Time: {time}, Heading: {head}, Wind: {wind}\n'.format(
                time = time.asctime(),
                head = self._arduino.getCompass()
                wind = self._arduino.getCompass()
            )
        with open(logfile, 'a') as f:
            f.writeline(l)
