import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ECE441TechDemo extends PApplet {



Serial port;                      // The serial port
String portname;                  // The serial port name

int tPos;                         // Position along the chart's time axis

float rawValue;                   // Raw data value from serial port
float processedValue;             // EWMA-smoothed value

/**
  * Opens first available serial port (usually something like 'COM3')
  *  with 9600 baud rate (must match baud rate from microcontroller output)
  */
public void connect() {
  while(Serial.list().length == 0);  // Wait until board connects
  portname = Serial.list()[0];       // Get first available port name
  port = new Serial(this, portname, 9600);  // Connect to port
  port.bufferUntil('\n');            // Call serialEvent() when newline received

  // Initialize variables
  tPos-= 90;                         // Offset initial buffer delay
  int windowHeight = height + 20;    // 'height' value is 20 pixels off
  rawValue = windowHeight/2;
  processedValue = windowHeight/2;
}
  

public void setup () {
    // Set window size
  connect();     // Initialize serial communication
  background(0); // Set background color
}

public void draw () {
  // Data update happens in serialEvent() handler

  int windowHeight = height + 20;   // 'height' value is 20 pixels off
  int threshold = windowHeight / 2; // Define left/right acceleration boundary

  // Try reconnecting if disconnected
  if(Serial.list().length == 0) connect();

  // Show the name of the serial port that got connected
  fill(255); stroke(255); textSize(12);
  text(portname, 20, 20);

  // Draw green area between threshold and processedValue
  stroke(0,255,0);
  line(tPos, threshold, tPos, windowHeight - processedValue);

  // Draw red area between processedValue and rawValue
  stroke(255,0,0);
  line(tPos, windowHeight - processedValue, tPos, windowHeight - rawValue);

  // Show current angle (mapped back to 0 - 180 degrees)
  fill(0); stroke(255);
  rect(10, 30, 180, 40);
  fill(255); textSize(32);
  text(map(processedValue, 0, windowHeight, 0, 180), 20, 60);

  // Reset chart when full
  if (tPos >= width) {
    tPos = 0;                   // Move draw position back to start
    background(0);              // Clear chart
  } else tPos++;                // Increment horizontal position
}

/**
  * This function gets called automatically by the serial port object
  */
public void serialEvent (Serial port) {
  int windowHeight = height + 20;  // 'height' value is 20 pixels off

  String inString = port.readStringUntil('\n'); // Get ASCII string

  // Process the data
  if (inString != null) {
    inString = trim(inString);  // Trim off whitespace
    rawValue = PApplet.parseFloat(inString); // Convert input text to a decimal number

    // If inString wasn't numerical
    if(Float.isNaN(rawValue)) {
      rawValue = windowHeight/2; // Set to default
    } else {
      // Otherwise, scale value to fit the window height
      rawValue = map(rawValue, 0, 180, 0, windowHeight);
    }

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
    float alpha = 0.08999f;      // Set EWMA reaction time
    processedValue = alpha * rawValue + (1 - alpha) * processedValue;
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "ECE441TechDemo" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
