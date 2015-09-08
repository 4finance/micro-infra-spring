package com.ofg.infrastructure.web.logging.wrapper

import groovy.transform.CompileStatic
import org.apache.commons.io.output.TeeOutputStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

@CompileStatic
class DelegatingServletOutputStream extends ServletOutputStream {

    private final ServletOutputStream source
    private final ByteArrayOutputStream target
    private final TeeOutputStream wrapper

    DelegatingServletOutputStream(ServletOutputStream source) {
        assert source, "source ServletOutputStream must not be null"
        this.target = new ByteArrayOutputStream()
        this.wrapper = new TeeOutputStream(source, this.target)
    }

    void write(int b) throws IOException {
        this.wrapper.write(b)
    }

    void flush() throws IOException {
        this.wrapper.flush()
    }

    void close() throws IOException {
        this.wrapper.close()
    }

    @Override
    boolean isReady() {
        return this.source.isReady()
    }

    @Override
    void setWriteListener(WriteListener writeListener) {
        this.source.setWriteListener(writeListener)
    }

    byte[] getBytes() {
        return this.target.toByteArray()
    }
}
