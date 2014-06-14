package uk.ac.mdx.cs.asip;

/* 
 * @author Franco Raimondi
 * 
 * A simple implementation with serial communication and only I/O services
 * 
 */
import uk.ac.mdx.cs.asip.AsipClient;
import uk.ac.mdx.cs.asip.AsipWriter;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public class SimpleSerialBoard {

	// This board uses serial communication (provided by jssc)
	SerialPort serialPort;
	
	// The client for the aisp protocol
	AsipClient asip;

	// This constructor takes the name of the serial port and it
	// creates the serialPort object.
	// We attach a listener to the serial port with SerialPortReader; this
	// listener calls the aisp method to process input.
	public SimpleSerialBoard(String port) {
		
		serialPort = new SerialPort(port);

		try {
			serialPort.openPort();// Open port
			serialPort.setParams(57600, 8, 1, 0);// Set params
			int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS
					+ SerialPort.MASK_DSR;// Prepare mask
			serialPort.setEventsMask(mask);// Set mask
			serialPort.addEventListener(new SerialPortReader());// Add
																// SerialPortEventListener
		} catch (SerialPortException ex) {
			System.out.println(ex);
		}

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		asip = new AsipClient(new SimpleWriter());
	}
	
	// The following 5 methods are just a replica from the asip class.
	public int digitalRead(int pin) {
		return asip.digitalRead(pin);
	}	
	public int analogRead(int pin) {
		return asip.analogRead(pin);
	}
	public void setPinMode(int pin, int mode) {
		asip.setPinMode(pin, mode);
	}
	public void digitalWrite(int pin, int value) {
		asip.digitalWrite(pin, value);
	}
	public void analoglWrite(int pin, int value) {
		asip.analoglWrite(pin, value);
	}
	
	// As described above, SimpleSerialBoard writes messages to
	// the serial port.
    private class SimpleWriter implements AsipWriter {
        public void write(String val) {
          try {	
			serialPort.writeString(val);
          } catch (SerialPortException e) {
        	  // TODO Auto-generated catch block
        	  e.printStackTrace();
          }	
        }
    }
	
	// A class for a listener that calls the processInput method of
	// the AispClient.
	private class SerialPortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR()){//If data is available
            	try {
            		String val = serialPort.readString();
            		asip.processInput(val);
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
	
}
