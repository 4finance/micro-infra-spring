package com.ofg.infrastructure.property.decrypt

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import javax.crypto.Cipher

@CompileStatic
@PackageScope
class JceUnlimitedStrengthTestFixture {

    static boolean isPropertiesDecryptionTestingExplicitDisabled() {
        System.getProperty("disableDecryptionTests") == "true"
    }

    static boolean isPropertiesDecryptionTestingEnforced() {
        System.getProperty("disableDecryptionTests") == "false"
    }

    static boolean isStrongEncryptionSupported() {
        Cipher.getMaxAllowedKeyLength("AES") > 128
    }

    static boolean shouldDecryptionTestsBeExecuted() {
        (isStrongEncryptionSupported() && !isPropertiesDecryptionTestingExplicitDisabled()) ||
                (!isStrongEncryptionSupported() && isPropertiesDecryptionTestingEnforced()) ||
                isStrongEncryptionSupported()
    }
}
