#include <Servo.h> 
#include <Wire.h>
#include <EEPROM.h>

#define HMC6343_ADDRESS 0x19
#define HMC6343_HEADING_REG 0x50

Servo myRudderServo; // create servo object to control a servo 
Servo mySailServo; // a maximum of eight servo objects can be created 

char inData[6]; // Allocate some space for the string
int DEBUG = 0;
int offset = 0;

void setup() {
    Serial.begin(9600); //Begin at 9600
    //Use .attach for setting up connection to the servo
    myRudderServo.attach(10, 1000, 2000); // Attach, with the output limited
                                          // between 1000 and 2000 ms
    mySailServo.attach(9, 1000, 1850); // Same, but between 1000 and 1850 ms
    myRudderServo.writeMicroseconds(1500);
    mySailServo.writeMicroseconds(1500);
    pinMode(11, INPUT);  //Use pinMode for setting up connection to wind sensor
    pinMode(12, OUTPUT);
    Wire.begin(); // Initialize the I2C bus for the compass
    offset = (EEPROM.read(0) << 8) + EEPROM.read(1);
    if (DEBUG) {
        Serial.write("Power On\n");
    }
}

void getData() {
    char inChar=-1; // Where to store the character read
    byte index = 0; // Index into array; where to store the character
    for(index = 0; index<5; index++) {
        while (Serial.available() == 0);
        inChar = Serial.read(); // Read a character
        if(inChar != '\n') {
            inData[index] = inChar; // Store it
        } else {
            break;
        }
    }
    inData[index] = '\0'; // Null terminate the string
}

void setServo(char servoChar, int turnAmount){
    if('R' == servoChar) {
        turnAmount = constrain(turnAmount, 1000, 2000);
        myRudderServo.writeMicroseconds(turnAmount);
    }
    else {
        turnAmount = constrain(turnAmount, 1000, 1850);
        mySailServo.writeMicroseconds(turnAmount);
    }
}

int getAmount() {
    int turnAmount;
    turnAmount = (int) strtol(inData+1, NULL, 10);
    Serial.println(turnAmount);
    return turnAmount;
}

int readCompass() {
    byte highByte, lowByte;

    Wire.beginTransmission(HMC6343_ADDRESS); // Start communicating with the HMC6343 compasss
    Wire.write(HMC6343_HEADING_REG); // Send the address of the register that we want to read
    Wire.endTransmission();

    Wire.requestFrom(HMC6343_ADDRESS, 6); // Request six bytes of data from the HMC6343 compasss
    while(Wire.available() < 1); // Busy wait while there is no byte to receive

    highByte = Wire.read(); // Reads in the bytes and convert them into proper degree units.
    lowByte = Wire.read();
    float heading = ((highByte << 8) + lowByte) / 10.0; // the heading in degrees

    return (int)heading; // Print the sensor readings to the serial port.
}

int readWindSensor() {
    int windSensorPin = 11;
    int pulseLength=0;
    int windAngle=0;
    pulseLength = pulseIn(windSensorPin, HIGH, 2000);
    int magic = 29;
    windAngle =((pulseLength*10)/29); // 29 is the magic number where pulse time of 1036 = 359
    windAngle = windAngle - offset;//Compensate for offset
    windAngle = mod(windAngle); // Wrap Arround
    return (windAngle);
}

int mod(int windAngle){
    int newWindAngle;
    if(windAngle < 0){
        newWindAngle = windAngle + 360;
    }
    else if(windAngle >= 360){
        newWindAngle = windAngle - 360;
    }
    else{
        newWindAngle = windAngle;
    }
    return newWindAngle;
}

void loop() {
    getData();
    switch(inData[0]){
        case 'c':
            if (DEBUG) {
              Serial.print("c");
            }
            Serial.println(readCompass()); //Compass Read
            break;
        case 'w':
            if (DEBUG) {
              Serial.print("w");
            }
            Serial.println(readWindSensor()); //Wind Sensor Read
            break;
        case 'r':
            if (DEBUG) {
              Serial.println("r");
            }
            setServo('R', getAmount()); // Rudder Set
            break;
        case 's':
            if (DEBUG) {
              Serial.println("s");
            }
            setServo('S', getAmount()); // Sail Set
            break;
        case 'o':
            if (DEBUG) {
              Serial.println("o");
            }
            offset = readWindSensor();
            byte byte1 = (byte)(offset >> 8);
            byte byte2 = (byte)(offset << 8);
            if (DEBUG) {
              Serial.println("Offset: " + offset);
              Serial.println("Byte1: " + byte1);
              Serial.println("Byte2: " + byte2);
            }
            EEPROM.write(0, byte1);
            EEPROM.write(1, byte2);
            Serial.println(1);
    }
}
