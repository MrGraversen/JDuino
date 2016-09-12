// Declare a helper variable
String content = "";

// Setup is run once when the program starts (i.e. when the device is powered)
void setup() 
{
  // Initialize serial port at 9.6 kbit/s
  Serial.begin(9600);
}

// Runs for as long as the program lives (i.e. as long as the device is powered)
void loop() 
{
  // As long as data is streaming over serial...
  if(Serial.available() > 0)
  {
    // Make sure the content variable is clear
    content = "";
    // Read stream until a line break character appears
    content = Serial.readStringUntil('\n');

    // if content was received...
    if (content != "") 
    {
      // print it!
      Serial.println(content);
    }
  }
}
