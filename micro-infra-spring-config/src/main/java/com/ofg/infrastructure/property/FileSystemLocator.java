package com.ofg.infrastructure.property;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.core.env.*;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public PropertySource<?> locate(Environment environment) {
        final ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
        final NativeEnvironmentRepository springEnv = getNativeEnvRepoThatDoesNotAttachTrailingSlash(configurableEnvironment);
        final List<File> propertiesPath = getConfigFiles();
        logConfigurationFiles(propertiesPath);
        springEnv.setSearchLocations(toSearchLocations(propertiesPath));
        final org.springframework.cloud.config.environment.Environment loadedEnvs =
                springEnv.findOne(appCoordinates.getPath(), appCoordinates.getEnvironment(), null);
        return toPropertySource(loadedEnvs);
    }

    private NativeEnvironmentRepository getNativeEnvRepoThatDoesNotAttachTrailingSlash(final ConfigurableEnvironment configurableEnvironment) {
        return new NativeEnvironmentRepository(configurableEnvironment) {
            @Override
            protected org.springframework.cloud.config.environment.Environment clean(org.springframework.cloud.config.environment.Environment value) {
                org.springframework.cloud.config.environment.Environment result = new org.springframework.cloud.config.environment.Environment(value.getName(), value.getProfiles(),
                        value.getLabel(), value.getVersion());
                for (org.springframework.cloud.config.environment.PropertySource source : value.getPropertySources()) {
                    String name = source.getName();
                    if (configurableEnvironment.getPropertySources().contains(name)) {
                        continue;
                    }
                    name = name.replace("applicationConfig: [", "");
                    name = name.replace("]", "");
                    if (getSearchLocations() != null) {
                        boolean matches = false;
                        String normal = name;
                        if (normal.startsWith("file:")) {
                            normal = StringUtils.cleanPath(new File(normal.substring("file:".length()))
                                    .getAbsolutePath());
                        }
                        for (String pattern : StringUtils
                                .commaDelimitedListToStringArray(getLocations(getSearchLocations(),
                                        result.getLabel()))) {
                            if (!pattern.contains(":")) {
                                pattern = "file:" + pattern;
                            }
                            if (pattern.startsWith("file:")) {
                                pattern = StringUtils.cleanPath(new File(pattern
                                        // at the end of this line is a trailing slash that breaks our approach
                                        .substring("file:".length())).getAbsolutePath());
                            }
                            if (log.isTraceEnabled()) {
                                log.trace("Testing pattern: " + pattern
                                        + " with property source: " + name);
                            }
                            if (normal.startsWith(pattern)
                                    && !normal.substring(pattern.length()).contains("/")) {
                                matches = true;
                                break;
                            }
                        }
                        if (!matches) {
                            // Don't include this one: it wasn't matched by our search locations
                            if (log.isDebugEnabled()) {
                                log.debug("Not adding property source: " + name);
                            }
                            continue;
                        }
                    }
                    log.info("Adding property source: " + name);
                    result.add(new org.springframework.cloud.config.environment.PropertySource(name, source.getSource()));
                }
                return result;
            }

            private String getLocations(String[] locations, String label) {
                List<String> output = new ArrayList<String>();
                for (String location : locations) {
                    output.add(location);
                }
                for (String location : locations) {
                    if (isDirectory(location) && StringUtils.hasText(label)) {
                        output.add(location + label.trim() + "/");
                    }
                }
                return StringUtils.collectionToCommaDelimitedString(output);
            }


            private boolean isDirectory(String location) {
                return !location.endsWith(".properties") && !location.endsWith(".yml")
                        && !location.endsWith(".yaml");
            }
        };
    }

    private void logConfigurationFiles(List<File> propertiesPath) {
        log.info("Loading configuration from:");
        for (File file : propertiesPath) {
            log.info("File (exists: {}): {}", file.exists(), file.getAbsolutePath());
        }
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

    private PropertySource<?> toPropertySource(org.springframework.cloud.config.environment.Environment loadedEnvs) {
        CompositePropertySource composite = new CompositePropertySource(FileSystemLocator.class.getSimpleName());
        for (org.springframework.cloud.config.environment.PropertySource source : loadedEnvs.getPropertySources()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = decrypt((Map<String, Object>) source.getSource());
            composite.addPropertySource(new MapPropertySource(source.getName(), map));
        }
        return composite;
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
