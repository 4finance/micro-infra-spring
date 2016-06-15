package com.ofg.infrastructure.web.logging;

import com.ofg.infrastructure.web.logging.config.LogsConfig;
import com.ofg.infrastructure.web.logging.config.LogsConfigElement;
import com.ofg.infrastructure.web.logging.obfuscation.PayloadObfuscationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class RequestResponseLogger {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLogger.class);
    private LogsConfig props;
    private PayloadObfuscationProcessor obfuscator;

    public RequestResponseLogger(LogsConfig props, PayloadObfuscationProcessor obfuscator) {
        this.props = props;
        this.obfuscator = obfuscator;
    }

    public void logObfuscatedRequest(HttpData reqData, String tag) {
        checkNotNull(reqData, "request data cannot be empty");
        LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.getPath(), reqData.getHttpMethod());
        if(!config.isSkipAll()){
            reqData.setProcessedContent(obfuscator.process(reqData.getContent(), reqData.getHeaders(), config.getFilteredReqFields()));
            reqData.setProcessedHeaders(config.getRequestHeadersToLogging(reqData.getHeaders()));
            log.debug("REQ {}->{}", tag, LogBuilder.createLogBuilder()
                    .withHttpData(reqData)
                    .build());
        }
    }
    
    public void logObfuscatedResponse(HttpData reqData, HttpData resData, String tag) {
        checkNotNull(reqData, "request data cannot be empty");
        checkNotNull(resData, "response data cannot be empty");
        LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.getPath(), reqData.getHttpMethod());
        if(!config.isSkipAll()){
            resData.setProcessedContent(obfuscator.process(resData.getContent(), resData.getHeaders(), config.getFilteredResFields()));
            resData.setProcessedHeaders(config.getResponseHeadersToLogging(resData.getHeaders()));
            log.debug("RES {}<-{}", tag, LogBuilder.createLogBuilder()
                    .withHttpData(resData)
                    .build());
        }
    }
}
