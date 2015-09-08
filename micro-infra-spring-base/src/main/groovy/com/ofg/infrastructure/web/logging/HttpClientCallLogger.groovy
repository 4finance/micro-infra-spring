package com.ofg.infrastructure.web.logging

import com.ofg.infrastructure.web.logging.config.LogsConfig
import com.ofg.infrastructure.web.logging.config.LogsConfigElement
import com.ofg.infrastructure.web.logging.obfuscation.PayloadObfuscationProcessor
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData
@CompileStatic
@Slf4j
class HttpClientCallLogger implements ClientHttpRequestInterceptor {

    @Autowired
    LogsConfig props

    @Autowired
    PayloadObfuscationProcessor obfuscator


    @Override
    ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpData reqData = createHttpData(request, body)
        LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.path, reqData.httpMethod)
        if(!config.skipAll.toBoolean()){
            reqData.content = obfuscator.process(reqData.content, reqData.headers, config.filteredReqFields)
            reqData.headers = config.getRequestHeadersToLogging(reqData.headers)
            log.info('REQ CLIENT->' + LogBuilder.createLogBuilder()
                                                .withHttpData(reqData)
                                                .build())
        }

        ClientHttpResponse response = execution.execute(request, body)

        if(!config.skipAll.toBoolean()){
            HttpData resData = createHttpData(response)
            resData.content = obfuscator.process(resData.content, resData.headers, config.filteredResFields)
            resData.headers = config.getResponseHeadersToLogging(resData.headers)
            log.info('RES CLIENT<-' + LogBuilder.createLogBuilder()
                                                .withHttpData(resData)
                                                .build())
        }
        return response
    }
}