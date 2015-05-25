package com.ofg.infrastructure.web.correlationid;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.correlationid.UuidGenerator;
import groovy.transform.CompileStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import static com.ofg.infrastructure.correlationid.CorrelationIdHolder.CORRELATION_ID_HEADER;
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
@CompileStatic
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final Pattern DEFAULT_SKIP_PATTERN = Pattern.compile("/api-docs.*|/autoconfig|/configprops|/dump|/info|/metrics.*|/mappings|/trace|/swagger.*|.*\\.png|.*\\.css|.*\\.js|.*\\.html");

    private final Optional<Pattern> skipCorrId;
    private final UuidGenerator uuidGenerator;

    public CorrelationIdFilter() {
        uuidGenerator = new UuidGenerator();
        skipCorrId = Optional.absent();
    }

    public CorrelationIdFilter(UuidGenerator uuidGenerator, Pattern skipCorrId) {
        this.uuidGenerator = uuidGenerator;
        this.skipCorrId = Optional.of(skipCorrId);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        setupCorrelationId(request, response);
        try {
            filterChain.doFilter(request, response);
        } finally {
            cleanupCorrelationId();
        }
    }

    private void setupCorrelationId(HttpServletRequest request, HttpServletResponse response) {
        String correlationIdFromRequest = getCorrelationIdFrom(request);
        String correlationId = hasText(correlationIdFromRequest) ? correlationIdFromRequest : getCorrelationIdFrom(response);
        if (!hasText(correlationId) && shouldGenerateCorrId(request)) {
            correlationId = createNewCorrIdIfEmpty();
        }
        CorrelationIdHolder.set(correlationId);
        addCorrelationIdToResponseIfNotPresent(response, correlationId);
    }

    private String getCorrelationIdFrom(final HttpServletResponse response) {
        return withLoggingAs("response", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return response.getHeader(CORRELATION_ID_HEADER);
            }
        });
    }

    private String getCorrelationIdFrom(final HttpServletRequest request) {
        return withLoggingAs("request", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return request.getHeader(CORRELATION_ID_HEADER);
            }
        });
    }

    private String withLoggingAs(String whereWasFound, Callable<String> correlationIdGetter) {
        String correlationId = tryToGetCorrelationId(correlationIdGetter);
        if (hasText(correlationId)) {
            MDC.put(CORRELATION_ID_HEADER, correlationId);
            log.debug("Found correlationId in {}: {}", whereWasFound, correlationId);
        }
        return correlationId;
    }

    private String tryToGetCorrelationId(Callable<String> correlationIdGetter) {
        try {
            return correlationIdGetter.call();
        } catch (Exception e) {
            log.error("Exception occurred while trying to retrieve request header", e);
            return "";
        }
    }

    private String createNewCorrIdIfEmpty() {
        String currentCorrId = uuidGenerator.create();
        MDC.put(CORRELATION_ID_HEADER, currentCorrId);
        log.debug("Generating new correlationId: {}", currentCorrId);
        return currentCorrId;
    }

    protected boolean shouldGenerateCorrId(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        boolean skip = skipCorrId.transform(new Function<Pattern, Boolean>() {
            public Boolean apply(Pattern skipPattern) {
                return skipPattern.matcher(uri).matches();
            }
        }).or(false);
        return !skip;
    }

    private void addCorrelationIdToResponseIfNotPresent(HttpServletResponse response, String correlationId) {
        if (!hasText(response.getHeader(CORRELATION_ID_HEADER))) {
            response.addHeader(CORRELATION_ID_HEADER, correlationId);
        }
    }

    private void cleanupCorrelationId() {
        MDC.remove(CORRELATION_ID_HEADER);
        CorrelationIdHolder.remove();
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}
