package com.ofg.infrastructure.web.logging.wrapper;

import com.google.common.base.Preconditions;
import org.apache.commons.io.output.TeeOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DelegatingServletOutputStream extends ServletOutputStream {

    private final ServletOutputStream source;
    private final ByteArrayOutputStream target;
    private final TeeOutputStream wrapper;

    DelegatingServletOutputStream(ServletOutputStream source) {
        Preconditions.checkArgument(source != null, "source ServletOutputStream must not be null");
        this.source = source;
        this.target = new ByteArrayOutputStream();
        this.wrapper = new TeeOutputStream(source, this.target);
    }

    public void write(int b) throws IOException {
        this.wrapper.write(b);
    }

    public void flush() throws IOException {
        this.wrapper.flush();
    }

    public void close() throws IOException {
        this.wrapper.close();
    }

    @Override
    public boolean isReady() {
        return this.source.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        this.source.setWriteListener(writeListener);
    }

    public byte[] getBytes() {
        return this.target.toByteArray();
    }
}
