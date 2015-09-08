package com.ofg.infrastructure.web.logging.wrapper

import groovy.transform.CompileStatic
import org.apache.commons.io.input.TeeInputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream

@CompileStatic
class DelegatingServletInputStream extends ServletInputStream{

    private final  ServletInputStream source
    private final ByteArrayOutputStream target
    private final TeeInputStream wrapper

    DelegatingServletInputStream(ServletInputStream source) {
        assert source, "source ServletInputStream must not be null"
        this.target = new ByteArrayOutputStream()
        this.wrapper = new TeeInputStream(source, this.target)
    }

    @Override
    boolean isFinished() {
        return this.source.isFinished()
    }

    @Override
    boolean isReady() {
        return this.source.isReady()
    }

    @Override
    void setReadListener(ReadListener readListener) {
        this.source.setReadListener(readListener)
    }

    @Override
    int read() throws IOException {
        return this.wrapper.read()
    }

    byte[] getBytes() {
        return this.target.toByteArray()
    }
}
