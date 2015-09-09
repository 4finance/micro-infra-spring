package com.ofg.infrastructure.property
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.config.client.ConfigClientAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

class LoadingFromInvalidFileSpec extends AbstractIntegrationSpec {

    def "should fail with meaningful error on invalid yaml file"() {
        given:
            System.setProperty('microservice.config.file', 'classpath:microservice-invalid.json')
            System.setProperty('spring.cloud.bootstrap.name', 'bootstrap-invalid')
        when:
            contextWithSources(AppWithInvalidConfig)
        then:
            def e = thrown(Exception)
            e.message.contains("keyForUnquotedCurlyBraces: {cipher}aKey")
    }
}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = [ConfigClientAutoConfiguration.class])
class AppWithInvalidConfig {
}
