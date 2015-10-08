package com.ofg.infrastructure.web.logging;

import com.ofg.infrastructure.web.logging.config.LogsConfig;
import com.ofg.infrastructure.web.logging.config.LogsConfigElement;
import com.ofg.infrastructure.web.logging.obfuscation.PayloadObfuscationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData;
class HttpClientCallLogger implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(HttpClientCallLogger.class);
    private LogsConfig props;
    private PayloadObfuscationProcessor obfuscator;

    public HttpClientCallLogger(LogsConfig props, PayloadObfuscationProcessor obfuscator) {
        this.props = props;
        this.obfuscator = obfuscator;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpData reqData = createHttpData(request, body);
        LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.getPath(), reqData.getHttpMethod());
        if(!config.isSkipAll()){
            reqData.setProcessedContent(obfuscator.process(reqData.getContent(), reqData.getHeaders(), config.getFilteredReqFields()));
            reqData.setProcessedHeaders(config.getRequestHeadersToLogging(reqData.getHeaders()));
            log.info("REQ CLIENT->" + LogBuilder.createLogBuilder()
                                                .withHttpData(reqData)
                                                .build());
        }

        ClientHttpResponse response = execution.execute(request, body);

        if(!config.isSkipAll()){
            HttpData resData = createHttpData(response);
            resData.setProcessedContent(obfuscator.process(resData.getContent(), resData.getHeaders(), config.getFilteredResFields()));
            resData.setProcessedHeaders(config.getResponseHeadersToLogging(resData.getHeaders()));
            log.info("RES CLIENT<-" + LogBuilder.createLogBuilder()
                                                .withHttpData(resData)
                                                .build());
        }
        return response;
    }
}