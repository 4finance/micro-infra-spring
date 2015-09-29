package com.ofg.infrastructure.web.logging

import com.ofg.infrastructure.web.logging.config.LogsConfig
import com.ofg.infrastructure.web.logging.config.LogsConfigElement
import com.ofg.infrastructure.web.logging.obfuscation.PayloadObfuscationProcessor
import com.ofg.infrastructure.web.logging.wrapper.HttpServletRequestLoggingWrapper
import com.ofg.infrastructure.web.logging.wrapper.HttpServletResponseLoggingWrapper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData

@Slf4j
@CompileStatic
@Component
class HttpControllerCallLogger extends GenericFilterBean {

    private final LogsConfig props

    private final PayloadObfuscationProcessor obfuscator

    HttpControllerCallLogger() {
        this.props = new LogsConfig()
        this.obfuscator = new PayloadObfuscationProcessor()
    }

    HttpControllerCallLogger(LogsConfig props, PayloadObfuscationProcessor obfuscator) {
        this.props = props
        this.obfuscator = obfuscator
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequestLoggingWrapper requestWrapper = new HttpServletRequestLoggingWrapper((HttpServletRequest)request)
        HttpServletResponseLoggingWrapper responseWrapper = new HttpServletResponseLoggingWrapper((HttpServletResponse) response)


        log.info("REQ CONTROLLER start ${requestWrapper.getPathInfo()}")
        try {
            filterChain.doFilter(requestWrapper, responseWrapper)
        } finally {
            HttpData reqData = createHttpData(requestWrapper)
            LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.url, reqData.httpMethod)
            if(!Boolean.valueOf(config.skipAll)){
                reqData.content = obfuscator.process(reqData.content, reqData.headers, config.filteredReqFields)
                reqData.headers = config.getRequestHeadersToLogging(reqData.headers)
                log.info('REQ CONTROLLER->' + LogBuilder.createLogBuilder()
                        .withHttpData(reqData)
                        .build())

                HttpData resData = createHttpData(responseWrapper)
                resData.content = obfuscator.process(resData.content, resData.headers, config.filteredResFields)
                resData.headers = config.getResponseHeadersToLogging(resData.headers)
                log.info('RES CONTROLLER<-' + LogBuilder.createLogBuilder()
                        .withHttpData(resData)
                        .build())
            }
        }
    }
}
