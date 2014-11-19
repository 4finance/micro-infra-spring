package com.ofg.infrastructure.property

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.autoconfigure.ConfigClientAutoConfiguration
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import spock.lang.Shared

import javax.annotation.PostConstruct
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicInteger

import static com.jayway.awaitility.Awaitility.await

class FileSystemPollerTest extends AbstractIntegrationTest {

    @Shared
    private ConfigurableApplicationContext context
    @Shared
    private FileSystemPoller poller
    @Shared
    private InitCounter counter;

    def setupSpec() {
        System.setProperty("encrypt.key", "eKey")
        System.setProperty(AppCoordinates.COUNTRY_CODE, "pl")
        context = new SpringApplicationBuilder(RefreshingApp)
                .web(false)
                .showBanner(false)
                .run()
        counter = context.getBean(InitCounter)
        poller = context.getBean(FileSystemPoller)
    }

    def cleanupSpec() {
        context?.close()
    }

    def 'should reload configuration once when file system touched'() {
        given:
            def previous = counter.value

        when:
            oneConfigurationFileWasChanged()

        then:
            beanWasReloaded(previous)
            counter.value == previous + 1
    }

    private def beanWasReloaded(int previous) {
        await().until ({
            return counter.value == previous + 1
        } as Callable<Boolean>)
        true
    }

    private void oneConfigurationFileWasChanged() {
        new File(poller.getConfigPath(), "micro-app.yaml").setLastModified(System.currentTimeMillis())
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