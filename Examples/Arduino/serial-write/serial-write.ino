// Setup is run once when the program starts (i.e. when the device is powered)
void setup() 
{
  // Initialize serial port at 9.6 kbit/s
  Serial.begin(9600);
}

// Runs for as long as the program lives (i.e. as long as the device is powered)
void loop() 
{
  // Let's print something over serial
  Serial.println("Hello from Arduino Uno!");
  // Wait for a bit as to not spam too much
  delay(500);
}
