package com.ofg.infrastructure.property.decrypt

import groovy.transform.PackageScope
import spock.lang.IgnoreIf
import spock.lang.Specification

import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthTestFixture.isPropertiesDecryptionTestingExplicitDisabled
import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthTestFixture.strongEncryptionSupported

@IgnoreIf({ isPropertiesDecryptionTestingExplicitDisabled() })
class BrokenJceInstallationSpec extends Specification {

    def "JCE US has to be installed for property values decryption - see exception message how to disable that"() {
        expect:
            if (!isStrongEncryptionSupported()) {
                throw new JceForOracleJdkOrOpenJdkAreRequiredForPropertyDecryption()
            }
    }
}

@PackageScope
class JceForOracleJdkOrOpenJdkAreRequiredForPropertyDecryption extends UnsupportedOperationException {
    JceForOracleJdkOrOpenJdkAreRequiredForPropertyDecryption() {
        super("""It seems your JDK does not support strong encryption.

Install Java Cryptography Extension (JCE) Unlimited Strength for Oracle JDK or use OpenJDK to have support for property decryption.
If you don't need to test that feature you can disable related tests with '-DdisableDecryptionTests=true' - it does not affect built JAR/WAR.
To force running of all decryption tests in that environment set that flag to "false".
""")
    }
}
