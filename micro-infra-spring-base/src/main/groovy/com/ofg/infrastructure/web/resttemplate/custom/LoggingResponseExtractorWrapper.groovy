package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseExtractor

@CompileStatic
@PackageScope
@Slf4j
class LoggingResponseExtractorWrapper<T> implements ResponseExtractor<ResponseEntity<T>> {

    private final ResponseExtractor<ResponseEntity<T>> delegate
    private final int maxLogResponseChars

    LoggingResponseExtractorWrapper(ResponseExtractor<ResponseEntity<T>> delegate, int maxLogResponseChars) {
        this.delegate = delegate
        this.maxLogResponseChars = maxLogResponseChars
    }

    @Override
    ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
        logBody(response)
        return delegate.extractData(response)
    }

    private void logBody(ClientHttpResponse response) {
        if (shouldLogResponse()) {
            String responseAbbreviated = InputStreamPrinter.abbreviate(response.getBody(), maxLogResponseChars)
            log.debug("REST response: {}", responseAbbreviated)
        }
    }

    private boolean shouldLogResponse() {
        return log.isDebugEnabled() && maxLogResponseChars > 0
    }
}
