package com.ofg.infrastructure.property;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.google.common.base.Throwables;

public class FileSystemLocator implements PropertySourceLocator {

    private static final Logger log = LoggerFactory.getLogger(FileSystemLocator.class);
    public static final String CIPHER_PREFIX = "{cipher}";

    private final File propertiesFolder;
    private final AppCoordinates appCoordinates;
    private final TextEncryptor encryptor;

    public FileSystemLocator(File propertiesFolder, AppCoordinates appCoordinates, TextEncryptor encryptor) {
        this.propertiesFolder = propertiesFolder;
        this.appCoordinates = appCoordinates;
        this.encryptor = encryptor;
    }

    @Override
    public PropertySource<?> locate(org.springframework.core.env.Environment environment) {
        final ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        final NativeEnvironmentRepository springEnv = new NativeEnvironmentRepository(configurableEnvironment);
        final List<File> propertiesPath = getConfigDirs();
        //https://github.com/spring-cloud/spring-cloud-config/issues/338
        springEnv.setSearchLocations(toSearchLocations(propertiesPath));
        CompositePropertySource composite = new CompositePropertySource(FileSystemLocator.class.getSimpleName());
        addToPropertySource(returnPropertiesForName(springEnv, appCoordinates.getApplicationNameWithCountry()), composite);
        addToPropertySource(returnPropertiesForName(springEnv, appCoordinates.getApplicationNameWithoutCountry()), composite);
        addToPropertySource(returnPropertiesForName(springEnv, ConfigLocations.BASE_FILENAME_FOR_GLOBAL_CONFIG), composite);
        return composite;
    }

    private Environment returnPropertiesForName(NativeEnvironmentRepository springEnv, String forName) {
        return springEnv.findOne(forName, appCoordinates.getEnvironment(), null);
    }

    List<File> getConfigDirs() {
        return getConfigLocations().getAllDirs();
    }

    List<File> getConfigFiles() {
        return appCoordinates.getConfigFiles(propertiesFolder);
    }

    private String[] toSearchLocations(List<File> propertiesPath) {
        final String[] files = new String[propertiesPath.size()];
        for (int i = 0; i < propertiesPath.size(); i++) {
            files[i] = propertiesPath.get(i).toURI().toString();
        }
        return files;
    }

    private void addToPropertySource(org.springframework.cloud.config.environment.Environment loadedEnvs, CompositePropertySource propertySource) {
        for (org.springframework.cloud.config.environment.PropertySource source : loadedEnvs.getPropertySources()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = decrypt((Map<String, Object>) source.getSource());
            propertySource.addPropertySource(new MapPropertySource(source.getName(), map));
        }
    }

    public ConfigLocations getConfigLocations() {
        return appCoordinates.getConfigLocations(propertiesFolder);
    }

    private Map<String, Object> decrypt(Map<String, Object> sourceMap) {
        final Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            try {
                result.put(entry.getKey(), decryptIfEncrypted(entry.getValue()));
            } catch (Exception e) {
                log.error("Exception occurred while trying to decrypt key [{}]", entry.getKey());
                Throwables.propagate(e);
            }
        }
        return result;
    }

    private Object decryptIfEncrypted(Object obj) {
        if (obj.toString().startsWith(CIPHER_PREFIX)) {
            return decrypt(obj.toString());
        } else {
            return obj;
        }
    }

    private String decrypt(String encrypted) {
        final String encryptedStr = encrypted.substring(CIPHER_PREFIX.length());
        return encryptor.decrypt(encryptedStr);
    }
}
