package com.ofg.infrastructure.web.logging.obfuscation

import spock.lang.Specification

class XmlPayloadObfuscatorSpec extends Specification {

    def 'Should return input content when logic throws exception'() {
        given:
            ObfuscationFieldStrategy obfuscationFieldStrategy = Mock(ObfuscationFieldStrategy)
            XmlPayloadObfuscator obfuscator = new XmlPayloadObfuscator(obfuscationFieldStrategy)
        when:
            String result = obfuscator.process('no xml content', Collections.emptyList())
        then:
            result == 'no xml content'
    }
}
