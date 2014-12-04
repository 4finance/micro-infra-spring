package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import com.ofg.stub.config.StubRunnerConfiguration
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.get
import static com.ofg.config.BasicProfiles.TEST
import static org.hamcrest.Matchers.equalTo

@Slf4j
class ConsumerDrivenContractSpec extends Specification {

    public static final String GENERIC_FOOBAR = 'foobar'
    public static final String CONTEXT_SPECIFIC_FOOBAR = 'foobar Poland'
    private AnnotationConfigApplicationContext applicationContext

    static {
        //System.setProperty("ivy.message.logger.level", '4')
    }

    def setup() {
        applicationContext = new AnnotationConfigApplicationContext()
        this.applicationContext.environment.setActiveProfiles(TEST)
        this.applicationContext.register(PropertySourceConfiguration, StubRunnerConfiguration, ServiceResolverConfiguration)
        this.applicationContext.refresh()
    }

    def 'should register a collaborator in a TestingZookeeper'()  {
        given:
            ServiceResolver serviceResolver = applicationContext.getBean(ServiceResolver)
        expect:
            serviceResolver.getUrl('foo-bar').isPresent()
    }

    def 'should set up a stubbed http endpoint'()  {
        given:
            ServiceResolver serviceResolver = applicationContext.getBean(ServiceResolver)
        and:
            String fooBarUrl = serviceResolver.getUrl('foo-bar').get()
        expect:
            get("$fooBarUrl/foobar").then().assertThat().body(equalTo(GENERIC_FOOBAR));
            get("$fooBarUrl/pl/foobar").then().assertThat().body(equalTo(CONTEXT_SPECIFIC_FOOBAR));
    }

    def cleanup() {
        applicationContext.close()
    }
}