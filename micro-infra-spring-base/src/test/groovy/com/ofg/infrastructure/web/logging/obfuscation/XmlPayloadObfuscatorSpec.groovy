package com.ofg.infrastructure.web.logging.obfuscation

import spock.lang.Specification
import spock.lang.Unroll

class XmlPayloadObfuscatorSpec extends Specification {

    @Unroll
    def 'Should return input content when logic throws exception for : [#content]'() {
        given:
            ObfuscationFieldStrategy obfuscationFieldStrategy = Mock(ObfuscationFieldStrategy)
            XmlPayloadObfuscator obfuscator = new XmlPayloadObfuscator(obfuscationFieldStrategy)
        when:
            String result = obfuscator.process(content, Collections.emptyList())
        then:
            result == content
        where:
            content << ['no xml content', '', ' ', null]
    }
}
