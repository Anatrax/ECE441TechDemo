int xPin = 3; // Pins for accelerometer
int yPin = 4;
int accelerationX, accelerationY = 90; // Variables to hold acceleration data

void setup() {
    // Initialize serial communication
    Serial.begin(9600);
}

void loop() {
  // Read in acceleration data
  float pulseX = getAcceleration(xPin);
  float pulseY = getAcceleration(yPin);

  // Clip values
  accelerationX = rangeClip((int) pulseX, 0, 180);
  accelerationY = rangeClip((int) pulseY, 0, 180);

  // Send sensor data over serial connection
  Serial.println(accelerationX);
  //Serial.println(accelerationY);

  delay(1); // Short delay to reduce electrical noise
}

/**
 * @brief Limits values to a given range
 * 
 * @param value - The value to clip
 * @param min_value - The minimum value of the range (inclusive)
 * @param max_value - The maximum value of the range (inclusive)
 * 
 * @returns the original value if within range, otherwise min if value < range or max if value > range
 */
int rangeClip(int value, int min_range, int max_range) {
  int clipped_value = value;

  // Set minimum
  if(clipped_value < min_range) {
    clipped_value = min_range;
  }

  // Set maximum
  if(clipped_value > max_range) {
    clipped_value = max_range;
  }

  return clipped_value;
}

/**
 * @brief Reads in data from accelerometer
 * 
 * @param pin - The pin number the accelerometer is connected to
 * 
 * @returns angle between 0 - 180 degrees
 */
float getAcceleration(int pin) {
  return (((pulseIn(xPin, HIGH) - 3700) * 18.0) / 250);
}

