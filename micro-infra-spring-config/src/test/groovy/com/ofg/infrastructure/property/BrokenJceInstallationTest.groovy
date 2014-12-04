package com.ofg.infrastructure.property

import groovy.transform.PackageScope
import spock.lang.IgnoreIf
import spock.lang.Specification

import javax.crypto.Cipher

import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthTestFixture.propertiesDecryptionTestingEnabled

//Placed in generic package to be reported earlier by Gradle when JCE US is not installed
@IgnoreIf({ isPropertiesDecryptionTestingEnabled() })
class BrokenJceInstallationTest extends Specification {

    def "JCE US has to be installed for property values decryption - see exception message how to disable that"() {
        expect:
            def strength = Cipher.getMaxAllowedKeyLength("AES")
            if (strength <= 128) {
                throw new JceForOracleJdkOrOpenJdkAreRequiredForPropertyDecryption(strength)
            }
    }
}

@PackageScope
class JceForOracleJdkOrOpenJdkAreRequiredForPropertyDecryption extends UnsupportedOperationException {
    JceForOracleJdkOrOpenJdkAreRequiredForPropertyDecryption(int strength) {
        super("""It seems your JDK does not support strong encryption (only ${String.valueOf(strength)} for AES).

Install Java Cryptography Extension (JCE) Unlimited Strength for Oracle JDK or use OpenJDK to have support for property decryption.
If you don't need to test that feature you can disable related tests with '-DdisableDecryptionTests=true' - it does not affect built JAR/WAR
""")
    }
}
