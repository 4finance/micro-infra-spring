package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import com.ofg.stub.config.StubRunnerConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.get
import static com.ofg.config.BasicProfiles.TEST
import static org.hamcrest.Matchers.equalTo

class ConsumerDrivenContractSpec extends Specification {

    public static final String GENERIC_FOOBAR = 'foobar'
    public static final String CONTEXT_SPECIFIC_FOOBAR = 'foobar Poland'

    @Shared
    private AnnotationConfigApplicationContext applicationContext

    static {
        //System.setProperty("ivy.message.logger.level", '4')
    }

    def setupSpec() {
        applicationContext = new AnnotationConfigApplicationContext()
        applicationContext.environment.setActiveProfiles(TEST)
        applicationContext.register(PropertySourceConfiguration, StubRunnerConfiguration, ServiceResolverConfiguration)
        applicationContext.refresh()
    }

    def 'should register a collaborator in a TestingZookeeper'()  {
        given:
            ServiceResolver serviceResolver = applicationContext.getBean(ServiceResolver)
        expect:
            serviceResolver.getUri(new ServicePath('/com/ofg/foo/bar')).isPresent()
    }

    def 'should set up a stubbed http endpoint'()  {
        given:
            ServiceResolver serviceResolver = applicationContext.getBean(ServiceResolver)
        and:
            URI fooBarUrl = serviceResolver.getUri(new ServicePath('/com/ofg/foo/bar')).get()
        expect:
            get("$fooBarUrl/foobar").then().assertThat().body(equalTo(GENERIC_FOOBAR));
            get("$fooBarUrl/pl/foobar").then().assertThat().body(equalTo(CONTEXT_SPECIFIC_FOOBAR));
    }

    def cleanupSpec() {
        applicationContext.close()
    }
}