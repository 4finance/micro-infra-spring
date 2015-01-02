package com.ofg.stub.util;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class PortResolver {


    /**
     * Returns port number from service url
     *
     * @param url host and port url in format hostname:port
     * @return empty if no port present or port
     * @throws IllegalArgumentException when url don't have colon
     */
    public static Optional<Integer> tryGetPortFromUrl(String url) {
        checkNotNull(url);
        if (isNullOrEmpty(StringUtils.substringAfterLast(url, ":"))) {
            return Optional.absent();
        } else {
            return Optional.of(HostAndPort.fromString(url).getPort());
        }

    }
}
