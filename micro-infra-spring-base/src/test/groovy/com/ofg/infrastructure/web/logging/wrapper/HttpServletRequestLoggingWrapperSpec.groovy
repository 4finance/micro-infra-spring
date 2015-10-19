package com.ofg.infrastructure.web.logging.wrapper

import spock.lang.Specification

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest

class HttpServletRequestLoggingWrapperSpec extends Specification {

    def 'Should return IllegalStateException when request input stream access ends with IOException'() {
        given:
            HttpServletRequest request = Mock(HttpServletRequest)
            request.getInputStream() >> {throw new IOException('any error')}
        when:
            new HttpServletRequestLoggingWrapper(request)
        then:
            thrown(IllegalStateException)
    }

    def 'Should return BufferedReader after wrapper initialization and reader call'() {
        given:
            HttpServletRequest request = Mock(HttpServletRequest)
            request.getInputStream() >> Mock(ServletInputStream)
            HttpServletRequestLoggingWrapper wrapper = new HttpServletRequestLoggingWrapper(request)
        expect:
            wrapper.getReader()
    }
}
