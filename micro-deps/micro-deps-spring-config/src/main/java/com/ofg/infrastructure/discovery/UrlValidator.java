package com.ofg.infrastructure.discovery;

import java.net.InetAddress;

class UrlValidator {

    static InetAddress isValidUrl(String string) {
        try {
            return InetAddress.getByName(string);
        } catch (Exception e) {
            return null;
        }
    }

}
