package com.ofg.infrastructure.web.logging;

import feign.Request;
import feign.Response;

import java.io.IOException;

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData;

public class FeignCallObfuscatingLogger extends feign.Logger.JavaLogger {

    public static final String TAG = "CLIENT";
    private final RequestDataProvider requestDataProvider;
    private final SpanIdProvider traceIdProvider;
    private final RequestResponseLogger requestResponseLogger;
    
    public FeignCallObfuscatingLogger(RequestDataProvider requestDataProvider, SpanIdProvider traceIdProvider, RequestResponseLogger requestResponseLogger) {
        this.requestDataProvider = requestDataProvider;
        this.traceIdProvider = traceIdProvider;
        this.requestResponseLogger = requestResponseLogger;
    }

    @Override
    protected void logRequest(String configKey, feign.Logger.Level logLevel, Request request) {
        HttpData reqData = createHttpData(request);
        String traceId = traceIdProvider.getSpanId();
        requestDataProvider.store(traceId, reqData);
        requestResponseLogger.logObfuscatedRequest(reqData, TAG);
        super.logRequest(configKey, logLevel, request);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, feign.Logger.Level logLevel, Response response, long elapsedTime) throws IOException {
        String traceId = traceIdProvider.getSpanId();
        HttpData reqData = requestDataProvider.retrieve(traceId);
        HttpData resData = createHttpData(response);
        Response rebufferedResponse = Response.create(response.status(), response.reason(), response.headers(), resData.getContent().getBytes());
        requestResponseLogger.logObfuscatedResponse(reqData, resData, TAG);
//        requestDataProvider.remove(traceId);
        return super.logAndRebufferResponse(configKey, logLevel, rebufferedResponse, elapsedTime);
    }
}