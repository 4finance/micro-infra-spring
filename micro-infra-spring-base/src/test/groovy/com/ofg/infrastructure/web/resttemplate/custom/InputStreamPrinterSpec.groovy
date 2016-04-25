package com.ofg.infrastructure.web.resttemplate.custom

import spock.lang.Specification
import spock.lang.Unroll

class InputStreamPrinterSpec extends Specification {

    def 'should abbreviate "#source" to "#target" with max chars #maxChars'() {
        given:
            InputStream input = new ByteArrayInputStream(source.bytes)
        when:
            String abbreviated = InputStreamPrinter.abbreviate(input, maxChars)
        then:
            abbreviated == target
        where:
            source      | maxChars || target
            ''          | 0        || ''
            ''          | 10       || ''
            'abc'       | 10       || 'abc'
            'abcd'      | 4        || 'abcd'
            'abcde'     | 4        || 'a...'
            'abcdefghi' | 8        || 'abcde...'
            'abcdefghi' | 10       || 'abcdefghi'
    }

}
