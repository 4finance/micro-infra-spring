package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.config.PropertySourceConfiguration
import com.ofg.stub.BatchStubRunner
import com.ofg.stub.config.StubRunnerConfiguration
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static com.jayway.restassured.RestAssured.get
import static com.ofg.config.BasicProfiles.TEST
import static org.hamcrest.Matchers.equalTo

@ContextConfiguration(classes = [PropertySourceConfiguration, DependencyVerifierConfiguration, StubRunnerConfiguration, ServiceResolverConfiguration])
@ActiveProfiles(TEST)
@WebAppConfiguration
class ConsumerDrivenContractSpec extends Specification {

    public static final String GENERIC_FOOBAR = 'foobar'
    public static final String CONTEXT_SPECIFIC_FOOBAR = 'foobar Poland'

    static {
        //System.setProperty("ivy.message.logger.level", '4')
    }

    @Autowired BatchStubRunner batchStubRunner
    @Autowired TestingServer testingServer
    @Autowired WebApplicationContext webApplicationContext
    @Autowired ServiceResolver serviceResolver

    def 'should register a collaborator in a TestingZookeeper'()  {
        expect:
            serviceResolver.getUrl('foo-bar').isPresent()
    }

    def 'should set up a stubbed http endpoint'()  {
        given:
            String fooBarUrl = serviceResolver.getUrl('foo-bar').get()
        expect:
            get("$fooBarUrl/foobar").then().assertThat().body(equalTo(GENERIC_FOOBAR));
            get("$fooBarUrl/pl/foobar").then().assertThat().body(equalTo(CONTEXT_SPECIFIC_FOOBAR));
    }
}
