package com.ofg.infrastructure.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
@Profile("!test")
public class ExternalPropertiesConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Autowired(required = false)
    private TextEncryptor textEncryptor;

    @Value("${microservice.config.file:classpath:microservice.json}")
    private Resource microserviceConfig;

    @Bean
    public FileSystemLocator fileSystemLocator() {
        return new FileSystemLocator(
                PropertiesFolderFinder.find(),
                appCoordinates(),
                textEncryptor == null ? new FailsafeTextEncryptor() : textEncryptor);
    }

    @Bean
    public AppCoordinates appCoordinates() {
        return AppCoordinates.defaults(microserviceConfig);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
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
