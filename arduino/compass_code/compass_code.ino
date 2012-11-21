/**
 * Perform a basic test to verify communication with the HMC6343 compass. 
 * It queries the compass for heading, pitch, and roll data at 10Hz.
 */
#include <Wire.h>
 
/**
 * Define some useful constants.
 */
#define HMC6343_ADDRESS 0x19
#define HMC6343_HEADING_REG 0x50
 
void setup() {
  Wire.begin(); // Initialize the I2C bus
  Serial.begin(115200); // Initialize the serial bus
}
 
void loop() {
  byte highByte, lowByte;
 
  Wire.beginTransmission(HMC6343_ADDRESS);    // Start communicating with the HMC6343 compasss
  Wire.write(HMC6343_HEADING_REG);             // Send the address of the register that we want to read
  Wire.endTransmission();
 
  Wire.requestFrom(HMC6343_ADDRESS, 6);    // Request six bytes of data from the HMC6343 compasss
  while(Wire.available() < 1);             // Busy wait while there is no byte to receive
 
  highByte = Wire.read();              // Reads in the bytes and convert them into proper degree units.
  lowByte = Wire.read();
  float heading = ((highByte << 8) + lowByte) / 10.0; // the heading in degrees
 
  highByte = Wire.read();
  lowByte = Wire.read();
  float pitch = ((highByte << 8) + lowByte) / 10.0;   // the pitch in degrees
 
  highByte = Wire.read();
  lowByte = Wire.read();
  float roll = ((highByte << 8) + lowByte) / 10.0;    // the roll in degrees
 
  Serial.print("Heading=");             // Print the sensor readings to the serial port.
  Serial.print(heading);
  Serial.print(", Pitch=");
  Serial.print(pitch);
  Serial.print(", Roll=");
  Serial.println(roll);
 
  delay(100); // Do this at approx 10Hz
}
