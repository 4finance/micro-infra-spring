package com.ofg.infrastructure.web.logging;

import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractHeaders;
import static com.ofg.infrastructure.web.logging.HttpDataExtractor.extractStatus;
import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.ofg.infrastructure.web.logging.config.LogsConfig;
import com.ofg.infrastructure.web.logging.config.LogsConfigElement;
import feign.Request;
import feign.Response;
import feign.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignCallObfuscatingLogger extends feign.Logger.JavaLogger {

    private static final String TAG = "CLIENT";
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLogger.class);

    private final LogsConfig props;
    private final RequestDataProvider requestDataProvider;
    private final RequestIdProvider requestIdProvider;
    private final RequestResponseLogger requestResponseLogger;

    public FeignCallObfuscatingLogger(LogsConfig props, RequestDataProvider requestDataProvider, RequestIdProvider requestIdProvider, RequestResponseLogger requestResponseLogger) {
        this.props = props;
        this.requestDataProvider = requestDataProvider;
        this.requestIdProvider = requestIdProvider;
        this.requestResponseLogger = requestResponseLogger;
    }

    @Override
    protected void logRequest(String configKey, feign.Logger.Level logLevel, Request request) {
        HttpData reqData = createHttpData(request);
        String requestId = requestIdProvider.getRequestId();
        requestDataProvider.store(requestId, reqData);
        requestResponseLogger.logObfuscatedRequest(reqData, TAG);
        super.logRequest(configKey, logLevel, request);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, feign.Logger.Level logLevel, Response response,
                                              long elapsedTime) throws IOException {
        String requestId = requestIdProvider.getRequestId();
        HttpData reqData = requestDataProvider.retrieve(requestId);
        if (requestTraceable(reqData) && isNotSkipped(reqData)) {
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            String content = new String(bodyData, StandardCharsets.UTF_8.name());
            HttpData resData = new HttpData(extractHeaders(response), extractStatus(response), content);
            Response rebufferedResponse = Response.create(response.status(), response.reason(), response.headers(),
                    bodyData);
            requestResponseLogger.logObfuscatedResponse(reqData, resData, TAG);
            requestDataProvider.remove(requestId);
            return super.logAndRebufferResponse(configKey, logLevel, rebufferedResponse, elapsedTime);
        } else {
            log.debug("Cannot obfuscate response, matching request data lost");
            return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
        }
    }

    private boolean requestTraceable(HttpData reqData) {
        return reqData != null;
    }

    private boolean isNotSkipped(HttpData reqData) {
        LogsConfigElement config = props.getConfigElementByUrlAndMethod(reqData.getPath(), reqData.getHttpMethod());
        return !config.isSkipAll();
    }

}