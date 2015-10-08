package com.ofg.infrastructure.web.logging;

import com.ofg.infrastructure.web.logging.config.LogsConfig;
import com.ofg.infrastructure.web.logging.config.LogsConfigElement;
import com.ofg.infrastructure.web.logging.obfuscation.PayloadObfuscationProcessor;
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

    private static final Logger log = LoggerFactory.getLogger(HttpControllerCallLogger.class);
    private final LogsConfig props;
    private final PayloadObfuscationProcessor obfuscator;

    HttpControllerCallLogger(LogsConfig props, PayloadObfuscationProcessor obfuscator) {
        this.props = props;
        this.obfuscator = obfuscator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequestLoggingWrapper requestWrapper = new HttpServletRequestLoggingWrapper((HttpServletRequest)request);
        HttpServletResponseLoggingWrapper responseWrapper = new HttpServletResponseLoggingWrapper((HttpServletResponse) response);


        log.info("REQ CONTROLLER start {}", requestWrapper.getPathInfo());
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            HttpData reqData = createHttpData(requestWrapper);
            LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.getUrl(), reqData.getHttpMethod());
            if(!config.isSkipAll()){
                reqData.setProcessedContent(obfuscator.process(reqData.getContent(), reqData.getHeaders(), config.getFilteredReqFields()));
                reqData.setProcessedHeaders(config.getRequestHeadersToLogging(reqData.getHeaders()));
                log.info("REQ CONTROLLER->" + LogBuilder.createLogBuilder()
                        .withHttpData(reqData)
                        .build());

                HttpData resData = createHttpData(responseWrapper);
                resData.setProcessedContent(obfuscator.process(resData.getContent(), resData.getHeaders(), config.getFilteredResFields()));
                resData.setProcessedHeaders(config.getResponseHeadersToLogging(resData.getHeaders()));
                log.info("RES CONTROLLER<-" + LogBuilder.createLogBuilder()
                        .withHttpData(resData)
                        .build());
            }
        }
    }
}
