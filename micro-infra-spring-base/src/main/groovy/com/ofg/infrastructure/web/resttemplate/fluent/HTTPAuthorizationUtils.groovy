package com.ofg.infrastructure.web.resttemplate.fluent

import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64

import static java.nio.charset.StandardCharsets.US_ASCII

@CompileStatic
class HTTPAuthorizationUtils {

    /**
     * Encodes credentials for HTTP basic authentication
     */
    public static String encodeCredentials(String username, String password) {
        String plainCreds = username + ":" + password
        byte[] plainCredsBytes = plainCreds.getBytes(US_ASCII)
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
        return new String(base64CredsBytes)
    }

}
