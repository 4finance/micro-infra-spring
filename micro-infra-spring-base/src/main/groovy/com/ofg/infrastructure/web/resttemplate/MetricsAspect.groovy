package com.ofg.infrastructure.web.resttemplate

import com.codahale.metrics.MetricRegistry
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.web.client.RestOperations

import static org.springframework.util.StringUtils.trimLeadingCharacter
import static org.springframework.util.StringUtils.trimTrailingCharacter

@Aspect
@CompileStatic
@Slf4j
class MetricsAspect {

    private static final String PREFIX = RestOperations.class.simpleName
    private final MetricRegistry metricRegistry

    MetricsAspect(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry
    }

    @Around('execution(public * org.springframework.web.client.RestOperations.exchange(..))')
    Object measureRestCallDuration(ProceedingJoinPoint pjp) throws Throwable {
        final Object url = pjp.args[0]
        final String name = metricName(url)
        return metricRegistry.timer(name).time {
            final long startTime = System.currentTimeMillis()
            Object result = pjp.proceed()
            final long done = System.currentTimeMillis() - startTime
            log.debug("Calling '$url' [$name] took ${done}ms")
            return result
        }
    }

    static String metricName(url) {
        String uriString = url.toString().replaceAll("[{}]", "")
        final URI uri = new URI(uriString)
        final String path = fixSpecialCharacters(uri.path)
        int port = (uri.port > 0)? uri.port : 80
        return trimDots("${PREFIX}.${uri.host}.${port}.${path}")
    }

    private static String fixSpecialCharacters(String value) {
        final String result = value
                .replaceAll("\\.", "_")
                .replaceAll("/", ".");
        return trimDots(result)
    }

    private static String trimDots(String str) {
        return trimTrailingCharacter(
                trimLeadingCharacter(str, '.' as Character), '.' as Character)
    }


}
