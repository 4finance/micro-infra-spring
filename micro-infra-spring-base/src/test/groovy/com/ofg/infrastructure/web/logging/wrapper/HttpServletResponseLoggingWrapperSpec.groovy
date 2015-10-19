package com.ofg.infrastructure.web.logging.wrapper

import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

class HttpServletResponseLoggingWrapperSpec extends Specification {

    def 'Should return IllegalStateException when response output stream access ends with IOException'() {
        given:
            HttpServletResponse response = Mock(HttpServletResponse)
            response.getOutputStream() >> {throw new IOException('any error')}
        when:
             new HttpServletResponseLoggingWrapper(response)
        then:
            thrown(IllegalStateException)
    }
}
