package com.ofg.infrastructure.web.logging.wrapper

import spock.lang.Specification

import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

class DelegatingServletOutputStreamSpec extends Specification {

    def 'Should return IllegalStateException when source is null'() {
        when:
            new DelegatingServletOutputStream(null)
        then:
            thrown(IllegalArgumentException)
    }

    def 'Should call close method on source  when delegate call close method'() {
        given:
            ServletOutputStream source = Mock(ServletOutputStream)
            DelegatingServletOutputStream outputStream = new DelegatingServletOutputStream(source)
        when:
            outputStream.close()
        then:
            1 * source.close()
    }

    def 'Should call ready method on source  when delegate call ready method'() {
        given:
            ServletOutputStream source = Mock(ServletOutputStream)
            DelegatingServletOutputStream outputStream = new DelegatingServletOutputStream(source)
        when:
            outputStream.isReady()
        then:
            1 * source.isReady()
    }

    def 'Should set WriterListener on source when delegate call setter method'() {
        given:
            ServletOutputStream source = Mock(ServletOutputStream)
            WriteListener writeListener = Mock(WriteListener)
            DelegatingServletOutputStream outputStream = new DelegatingServletOutputStream(source)
        when:
            outputStream.setWriteListener(writeListener)
        then:
            1 * source.setWriteListener(writeListener)
    }

}
