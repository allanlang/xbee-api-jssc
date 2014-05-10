package com.rapplogic.xbee.examples.zigbee.jssc;

import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.jssc.JSSCXBeeConnection;
import jssc.SerialPortException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ConnectionInitialisationExample {

    private static final Logger LOG = Logger.getLogger(ConnectionInitialisationExample.class);

    private String portName = JSSCExampleUtils.getDefaultPortName();

    private XBee xbee;

    public static void main(String[] args) throws SerialPortException, XBeeException {
        PropertyConfigurator.configure("log4j.properties");
        new ConnectionInitialisationExample();
    }

    public ConnectionInitialisationExample() throws SerialPortException, XBeeException {
        LOG.info(String.format("Will use port %s", portName));

        try {
            xbee = new XBee();
            xbee.initProviderConnection(new JSSCXBeeConnection(portName));
            LOG.info("Connection to XBee initialised successfully");
        } finally {
            // Ensure xbee is always closed otherwise port will be in use the next time we try to use it
            LOG.info("Closing XBee connection");
            xbee.close();
        }
    }

}
