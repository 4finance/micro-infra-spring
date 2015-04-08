package com.ofg.infrastructure.property.decrypt

import spock.lang.IgnoreIf
import spock.lang.Specification

import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthUtil.printWarningIfStrongEncryptionIsNotSupported
import static com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthUtil.strongEncryptionSupported

@IgnoreIf({ strongEncryptionSupported })
class BrokenJceInstallationWarningSpec extends Specification {

    def "WARNING. JCE US has to be installed for property values decryption"() {
        expect:
            printWarningIfStrongEncryptionIsNotSupported()
    }
}
