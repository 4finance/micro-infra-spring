package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

/*
* Autowire-by-name is not an option for micro-infra-spring defined beans because when we use it
* the user does not get an exception saying that there already is such a bean in micro-infra and
* that maybe they should consider using it. Sure there is a log message from spring, but will they spot it?
*
* Also, because of ability to override beans by name, all our beans should have 'microInfra' prefix in name,
* so that users does not override our beans without knowing (again, no exception, just log message)
* */
@ContextConfiguration(classes = Application, loader = SpringApplicationContextLoader)
class RestOperationsOverridingSpec extends MvcIntegrationSpec {

    //overridden default injected by type
    @Autowired
    RestOperations restOperationsByType

    @Autowired
    ServiceRestClient serviceRestClient

    def 'should allow for overriding default micro-infra-spring RestOperations'() {
        expect:
            restOperationsByType instanceof CustomDefaultRestTemplate
            serviceRestClient.restOperations instanceof CustomDefaultRestTemplate
    }

    @Configuration
    @Import([
            ImportOverrides,
            ServiceRestClientConfiguration,
            BaseConfiguration
    ])
    //@EnableServiceRestClient //no way this will work... unless till ConfigurationClassParser:416 goes above the for loop
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class Application {
    }

    @Configuration
    static class ImportOverrides {

        @Bean
        @Qualifier('micro-infra-spring')
        RestOperations customDefaultRestTemplate() {
            return new CustomDefaultRestTemplate()
        }
    }

}

@ContextConfiguration(classes = Application, loader = SpringApplicationContextLoader)
class RestOperationsCustomImplementationSpec extends MvcIntegrationSpec {

    //custom variant injected by its specific type
    @Autowired
    SpecificEndpointRestTemplate restOperationsByType

    //custom variant injected by name (no conflict with micro-infra beans)
    @Autowired
    RestOperations restOperations

    //custom variant injected by type + qualifier
    @Autowired
    @Qualifier('quite-peculiar')
    RestOperations peculiarEndpointRestOperations

    @Autowired
    ServiceRestClient serviceRestClient

    def 'should allow for having another bean of same type as one declared in micro-infra-spring'() {
        expect:
            restOperations instanceof SpecificEndpointRestTemplate
            restOperationsByType instanceof SpecificEndpointRestTemplate
            peculiarEndpointRestOperations instanceof PeculiarEndpointRestTemplate
    }

    def 'should not accidentally override the bean in micro-infra-spring'() {
        expect:
            serviceRestClient.restOperations instanceof com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
    }

    @Configuration
    @Import([BaseConfiguration])
    @EnableServiceRestClient
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class Application {

        @Bean
        RestOperations restOperations() {
            return new SpecificEndpointRestTemplate()
        }

        @Bean
        @Qualifier('quite-peculiar')
        RestOperations evenMoreSpecificEndpointRestOperations() {
            return new PeculiarEndpointRestTemplate()
        }
    }
}

@ContextConfiguration(classes = Application, loader = SpringApplicationContextLoader)
class RestOperationsOverridingAndCustomImplementationSpec extends MvcIntegrationSpec {

    //overridden default injected by name
    @Autowired
    RestOperations customDefaultRestTemplate

    //overridden default injected by type + qualifier
    @Autowired
    @Qualifier('micro-infra-spring')
    RestOperations restOperations

    //overridden default can't be injected by type only - there are 3 candidates for autowiring
    //@Autowired
    //RestOperations restOperationsByType

    //custom variant injected by name
    @Autowired
    RestOperations specificEndpointRestOperations

    //custom variant injected by type + qualifier
    @Autowired
    @Qualifier('quite-peculiar')
    RestOperations peculiarEndpointRestOperations

    //both custom variants injected by their specific types
    @Autowired SpecificEndpointRestTemplate specificEndpointRestTemplateByType
    @Autowired PeculiarEndpointRestTemplate peculiarEndpointRestTemplateByType

    @Autowired
    ServiceRestClient serviceRestClient

    def 'should allow for both overriding a default bean and having another variant of the bean at the same time'() {
        expect:
            customDefaultRestTemplate instanceof CustomDefaultRestTemplate
            restOperations instanceof CustomDefaultRestTemplate
            serviceRestClient.restOperations instanceof CustomDefaultRestTemplate

        and:
            specificEndpointRestOperations instanceof SpecificEndpointRestTemplate
            peculiarEndpointRestOperations instanceof PeculiarEndpointRestTemplate
            specificEndpointRestTemplateByType instanceof SpecificEndpointRestTemplate
            peculiarEndpointRestTemplateByType instanceof PeculiarEndpointRestTemplate
    }

    @Configuration
    @Import([
            ImportOverrides,
            ServiceRestClientConfiguration,
            BaseConfiguration
    ])
    //@EnableServiceRestClient //no way this will work... unless till ConfigurationClassParser:416 goes above the for loop
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class Application {

        @Bean
        RestOperations specificEndpointRestOperations() {
            return new SpecificEndpointRestTemplate()
        }

        @Bean
        @Qualifier('quite-peculiar')
        RestOperations evenMoreSpecificEndpointRestOperations() {
            return new PeculiarEndpointRestTemplate()
        }
    }

    @Configuration
    static class ImportOverrides {

        @Bean
        @Qualifier('micro-infra-spring')
        RestOperations customDefaultRestTemplate() {
            return new CustomDefaultRestTemplate()
        }
    }

}

class CustomDefaultRestTemplate extends RestTemplate {}
class SpecificEndpointRestTemplate extends RestTemplate {}
class PeculiarEndpointRestTemplate extends RestTemplate {}
