# UCLA ECE 209AS: Special Topics in Circuits and Embedded Systems
Hacking Rachio, the Smart WiFi Sprinkler Controller

## What is Rachio?
XXXX

## Security attack model navigation

### Hardware  
- Serial ports exposed  
- Insecure authentication mechanism used in the serial ports  
- Ability to dump the firmware over JTAG or via Flash chips  

**Attempt**: access to the circuit board.  

**Result**: Failed. The device is well packaged with no screws. The only way to open it is smashing the shell. But we don’t have proper tools to do so.  

### Firmware, software, application
1. Firmware
- Ability to modify firmware  
- Hardcoded sensitive values in the  firmware—API keys,
passwords, staging URLs, etc.  
- Ability to understand the entire functionality of the device
through the firmware  
- File system extraction from the firmware  

**Attempt**:  
- Download from official website  
**Result**: failed. Refuse to provide.

- Draw from hardware  
**Result**: failed. Due to the same reason in hardware part.  

2. Mobile application
- Reverse engineering the mobile app  
- Dumping source code of the mobile application  
- Side channel data leakage  
- Runtime manipulation attacks  
- Insecure network communication  
- Outdated 3rd party libraries and SDKs  

**Attempt**:
Analyze the Android app.

## Timeline
XXX
  
## Reference
【1】IoT Hackers Handbook: An ultimate guide to hacking the Internet of Things
