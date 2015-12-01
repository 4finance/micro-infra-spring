package com.ofg.infrastructure.web.logging.obfuscation

import spock.lang.Specification
import spock.lang.Unroll

class JsonPayloadObfuscatorSpec extends Specification {

    @Unroll
    def 'Should return input content when logic throws exception for : [#content]'() {
        given:
            ObfuscationFieldStrategy obfuscationFieldStrategy = Mock(ObfuscationFieldStrategy)
            JsonPayloadObfuscator obfuscator = new JsonPayloadObfuscator(obfuscationFieldStrategy)
        when:
            String result = obfuscator.process(content, ['filed'])
        then:
            result == content
        where:
            content << ['[[{]} broken content', '', ' ', null]
    }
}
