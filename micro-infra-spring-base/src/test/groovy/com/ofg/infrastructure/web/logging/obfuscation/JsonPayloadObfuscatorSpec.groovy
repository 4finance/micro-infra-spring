package com.ofg.infrastructure.web.logging.obfuscation

import spock.lang.Specification

class JsonPayloadObfuscatorSpec extends Specification {

    def 'Should return input content when logic throws exception'() {
        given:
            ObfuscationFieldStrategy obfuscationFieldStrategy = Mock(ObfuscationFieldStrategy)
            JsonPayloadObfuscator obfuscator = new JsonPayloadObfuscator(obfuscationFieldStrategy)
        when:
            String result = obfuscator.process('[[{]} broken content', ['filed'])
        then:
            result == '[[{]} broken content'
    }
}
