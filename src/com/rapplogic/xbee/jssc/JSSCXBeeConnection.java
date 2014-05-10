package com.rapplogic.xbee.jssc;

import com.rapplogic.xbee.XBeeConnection;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JSSCXBeeConnection implements XBeeConnection, SerialPortEventListener {

    private static final Logger LOG = Logger.getLogger(JSSCXBeeConnection.class);

    private static final int DEFAULT_BAUD_RATE = SerialPort.BAUDRATE_9600;

    private SerialPort port;

    private InputStream inputStream;

    private OutputStream outputStream;

    public JSSCXBeeConnection(String portName) throws SerialPortException {
        this(portName, DEFAULT_BAUD_RATE);
    }

    public JSSCXBeeConnection(String portName, int baudRate) throws SerialPortException {
        if (portName == null || "".equals(portName)) {
            throw new IllegalArgumentException("Port name is required");
        }
        port = new SerialPort(portName);
        port.openPort();
        port.setParams(baudRate, 8, 1, 0);
        int mask = SerialPort.MASK_RXCHAR;
        port.setEventsMask(mask);
        port.addEventListener(this);
        inputStream = new UnsignedByteSerialInputStream(port);
        outputStream = new BufferedOutputStream(new SerialOutputStream(port));
    }

    public void close() {
        try {
            port.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            LOG.trace(String.format("RXCHAR event received, value %d", event.getEventValue()));
            try {
                if (inputStream.available() > 0) {
                    synchronized (this) {
                        this.notify();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOG.warn(String.format("Unexpected event type %d received", event.getEventType()));
        }
    }

}
