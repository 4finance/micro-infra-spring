package com.ofg.infrastructure.web.correlationid

import com.google.common.base.Function
import com.ofg.infrastructure.correlationid.CorrelationIdHolder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.regex.Pattern
import com.google.common.base.Optional as GOptional

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER
import static org.springframework.util.StringUtils.hasText

/**
 * Filter that takes the value of the {@link com.ofg.infrastructure.correlationid.CorrelationIdHolder#CORRELATION_ID_HEADER} header
 * from either request or response and sets it in the {@link com.ofg.infrastructure.correlationid.CorrelationIdHolder}. It also provides
 * that value in {@link org.slf4j.MDC} logging related class so that logger prints the value of
 * correlation id at each log.
 * 
 * @see com.ofg.infrastructure.correlationid.CorrelationIdHolder
 * @see org.slf4j.MDC
 */
@Slf4j
@CompileStatic
//inspired by http://taidevcouk.wordpress.com/2014/05/26/implementing-correlation-ids-in-spring-boot/
class CorrelationIdFilter extends OncePerRequestFilter {

    public static final Pattern DEFAULT_SKIP_PATTERN =
            ~/\/api-docs.*|\/autoconfig|\/configprops|\/dump|\/info|\/metrics.*|\/mappings|\/trace|\/swagger.*|.*\.png|.*\.css|.*\.js|.*\.html/

    private final GOptional<Pattern> skipCorrId

    CorrelationIdFilter() {
        this.skipCorrId = GOptional.absent()
    }

    CorrelationIdFilter(Pattern skipCorrId) {
        this.skipCorrId = GOptional.of(skipCorrId)
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        setupCorrelationId(request, response)
        try {
            filterChain.doFilter(request, response)
        } finally {
            cleanupCorrelationId()
        }
    }

    private void setupCorrelationId(HttpServletRequest request, HttpServletResponse response) {
        String correlationId = getCorrelationIdFrom(request) ?: getCorrelationIdFrom(response)
        if(!hasText(correlationId) && shouldGenerateCorrId(request)) {
            correlationId = createNewCorrIdIfEmpty()
        }
        CorrelationIdHolder.set(correlationId)
        addCorrelationIdToResponseIfNotPresent(response, correlationId)
    }

    private String getCorrelationIdFrom(HttpServletResponse response) {
        return withLoggingAs('response') { response.getHeader(CORRELATION_ID_HEADER) }
    }

    private String getCorrelationIdFrom(HttpServletRequest request) {
        return withLoggingAs('request') { request.getHeader(CORRELATION_ID_HEADER) }
    }

    private withLoggingAs(String whereWasFound, Closure correlationIdGetter) {
        String correlationId = correlationIdGetter.call()
        if (hasText(correlationId)) {
            MDC.put(CORRELATION_ID_HEADER, correlationId)
            log.debug("Found correlationId in $whereWasFound: $correlationId")
        }
        return correlationId
    }

    private String createNewCorrIdIfEmpty() {
        String currentCorrId = UUID.randomUUID().toString()
        MDC.put(CORRELATION_ID_HEADER, currentCorrId)
        log.debug("Generating new correlationId: " + currentCorrId)
        return currentCorrId
    }

    protected boolean shouldGenerateCorrId(HttpServletRequest request) {
        String uri = request.requestURI?: ''
        boolean skip = skipCorrId
                .transform({Pattern skipPattern -> uri.matches(skipPattern)} as Function<Pattern, Boolean>)
                .or(false)
        return !skip
    }

    private void addCorrelationIdToResponseIfNotPresent(HttpServletResponse response, String correlationId) {
        if (!hasText(response.getHeader(CORRELATION_ID_HEADER))) {
            response.addHeader(CORRELATION_ID_HEADER, correlationId)
        }
    }

    private void cleanupCorrelationId() {
        MDC.remove(CORRELATION_ID_HEADER)
        CorrelationIdHolder.remove()
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false
    }
}