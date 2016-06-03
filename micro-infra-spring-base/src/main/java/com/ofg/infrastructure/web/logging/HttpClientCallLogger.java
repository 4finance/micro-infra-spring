package com.ofg.infrastructure.web.logging;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.ofg.infrastructure.web.logging.HttpDataFactory.createHttpData;
class HttpClientCallLogger implements ClientHttpRequestInterceptor {

    public static final String TAG = "CLIENT";
    private RequestResponseLogger requestResponseLogger;

    public HttpClientCallLogger(RequestResponseLogger requestResponseLogger) {
        this.requestResponseLogger = requestResponseLogger;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpData reqData = createHttpData(request, body);
        requestResponseLogger.logObfuscatedRequest(reqData, TAG);

        ClientHttpResponse response = execution.execute(request, body);

        HttpData resData = createHttpData(response);
        requestResponseLogger.logObfuscatedResponse(reqData, resData, TAG);
        return response;
    }
}