# JDuino

JDuino is an event-driven library for exchanging serial data between a Java client and an Arduino device.

**Background**  
I was always intrigued to make my gizmos speak with some higher-level software to enable them to do higher-level things, such as communicate something over a socket, or access data in a relational database.  
Granted, you can do most of this stuff with the *right* gizmos, but what if you have some extremely primitive hardware to work with and you just want it to, say, deliver some data to some data store or do something when something triggers.  
Well, this is what the library is made for.

The "rxtxSerial" project, which this was built on top of, was abandoned about 9 years ago as of this writing (sept. 2016). What's left is a not-very-stable library with a good bit of leaky abstractions and a lot of "to be fixed in the next version"-bugs. It is very hard to use the "rxtxSerial" library but it's the only thing we've got to work with, unfortunately.

Please find some examples in the folder structure.

Here's an image of some bastard contraption I made. It has an RFID reader on it, and it was used to authenticate users, using their RFID cards (also pictured), against a MySQL database.  
It was made with what eventually became this library (and some Arduino code, of coourse).

![alt text](http://media.martinbytes.com/IMG_0012.JPG "RFID Reader Circuit")

*Currently untested under Linux!*