package com.ofg.infrastructure.property.decrypt

class JceUnlimitedStrengthTestFixture {

    static boolean isPropertiesDecryptionTestingEnabled() {
        System.getProperty("disableDecryptionTests") == "true"
    }
}
