package com.ofg.infrastructure.web.logging.wrapper

import groovy.transform.CompileStatic
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@CompileStatic
class HttpServletRequestLoggingWrapper extends HttpServletRequestWrapper{

    private final DelegatingServletInputStream delegate

    HttpServletRequestLoggingWrapper(HttpServletRequest response) {
        super(response)
        this.delegate =  new DelegatingServletInputStream(super.getInputStream())
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.delegate
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.delegate))
    }

    byte[] getBytes() {
        return this.delegate.bytes
    }
}
