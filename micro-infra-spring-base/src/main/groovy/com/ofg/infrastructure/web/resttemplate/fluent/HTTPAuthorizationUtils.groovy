package com.ofg.infrastructure.web.resttemplate.fluent

import org.apache.commons.codec.binary.Base64

class HTTPAuthorizationUtils {

    /**
     * Encodes credentials for HTTP basic authentication
     * @param username
     * @param password
     * @return
     */
    public static String encodeCredentials(String username, String password) {
        String plainCreds = username + ":" + password
        byte[] plainCredsBytes = plainCreds.getBytes("US-ASCII")
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
        return new String(base64CredsBytes)
    }

}
