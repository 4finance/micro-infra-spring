package com.ofg.infrastructure.property

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.autoconfigure.ConfigClientAutoConfiguration
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

@IgnoreIf({ os.macOs }) //Due to problems with native file system poller implementation - https://github.com/4finance/micro-infra-spring/issues/119
class FileSystemPollerTest extends AbstractIntegrationTest {

    @Shared
    @AutoCleanup
    private ConfigurableApplicationContext context
    @Shared
    private FileSystemPoller poller
    @Shared
    private InitCounter counter;

    private PollingConditions conditions = new PollingConditions()

    def setupSpec() {
        System.setProperty("encrypt.key", "eKey")
        context = new SpringApplicationBuilder(RefreshingApp)
                .web(false)
                .showBanner(false)
                .run()
        counter = context.getBean(InitCounter)
        poller = context.getBean(FileSystemPoller)
    }

    def 'should reload configuration once when file system touched'() {
        when:
            oneConfigurationFileWasChanged("micro-app.yaml")
        then:
            conditions.eventually {
                counter.value == old(counter.value) + 1
            }
            counter.value == old(counter.value) + 1
    }

    private void oneConfigurationFileWasChanged(String configFile) {
        File file = new File(poller.getConfigPath(), configFile)
        assert file.exists()
        file.setLastModified(System.currentTimeMillis())
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
