#include <Servo.h> 
 
Servo myservo;  // create servo object to control a servo 
                // a maximum of eight servo objects can be created 

char inData[5]; // Allocate some space for the string
char inChar=-1; // Where to store the character read

void setup() {
    Serial.begin(9600); //Begin
    Serial.write("Power On\n");
}

void getData() {
  byte index = 0; // Index into array; where to store the character
  for(index = 0; index<5; index++)
    {
        while (Serial.available() == 0);
            inChar = Serial.read(); // Read a character
            if(inChar != '\n'){
                inData[index] = inChar; // Store it
            }
            else{break;} 
          
    }
    inData[index] = '\0'; // Null terminate the string
}

void setServo(char servoChar, int turnAmount){
  if(servoChar = 'R'){
    myservo.attach(9);
    myservo.write(turnAmount);
  }
  else{
    myservo.attach(10);
    myservo.write(turnAmount);
  }
}

int getAmount(){
  char newArray[3];
  int turnAmount;
  newArray[0] = inData[1];
  newArray[1] = inData[2];
  newArray[2] = inData[3];
  turnAmount = (int) strtol(newArray, NULL, 10);
  //Serial.print(turnAmount);
  return turnAmount;
}

void loop()
{
    getData();
    Serial.write(inData);
    switch(inData[0]){
      case 'c' : Serial.println("CommandCharacter = c"); //Compass Read
      break;
      case 'w' : Serial.println("CommandCharacter = w"); // Wind Sensor Read
      break;
      case 'r' : Serial.println("CommandCharacter = r"); // Rudder Set
                 setServo('R', getAmount());
      break;
      case 's' : Serial.println("CommandCharacter = s"); // Sail Set
                 setServo('S', getAmount());
      break;
    }
}
