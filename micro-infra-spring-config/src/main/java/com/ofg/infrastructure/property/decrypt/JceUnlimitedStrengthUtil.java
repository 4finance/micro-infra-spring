package com.ofg.infrastructure.property.decrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

import static java.lang.invoke.MethodHandles.lookup;

public class JceUnlimitedStrengthUtil {

    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    public static void printWarningIfStrongEncryptionIsNotSupported() {
        if (!isStrongEncryptionSupported()) {
            log.warn("WARNING. It seems your JDK does not support strong encryption. " +
                    "Install Java Cryptography Extension (JCE) Unlimited Strength for Oracle JDK or " +
                    "use OpenJDK to have support for property decryption.");
        }
    }

    public static boolean isStrongEncryptionSupported() {
        try {
            return Cipher.getMaxAllowedKeyLength("AES") > 128;
        } catch (NoSuchAlgorithmException e) {
            log.warn("Error while checking if strong encryption is supported", e);
            return false;
        }
    }
}
