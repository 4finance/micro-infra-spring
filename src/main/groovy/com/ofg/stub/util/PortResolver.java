package com.ofg.stub.util;

import com.google.common.net.HostAndPort;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class PortResolver {


    /**
     * Returns port number from service url
     *
     * @param url host and port url in format hostname:port
     *
     * @return -1 if no port present or port
     *
     * @throws IllegalArgumentException when url don't have colon
     */
    public static int getPortFromUrlOrRandom(String url) {
        checkNotNull(url);
        int colonIndex = url.indexOf(":");
        checkArgument(colonIndex != -1);
        String portNumberString = url.substring(colonIndex + 1);
        if (isNullOrEmpty(portNumberString)) {
            return -1;
        } else {
            return HostAndPort.fromString(url).getPort();
        }

    }
}
