#include <Servo.h> 
#include <Wire.h>

#define HMC6343_ADDRESS 0x19
#define HMC6343_HEADING_REG 0x50

Servo myRudderServo; // create servo object to control a servo 
Servo mySailServo; // a maximum of eight servo objects can be created 

char inData[6]; // Allocate some space for the string

void setup() {
    Serial.begin(9600); //Begin at 9600
    //Use .attach for setting up connection to the servo
    myRudderServo.attach(10, 1000, 2000);
    mySailServo.attach(9, 1000, 1850);
    myRudderServo.writeMicroseconds(1500);
    mySailServo.writeMicroseconds(1500);
    pinMode(11, INPUT);  //Use pinMode for setting up connection to wind sensor
    pinMode(12, OUTPUT);
    Wire.begin(); // Initialize the I2C bus for the compass
    Serial.write("Power On\n");
}

void getData() {
    char inChar=-1; // Where to store the character read
    byte index = 0; // Index into array; where to store the character
    for(index = 0; index<5; index++)
    {
      while (Serial.available() == 0);
      inChar = Serial.read(); // Read a character
      if(inChar != '\n'){
        inData[index] = inChar; // Store it
      }
      else{
        break;
      } 
    }
    inData[index] = '\0'; // Null terminate the string
}

void setServo(char servoChar, int turnAmount){
    if('R' == servoChar){
      turnAmount = constrain(turnAmount, 1000, 2000);
      myRudderServo.writeMicroseconds(turnAmount);
      //myRudderServo.writeMicroseconds(500);
    }
    else{
      turnAmount = constrain(turnAmount, 1000, 1850);
      mySailServo.writeMicroseconds(turnAmount);
      //mySailServo.writeMicroseconds(500);
    }
}

int getAmount(){
 // char newArray[5] = {
 //     inData[1], inData[2], inData[3], inData[4], 0    };
    int turnAmount;
    turnAmount = (int) strtol(inData+1, NULL, 10);
    Serial.println(turnAmount);
    return turnAmount;
}

int readCompass(){
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

int readWindSensor(){
    int windSensorPin = 11;
    int pulseLength=0;
    int windAngle=0;
    pulseLength = pulseIn(windSensorPin, HIGH, 2000);
    int magic = 29;
    windAngle =((pulseLength*10)/29); // 29 is the magic number where pulse time of 1036 = 359
    return (windAngle);
}

void loop()
{
    getData();
    //inData[0] = 'c'; // Infinate Compass Read
    switch(inData[0]){
    case 'c' : 
      Serial.println(readCompass()); //Compass Read
      break;
    case 'w' : 
      Serial.println(readWindSensor()); //Wind Sensor Read
      break;
    case 'r' : 
      Serial.println("r"); 
      setServo('R', getAmount());// Rudder Set

      break;
    case 's' : 
      Serial.println("s"); 
      setServo('S', getAmount()); // Sail Set

      break;
    }
}



