package com.ofg.infrastructure.web.logging.wrapper;

import com.google.common.base.Preconditions;
import org.apache.commons.io.input.TeeInputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DelegatingServletInputStream extends ServletInputStream {

    private final  ServletInputStream source;
    private final ByteArrayOutputStream target;
    private final TeeInputStream wrapper;

    public DelegatingServletInputStream(ServletInputStream source) {
        Preconditions.checkArgument(source != null , "source ServletInputStream must not be null");
        this.source = source;
        this.target = new ByteArrayOutputStream();
        this.wrapper = new TeeInputStream(source, this.target);
    }

    @Override
    public boolean isFinished() {
        return this.source.isFinished();
    }

    @Override
    public boolean isReady() {
        return this.source.isReady();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.source.setReadListener(readListener);
    }

    @Override
    public int read() throws IOException {
        return this.wrapper.read();
    }

    public byte[] getBytes() {
        return this.target.toByteArray();
    }
}
