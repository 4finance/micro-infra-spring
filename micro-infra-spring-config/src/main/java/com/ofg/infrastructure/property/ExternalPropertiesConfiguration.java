package com.ofg.infrastructure.property;

import com.ofg.infrastructure.property.decrypt.JceUnlimitedStrengthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryClientConfiguration;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Import({ ZookeeperAutoConfiguration.class, ZookeeperDiscoveryClientConfiguration.class })
@Configuration
@Profile("!test")
public class ExternalPropertiesConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Autowired(required = false)
    private TextEncryptor textEncryptor;

    @Bean
    public FileSystemLocator fileSystemLocator(AppCoordinates appCoordinates) {
        return new FileSystemLocator(
                PropertiesFolderFinder.find(),
                appCoordinates,
                textEncryptor == null ? new FailsafeTextEncryptor() : textEncryptor);
    }

    @Bean
    @Profile("!springCloud")
    public AppCoordinates deprecatedAppCoordinates( @Value("${microservice.config.file:classpath:microservice.json}")
                                                         Resource microserviceConfig) {
        return AppCoordinates.defaults(microserviceConfig);
    }

    @Bean
    @ConditionalOnBean(ZookeeperDiscoveryProperties.class)
    @Profile("springCloud")
    public AppCoordinates appCoordinates(ZookeeperDiscoveryProperties zookeeperDiscoveryProperties,
                                         @Value("${spring.application.name}") String applicationName) {
        return AppCoordinates.defaults(zookeeperDiscoveryProperties, applicationName);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        JceUnlimitedStrengthUtil.printWarningIfStrongEncryptionIsNotSupported();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * TextEncryptor that just fails, so that users don't get a false sense of security
     * adding ciphers to config files and not getting them decrypted.
     * <p/>
     * Based on FailsafeTextEncryptor from spring-cloud-config by Dave Syer.
     */
    static class FailsafeTextEncryptor implements TextEncryptor {

        @Override
        public String encrypt(String text) {
            throw new UnsupportedOperationException("Encryption is not supported. Did you configure the encryption key/keystore correctly?");
        }

        @Override
        public String decrypt(String encryptedText) {
            throw new UnsupportedOperationException("Decryption is not supported. Did you configure the encryption key/keystore correctly?");
        }
    }
}
