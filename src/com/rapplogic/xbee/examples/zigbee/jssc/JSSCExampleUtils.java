package com.rapplogic.xbee.examples.zigbee.jssc;

import jssc.SerialPortList;

/**
 * Created by allanlang on 10/05/2014.
 */
public class JSSCExampleUtils {

    private JSSCExampleUtils() {
    }

    public static final String getDefaultPortName() {
        String[] portNames = SerialPortList.getPortNames();
        if (portNames.length != 1) {
            throw new RuntimeException(String.format("%d ports found but one was expected", portNames.length));
        }
        return portNames[0];
    }

}
