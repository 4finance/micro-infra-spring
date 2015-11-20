package com.ofg.infrastructure.scheduling

import com.ofg.infrastructure.tracing.TracingConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.IdGenerator
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.Trace
import org.springframework.cloud.sleuth.TraceContextHolder
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions


@ContextConfiguration(classes = [ScheduledConfiguration])
class ScheduledTaskWithCorrelationIdAspectSpec extends Specification {

    @Autowired ScheduledClass scheduledClass

    PollingConditions pollingConditions = new PollingConditions()

    def 'should set correlationid on a scheduled method'() {
        expect:
            interaction {
                spanHasBeenSet()
            }
        and:
            Span firstSpan = scheduledClass.span
            interaction {
                spanIsDifferentThanTheFirstTime(firstSpan)
            }

    }

    private spanHasBeenSet() {
        pollingConditions.eventually {
            assert scheduledClass.span != null
        }
    }

    private spanIsDifferentThanTheFirstTime(Span firstSpan) {
        pollingConditions.eventually {
            assert scheduledClass.span != firstSpan
        }
    }


    @Configuration
    @Import([TracingConfiguration, TraceAutoConfiguration])
    @EnableScheduling
    static class ScheduledConfiguration {

        @Bean ScheduledTaskWithCorrelationIdAspect scheduledTaskWithCorrelationIdAspect(IdGenerator idGenerator, Trace trace) {
            return new ScheduledTaskWithCorrelationIdAspect(idGenerator, trace)
        }

        @Bean ScheduledClass scheduledClass() {
            return new ScheduledClass()
        }
    }

    static class ScheduledClass {

        Span span

        @Scheduled(fixedRate = 1L)
        void scheduledMethod() {
            span = TraceContextHolder.currentSpan
        }

    }
}
