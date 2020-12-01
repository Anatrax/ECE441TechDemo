import processing.serial.*;

Serial port;                      // The serial port
int tPos = 1;                     // Position along the chart's time axis
float rawValue;                   // Raw data value from serial port
float processedValue = height/2;  // EWMA-smoothed value

void setup () {
  fullScreen();                   // Set window size
  background(0);                  // Initalize background color

  /**
    * Open first available serial port (usually something like 'COM3')
    *  with 9600 baud rate (must match baud rate from microcontroller output)
    */
  //portname = Serial.list()[0];
  port = new Serial(this, Serial.list()[0], 9600);

  port.bufferUntil('\n');         // Call serialEvent() when newline received
}

void draw () {
  // Data update happens in serialEvent() handler

  // Show the name of the serial port that got connected
  stroke(255);
  text(Serial.list()[0], 20, 20);

  int threshold = height / 2;     // Define left/right acceleration boundary

  // Draw green area between threshold and processedValue
  stroke(0,255,0);
  line(tPos, height - processedValue, tPos, threshold);

  // Draw red area between processedValue and rawValue
  stroke(255,0,0);
  line(tPos, height - rawValue, tPos, height - processedValue);

  /**
   * Some instances of 'line()' are picky about the order of the arguments,
   *  so just repeating the line drawing steps with the line endpoints flipped.
   */
  stroke(0,255,0);
  line(tPos, threshold, tPos, height - processedValue);
  stroke(255,0,0);
  line(tPos, height - processedValue, tPos, height - rawValue);

  //// Show the current value
  //stroke(255);
  //rect(0, 20, 100, 60);
  //text(processedValue, 20, 20);

  // Reset chart when full
  if (tPos >= width) {
    tPos = 0;                   // Move draw position back to start
    background(0);              // Clear chart
  } else {
    tPos++;                     // Increment horizontal position
  }
}

/**
  * This function gets called automatically by the serial port object
  */
void serialEvent (Serial port) {
  // Get ASCII string
  String inString = port.readStringUntil('\n');
  //print(inString);              // Print statement for debugging

  // Process the data
  if (inString != null) {
    inString = trim(inString);  // Trim off whitespace
    rawValue = float(inString); // Convert input text to a decimal number
    rawValue = map(rawValue, 0, 180, 0, height);  // Map to screen height

    /**
      * Data processing is just an Exponentially Weighted Moving Average (EWMA)
      *  to smooth the noisy sensor data.
      * 
      * An alpha closer to 0.0 will result in a slower reaction time (smoother).
      * An alpha closer to 1.0 will result in a faster reaction time.
      * 
      * Note: I encountered issues setting alpha at or above 0.9,
      *  so I just got it as close as I could.
      */
    float alpha = 0.08999;      // Set EWMA reaction time
    processedValue = alpha * rawValue + (1 - alpha) * processedValue;
  }
}