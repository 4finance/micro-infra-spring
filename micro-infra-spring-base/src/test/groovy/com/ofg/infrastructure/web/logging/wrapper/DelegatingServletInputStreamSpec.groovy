package com.ofg.infrastructure.web.logging.wrapper

import spock.lang.Specification

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream

class DelegatingServletInputStreamSpec extends Specification {


    def 'Should return IllegalStateException when source is null'() {
        when:
            new DelegatingServletInputStream(null)
        then:
            thrown(IllegalArgumentException)
    }

    def 'Should call isReady method on source when delegate call isReady method'() {
        given:
            ServletInputStream source = Mock(ServletInputStream)
            DelegatingServletInputStream inputStream = new DelegatingServletInputStream(source)
        when:
            inputStream.isReady()
        then:
            1 * source.isReady()
    }

    def 'Should isFinished method on source when delegate call isFinished method'() {
        given:
            ServletInputStream source = Mock(ServletInputStream)
            DelegatingServletInputStream inputStream = new DelegatingServletInputStream(source)
        when:
            inputStream.isFinished()
        then:
            1 * source.isFinished()
    }

    def 'Should set readListener on source when delegate call setter method'() {
        given:
            ServletInputStream source = Mock(ServletInputStream)
            DelegatingServletInputStream inputStream = new DelegatingServletInputStream(source)
            ReadListener readListener = Mock(ReadListener)
        when:
            inputStream.setReadListener(readListener)
        then:
            1 * source.setReadListener(readListener)
    }

}
