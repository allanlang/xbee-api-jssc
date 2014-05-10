package com.rapplogic.xbee.examples.zigbee.jssc;

import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.jssc.JSSCXBeeConnection;
import jssc.SerialPortException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ZBIOSampleReceiveExample {

    private static final Logger LOG = Logger.getLogger(ZBIOSampleReceiveExample.class);

    private String portName = JSSCExampleUtils.getDefaultPortName();

    private XBee xbee;

    public static void main(String[] args) throws SerialPortException, XBeeException, InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        new ZBIOSampleReceiveExample();
    }

    public ZBIOSampleReceiveExample() throws SerialPortException, XBeeException, InterruptedException {
        LOG.info(String.format("Will use port %s", portName));

        try {
            xbee = new XBee();
            xbee.initProviderConnection(new JSSCXBeeConnection(portName));
            LOG.info("Connection to XBee initialised successfully");
            receive(10);
        } finally {
            // Ensure xbee is always closed otherwise port will be in use the next time we try to use it
            LOG.info("Closing XBee connection");
            xbee.close();
        }
    }

    private void receive(int maxPackets) throws InterruptedException {
        int processedPackets = 0;

        PacketListener listener = new PacketListener() {

            public void processResponse(XBeeResponse response) {
                if(response instanceof ZNetRxIoSampleResponse) {
                    ZNetRxIoSampleResponse sample = (ZNetRxIoSampleResponse)response;
                    LOG.info(String.format("Received sample [A0:%d]", sample.getAnalog0()));
                } else {
                    LOG.warn(String.format("Unexpected response type %s, ignoring", response.getClass().getName()));
                }
                synchronized (this) {
                    notify();
                }
            }

        };

        xbee.addPacketListener(listener);

        while (processedPackets < maxPackets) {
            synchronized (listener) {
                listener.wait();
                processedPackets++;
            }
        }
    }

}
