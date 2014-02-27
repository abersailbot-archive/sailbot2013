#include <CMPS10.h>
#include <Servo.h> 
#include <Wire.h>
#include <EEPROM.h>
#include <OneWire.h>

Servo myRudderServo; // create servo object to control a servo 
Servo mySailServo; // a maximum of eight servo objects can be created
OneWire ds(2);
CMPS10 cmps10;

char inData[6]; // Allocate some space for the string
int offset = 0;

int DEBUG = 0;

void setup() {
  Serial.begin(9600); //Begin at 9600
  //Use .attach for setting up connection to the servo
  myRudderServo.attach(10, 1060, 1920); // Attach, with the output limited
  // between 1000 and 2000 ms
  mySailServo.attach(9, 1050, 1930); // Same, but between 1000 and 1850 ms
  myRudderServo.writeMicroseconds(1500);
  mySailServo.writeMicroseconds(1500);
  pinMode(11, INPUT);    //Use pinMode for setting up connection to wind sensor
  pinMode(12, OUTPUT);
  Wire.begin(); // Initialize the I2C bus for the compass
  byte lowByte = EEPROM.read(0);
  byte highByte = EEPROM.read(1);
  offset = ((lowByte << 0) & 0xFF) + ((highByte << 8) & 0xFF00);
  if (DEBUG) {
    Serial.write("Power On\n");
    Serial.println(offset);
    Serial.println(lowByte);
    Serial.println(highByte);
  }
}

void getData() {
  char inChar; // Where to store the character read
  int index; // Index into array; where to store the character
  for(index = 0; index < 5; index++) {
    while (Serial.available() == 0); //Null statment to wait until there is a character waiting on the Serial line
    inChar = Serial.read(); // Read a character
    if(inChar != '\n') {
      inData[index] = inChar; // Store it
    } 
    else {
      break;
    }
  }
  inData[index] = '\0'; // Null terminate the string
}

void setServo(char servoChar, int turnAmount){
  if('R' == servoChar) {
    turnAmount = constrain(turnAmount, 1060, 1920);
    myRudderServo.writeMicroseconds(turnAmount);
  }
  else {
    turnAmount = constrain(turnAmount, 1050, 1930);
    mySailServo.writeMicroseconds(turnAmount);
  }
}

int getAmount() {
  int turnAmount;
  turnAmount = (int) strtol(inData+1, NULL, 10);
  Serial.println(turnAmount);
  return turnAmount;
}

float readCompass() {
  return cmps10.bearing(); // Print the sensor readings to the serial port.
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

float readThermometer() { // This makes me cry
  byte i;
  byte present = 0;
  byte data[12];
  byte addr[8];
  float celsius;
  
  if ( !ds.search(addr)) {
    ds.reset_search();
    delay(250);
  }
  ds.reset();
  ds.select(addr);
  ds.write(0x44, 1);        // start conversion, with parasite power on at the end
  
  delay(750);     // maybe 750ms is enough, maybe not
  
  present = ds.reset();
  ds.select(addr);    
  ds.write(0xBE);
  for ( i = 0; i < 9; i++) {           // we need 9 bytes
    data[i] = ds.read();
  }
  
  int16_t raw = (data[1] << 8) | data[0];
  raw = raw << 3; // 9 bit resolution default
  if (data[7] == 0x10) {
    raw = (raw & 0xFFF0) + 12 - data[6];
  }
  celsius = (float)raw / 16.0;
  return celsius;
}

int mod(int value){ //Wraps an Int value around 0 to 360
  int newValue;
  if(value < 0){
    newValue = value + 360;
  }
  else if(value >= 360){
    newValue = value - 360;
  }
  else{
    newValue = value;
  }
  return newValue;
}

float mod(float value){ //Wraps a Float value around 0 to 360
  float newValue;
  if(value < 0){
    newValue = value + 360;
  }
  else if(value >= 360){
    newValue = value - 360;
  }
  else{
    newValue = value;
  }
  return newValue;
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
  case 't':
    if (DEBUG) {
      Serial.print("t");
    }
    Serial.println(readThermometer()); //Read Thermometer
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
    offset = 0; //Set OffSet for the Wind Sensor
    if (DEBUG) {
      Serial.println("o");
    }
    offset = readWindSensor();
    byte lowByte = ((offset >> 0) & 0xFF);
    byte highByte = ((offset >> 8) & 0xFF);
    if (DEBUG) {
      Serial.println(offset);
      Serial.println(lowByte);
      Serial.println(highByte);
    }
    EEPROM.write(0, lowByte);
    EEPROM.write(1, highByte);
    Serial.println(1);
    break;
  }
}
