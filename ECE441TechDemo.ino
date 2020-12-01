int xPin = 2; //Pins for Accelerometer
int yPin = 3;
int temp; //temporary calculations value
int accelerationX, accelerationY = 90; //Servo positions, initializes to middle of field

void setup() {
    // Initialize the serial communication:
    Serial.begin(9600);
}

void loop() {
  float pulseX = (((pulseIn(xPin, HIGH) - 3700) * 18.0) / 250); //reading from accelerometer and converting value to 0-180
  float pulseY = (((pulseIn(yPin, HIGH) - 3700) * 18.0) / 250);
 
  temp = (accelerationX - pulseX); //Determines difference between new readings and current position
  temp = abs(temp);
  accelerationX = pulseX;
  if (accelerationX > 180) accelerationX = 180; //sets max value
  if (accelerationX < 0) accelerationX = 0; //sets minimum value

  temp = (accelerationY - pulseY); //Determines difference between new readings and current position
  temp = abs(temp);
  accelerationY = pulseY;
  if (accelerationY > 180) accelerationY = 180; //sets max value
  if (accelerationY < 0) accelerationY = 0; //sets minimum value

//  Serial.println(accelerationX);
  Serial.println(accelerationY);

  delay(1); //Tiny delay to reduce electrical noise
}

