## ECE 209AS: Special Topics in Circuits and Embedded Systems: Security and Privacy for Embedded Systems, Cyber-Physical Systems, and Internet of Things

Hacking Rachio, the Smart Sprinkler Controller

### Proposal

### What is rachio?
XXXX

### Security attack model navigation

### Hardware:

● Serial ports exposed
● Insecure authentication mechanism used in the serial ports
● Ability to dump the  firmware over JTAG or via Flash chips

Attempt: access to the circuit board.

Result: Failed. The device is well packaged with no screws. The only way to open it is smashing the shell. But we don’t have proper tools to do so.

### Firmware, software, application:
1. Firmware
o Ability to modify firmware
o Hardcoded sensitive values in the  firmware—API keys,
passwords, staging URLs, etc.
o Ability to understand the entire functionality of the device
through the  firmware
o File system extraction from the  firmware

Attempt:
(1). Download from official website.
Result : failed. Refuse to provide.

(2). Draw from hardware
Result : failed. Due to the same reason in hardware part.

2. Mobile application
o Reverse engineering the mobile app
o Dumping source code of the mobile application
o Side channel data leakage
o Runtime manipulation attacks
o Insecure network communication
o Outdated 3rd party libraries and SDKs

Attempt:
Analyze the Android app.

### Timeline

XXX

### 可能攻击的点：

1. Find firmware download url
Result: failed. Encrypted in somewhere. Cannot find it mannually.  

2. Password
Steal password

3. Number of devices
To know the number of devices.
Device database

4. Schedule
ViewScheduleActivity.java

5. *activity.java
Eg. ChooseWeatherStationActivity.java
Wrong weather station. Or steal information of the weatherstation.  

### Reference
【1】IoT Hackers Handbook: An ultimate guide to hacking the Internet of Things
