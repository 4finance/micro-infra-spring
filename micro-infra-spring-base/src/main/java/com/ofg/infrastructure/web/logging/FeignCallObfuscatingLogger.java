package com.ofg.infrastructure.web.logging;

import feign.Request;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData;

public class FeignCallObfuscatingLogger extends feign.Logger.JavaLogger {

    private static final String TAG = "CLIENT";
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLogger.class);

    private final RequestDataProvider requestDataProvider;
    private final RequestIdProvider requestIdProvider;
    private final RequestResponseLogger requestResponseLogger;

    public FeignCallObfuscatingLogger(RequestDataProvider requestDataProvider, RequestIdProvider requestIdProvider, RequestResponseLogger requestResponseLogger) {
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
    protected Response logAndRebufferResponse(String configKey, feign.Logger.Level logLevel, Response response, long elapsedTime) throws IOException {
        String requestId = requestIdProvider.getRequestId();
        HttpData reqData = requestDataProvider.retrieve(requestId);
        if (requestTraceable(reqData)) {
            HttpData resData = createHttpData(response);
            Response rebufferedResponse = Response.create(response.status(), response.reason(), response.headers(), resData.getContent().getBytes());
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
}