package com.ofg.infrastructure.web.logging;

import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper;
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData;

class HttpControllerCallLogger extends GenericFilterBean {

    public static final String TAG = "CONTROLLER";
    private static final Logger log = LoggerFactory.getLogger(HttpControllerCallLogger.class);
    private final RequestResponseLogger requestResponseLogger;

    HttpControllerCallLogger(RequestResponseLogger requestResponseLogger) {
        this.requestResponseLogger = requestResponseLogger;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        log.debug("REQ CONTROLLER init {}", (((HttpServletRequest) request).getRequestURI()));
        HttpServletRequestLoggingWrapper requestWrapper = new HttpServletRequestLoggingWrapper((HttpServletRequest) request);
        HttpServletResponseLoggingWrapper responseWrapper = new HttpServletResponseLoggingWrapper((HttpServletResponse) response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            HttpData reqData = createHttpData(requestWrapper);
            HttpData resData = createHttpData(responseWrapper);
            requestResponseLogger.logObfuscatedRequest(reqData, TAG);
            requestResponseLogger.logObfuscatedResponse(reqData,resData, TAG);
        }
    }
}
