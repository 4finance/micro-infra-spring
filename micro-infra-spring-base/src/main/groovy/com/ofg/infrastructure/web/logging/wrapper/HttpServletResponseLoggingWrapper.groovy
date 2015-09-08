package com.ofg.infrastructure.web.logging.wrapper

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

class HttpServletResponseLoggingWrapper extends HttpServletResponseWrapper {

    private final DelegatingServletOutputStream delegate

    HttpServletResponseLoggingWrapper(HttpServletResponse response) {
        super(response)
        this.delegate = new DelegatingServletOutputStream(super.getOutputStream())
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.delegate
    }

    byte[] getBytes() {
        return this.delegate.bytes
    }

}
