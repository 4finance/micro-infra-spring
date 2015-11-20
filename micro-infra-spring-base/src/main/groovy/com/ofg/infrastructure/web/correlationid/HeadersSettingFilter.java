package com.ofg.infrastructure.web.correlationid;

import org.slf4j.MDC;
import org.springframework.cloud.sleuth.IdGenerator;
import org.springframework.cloud.sleuth.RandomUuidGenerator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER;
import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.OLD_CORRELATION_ID_HEADER;
import static org.springframework.cloud.sleuth.Trace.SPAN_ID_NAME;
import static org.springframework.util.StringUtils.hasText;

/**
 * Workaround for missing header setting for TraceFilter in Spring Cloud Sleuth
 *
 * TODO: Remove if fixed in Sleuth
 *
 * @see MDC
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class HeadersSettingFilter extends OncePerRequestFilter {

    private final IdGenerator idGenerator;

    public HeadersSettingFilter(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public HeadersSettingFilter() {
        this.idGenerator = new RandomUuidGenerator();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String spanId = getHeader(request, response, SPAN_ID_NAME);
        String traceId = getFirstNonBlankHeader(request, response, OLD_CORRELATION_ID_HEADER, CORRELATION_ID_HEADER);
        appendSpanIdIfMissing(response, spanId);
        appendTraceIdIfMissing(response, traceId);
        filterChain.doFilter(request, response);
    }

    private void appendSpanIdIfMissing(HttpServletResponse response, String spanId) {
        String idToPass = spanId;
        if (!hasText(spanId)) {
            idToPass = idGenerator.create();
        }
        addToResponseIfNotPresent(response, SPAN_ID_NAME, idToPass);
    }

    private void appendTraceIdIfMissing(HttpServletResponse response, String traceId) {
        String idToPass = traceId;
        if (!hasText(traceId)) {
            idToPass = idGenerator.create();
        }
        addToResponseIfNotPresent(response, CORRELATION_ID_HEADER, idToPass);
        addToResponseIfNotPresent(response, OLD_CORRELATION_ID_HEADER, idToPass);
    }

    private String getFirstNonBlankHeader(HttpServletRequest request, HttpServletResponse response,
                             String firstName, String secondName) {
        String firstValue = getHeader(request, response, firstName);
        if (hasText(firstValue)) {
            return firstValue;
        }
        return getHeader(request, response, secondName);
    }

    private String getHeader(HttpServletRequest request, HttpServletResponse response,
                             String name) {
        String value = request.getHeader(name);
        return hasText(value) ? value : response.getHeader(name);
    }

    private void addToResponseIfNotPresent(HttpServletResponse response, String name,
                                           String value) {
        if (!hasText(response.getHeader(name))) {
            response.addHeader(name, value);
        }
    }

}
