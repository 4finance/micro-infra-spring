package com.ofg.infrastructure.web.logging.wrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpServletRequestLoggingWrapper extends HttpServletRequestWrapper{

    private final DelegatingServletInputStream delegate;

    public HttpServletRequestLoggingWrapper(HttpServletRequest response) {
        super(response);
        this.delegate =  new DelegatingServletInputStream(initDelegateInputStream());
    }

    private ServletInputStream initDelegateInputStream() {
        try {
            return super.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException("Error", e);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.delegate;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.delegate));
    }

    public byte[] getBytes() {
        return this.delegate.getBytes();
    }
}
