package com.ofg.infrastructure.web.logging.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class HttpServletResponseLoggingWrapper extends HttpServletResponseWrapper {

    private final DelegatingServletOutputStream delegate;

    public HttpServletResponseLoggingWrapper(HttpServletResponse response) {
        super(response);
        this.delegate = new DelegatingServletOutputStream(initDelegateInputStream());
    }

    private ServletOutputStream initDelegateInputStream() {
        try {
            return super.getOutputStream();
        } catch (IOException e) {
            throw new IllegalStateException("Error", e);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.delegate;
    }

    public byte[] getBytes() {
        return this.delegate.getBytes();
    }

}
