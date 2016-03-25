package com.ofg.infrastructure.property

import com.ofg.infrastructure.spock.ClassLevelRestoreSystemProperties
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static com.ofg.infrastructure.property.AppCoordinates.APP_ENV
import static com.ofg.infrastructure.property.AppCoordinates.CONFIG_FOLDER

@RestoreSystemProperties
@ClassLevelRestoreSystemProperties
abstract class AbstractIntegrationSpec extends Specification {

    // ENCRYPT_KEY can be passed as environment variable, but not as system property.
    // Spring makes magic only for SystemEnvironmentPropertySource
    public static final String CLOUD_ENCRYPT_KEY = 'encrypt.key'

    private static final String CLOUD_SERVER_ENABLED = 'spring.cloud.config.server.enabled'
    private static final String ZOOKEEPER_DISCOVERY_ENABLED = 'spring.cloud.zookeeper.discovery.enabled'
    private static final String ZOOKEEPER_ENABLED = 'spring.cloud.zookeeper.enabled'

    def setupSpec() {
        System.setProperty(CONFIG_FOLDER, getConfigFolder())
        System.setProperty(APP_ENV, 'prod')
        System.setProperty(CLOUD_SERVER_ENABLED, 'false')
        System.setProperty(ZOOKEEPER_DISCOVERY_ENABLED, 'false')
        System.setProperty(ZOOKEEPER_ENABLED, 'false')
    }

    protected String getConfigFolder() {
        return getClass().getResource('/test-config-dir').file
    }

    protected void setEncryptKey(String encryptKey = 'eKey') {
        System.setProperty(CLOUD_ENCRYPT_KEY, encryptKey)
    }

    protected ConfigurableApplicationContext contextWithSources(Object... sources) {
        return applicationBuilderWithSources(sources).run()
    }

    protected SpringApplicationBuilder applicationBuilderWithSources(Object... sources) {
        return new SpringApplicationBuilder(sources)
                .web(false)
                .bannerMode(Banner.Mode.CONSOLE)
    }

    protected static void setValidBootstrapConfig() {
        System.setProperty('spring.cloud.bootstrap.name', 'bootstrap-ok')
    }
}
