package com.ofg.infrastructure.web.correlationid;

import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
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

import static org.springframework.cloud.sleuth.Trace.SPAN_ID_NAME;
import static org.springframework.cloud.sleuth.Trace.TRACE_ID_NAME;
import static org.springframework.util.StringUtils.hasText;

/**
 * Filter that takes the value of the {@link CorrelationIdHolder#CORRELATION_ID_HEADER} header
 * from either request or response and sets it in the {@link CorrelationIdHolder}. It also provides
 * that value in {@link MDC} logging related class so that logger prints the value of
 * correlation id at each log.
 *
 * @see CorrelationIdHolder
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
        String traceId = getHeader(request, response, TRACE_ID_NAME);
        if (!hasText(spanId)) {
            addToResponseIfNotPresent(response, SPAN_ID_NAME, idGenerator.create());
        }
        if (!hasText(traceId)) {
            addToResponseIfNotPresent(response, TRACE_ID_NAME, idGenerator.create());
        }
        filterChain.doFilter(request, response);
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
