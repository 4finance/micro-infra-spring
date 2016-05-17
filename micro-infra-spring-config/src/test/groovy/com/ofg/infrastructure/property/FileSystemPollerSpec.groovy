package com.ofg.infrastructure.property

import com.jayway.awaitility.Awaitility
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.config.client.ConfigClientAutoConfiguration
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.AutoCleanup
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

import javax.annotation.PostConstruct
import java.util.concurrent.atomic.AtomicInteger

import static com.jayway.awaitility.Awaitility.await

@IgnoreIf({ os.macOs }) //Due to problems with native file system poller implementation - https://github.com/4finance/micro-infra-spring/issues/119
class FileSystemPollerSpec extends AbstractIntegrationSpec {

    private static final String MICROSERVICE_NAME = 'micro-app'
    private static final String PL_MICROSERVICE_NAME = MICROSERVICE_NAME + '-pl'

    @Shared
    @AutoCleanup
    private ConfigurableApplicationContext context
    @Shared
    private FileSystemPoller poller
    @Shared
    private InitCounter counter;

    private PollingConditions conditions = new PollingConditions()

    def setupSpec() {
        setValidBootstrapConfig()
        context = contextWithSources(RefreshingApp)
        counter = context.getBean(InitCounter)
        poller = context.getBean(FileSystemPoller)
    }

    def 'should reload configuration once when file #configFile from config location with allowed name is touched'() {
        when:
            oneConfigurationFileWasChanged(configFile)

        then:
            await().until {  counter.value == old(counter.value) + 1 }
        where:
            configFile << [
                    poller.getConfigLocations().globalPropertiesFile(),
                    poller.getConfigLocations().globalYamlFile(),
                    poller.getConfigLocations().commonPropertiesFile(MICROSERVICE_NAME),
                    poller.getConfigLocations().commonYamlFile(MICROSERVICE_NAME),
                    poller.getConfigLocations().envPropertiesFile(MICROSERVICE_NAME),
                    poller.getConfigLocations().envYamlFile(MICROSERVICE_NAME),
                    poller.getConfigLocations().commonCountryPropertiesFile(PL_MICROSERVICE_NAME),
                    poller.getConfigLocations().commonCountryYamlFile(PL_MICROSERVICE_NAME),
                    poller.getConfigLocations().envCountryPropertiesFile(PL_MICROSERVICE_NAME),
                    poller.getConfigLocations().envCountryYamlFile(PL_MICROSERVICE_NAME)
            ]
    }

    private void oneConfigurationFileWasChanged(File configFile) {
        assert configFile.exists()
        configFile.setLastModified(System.currentTimeMillis())
    }
}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = [ConfigClientAutoConfiguration.class])
class RefreshingApp {

    @Bean
    @RefreshScope
    InitCounter initCounter() {
        return new InitCounter()
    }
}

class InitCounter {
    private static final AtomicInteger COUNTER = new AtomicInteger()

    @PostConstruct
    public void started() {
        COUNTER.incrementAndGet()
    }

    public int getValue() {
        return COUNTER.get()
    }
}
