package com.ofg.infrastructure.tracing

import com.ofg.config.BasicProfiles
import org.springframework.boot.test.EnvironmentTestUtils
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

class TracingPropertiesEnablerSpec extends Specification {

    def 'should resolve zipkin property to [#propValue] if profile is [#profile] and app env is [#appEnv]'() {
        given:
            ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(TracingPropertiesEnabler)
            context.environment.setActiveProfiles(profile)
            EnvironmentTestUtils.addEnvironment(context, "${TracingPropertiesEnabler.ENVIRONMENT_PROPERTY}=$appEnv")
        expect:
            propValue == Boolean.valueOf(context.environment.getProperty(TracingPropertiesEnabler.SPRING_ZIPKIN_ENABLED))
        where:
            profile                   | appEnv     || propValue
            BasicProfiles.DEVELOPMENT | 'sth'      || false
            BasicProfiles.DEVELOPMENT | 'test'      || false
            BasicProfiles.PRODUCTION  | 'sth'      || true
            BasicProfiles.PRODUCTION  | 'test'     || false
            BasicProfiles.PRODUCTION  | 'test-01'  || false
            BasicProfiles.PRODUCTION  | 'STAGE-09' || false
            BasicProfiles.PRODUCTION  | 'stage-09' || false
    }
}
