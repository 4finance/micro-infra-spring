package com.ofg.infrastructure.property;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.core.io.Resource;

import com.google.common.base.Optional;
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;

public class AppCoordinates {

    public static final String CONFIG_FOLDER = "CONFIG_FOLDER";
    public static final String APP_ENV = "APP_ENV";

    private final String environment;
    private final String path;
    private final String countryCode;

    @Deprecated
    public static AppCoordinates defaults(Resource microserviceConfigResource) {
        requireNonNull(microserviceConfigResource, " Microservice configuration cannot be null");
        try {
            final String configJson = IOUtils.toString(microserviceConfigResource.getURL());
            final ServiceConfigurationResolver configurationResolver = new ServiceConfigurationResolver(configJson);
            final String path = configurationResolver.getMicroservicePath().getPath();
            final String countryName = configurationResolver.getBasePath();
            return new AppCoordinates(findEnvironment(), path, countryName);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read " + microserviceConfigResource, e);
        }
    }

    public static AppCoordinates defaults(ZookeeperDiscoveryProperties zookeeperDiscoveryProperties, String applicationName) {
        return new AppCoordinates(findEnvironment(), applicationName, rootWithoutStartingSlash(zookeeperDiscoveryProperties));
    }

    private static String rootWithoutStartingSlash(ZookeeperDiscoveryProperties zookeeperDiscoveryProperties) {
        if (zookeeperDiscoveryProperties.getRoot().startsWith("/")) {
            return zookeeperDiscoveryProperties.getRoot().substring(1);
        }
        return zookeeperDiscoveryProperties.getRoot();
    }

    AppCoordinates(String environment, String path, String countryCode) {
        this.environment = requireNonNull(environment);
        this.path = requireNonNull(path);
        this.countryCode = requireNonNull(countryCode);
    }

    public ConfigLocations getConfigLocations(File rootFolder) {
        return new ConfigLocations(rootFolder, getMicroservicePathPrefix(),
                environment, countryCode);
    }

    /**
     * It works as follows:
     * microservice path        result
     * /com/ofg/micro-app    -> /com/ofg
     * /com/ofg/pl/micro-app -> /com/ofg (if countryCode == pl)
     */
    private String getMicroservicePathPrefix() {
        final String[] components = nameComponents();
        StringBuilder pathSb = new StringBuilder();
        // Skip last 2 parts of name
        for (int i = 0; i < components.length - 2; i++) {
            pathSb.append(components[i]);
            pathSb.append(File.separatorChar);
        }
        pathSb.append(getPenultimateComponentIfNotCountryCode(components).or(""));
        return pathSb.toString();
    }

    private Optional<String> getPenultimateComponentIfNotCountryCode(String[] components) {
        String penultimateComponent = null;
        int possibleCountryIndex = components.length - 2;
        if (possibleCountryIndex >= 0) {
            String possibleCountryCode = components[possibleCountryIndex];
            if (!possibleCountryCode.equals(countryCode)) {
                penultimateComponent = possibleCountryCode;
            }
        }
        return Optional.fromNullable(penultimateComponent);
    }

    private static String findEnvironment() {
        final String envOrNull = PropertyUtils.getProperty(APP_ENV, null);
        return requireNonNull(envOrNull, "No " + APP_ENV + " property found. Good candidates are: 'dev', 'prod-01', etc.");
    }

    public String getEnvironment() {
        return environment;
    }

    /**
     *
     * @deprecated since 0.9.1, use {@link #getPath()} instead
     */
    @Deprecated
    public String getApplicationName() {
        return path;
    }

    public String getPath() {
        return path;
    }

    String getApplicationNameWithoutCountry() {
        return findBaseNameWithoutCountrySuffix(findBaseName());
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<File> getConfigFiles(File rootConfigFolder) {
        final String coreName = findBaseNameWithoutCountrySuffix(findBaseName());
        final String countryName = getCountryName(coreName);

        final ConfigLocations configLocations = getConfigLocations(rootConfigFolder);

        return Arrays.asList(
                configLocations.globalPropertiesFile(),
                configLocations.globalYamlFile(),
                configLocations.commonPropertiesFile(coreName),
                configLocations.commonYamlFile(coreName),
                configLocations.envPropertiesFile(coreName),
                configLocations.envYamlFile(coreName),
                configLocations.commonCountryPropertiesFile(countryName),
                configLocations.commonCountryYamlFile(countryName),
                configLocations.envCountryPropertiesFile(countryName),
                configLocations.envCountryYamlFile(countryName));
    }

    private String getCountryName(String coreName) {
        return coreName + "-" + countryCode;
    }

    public String getApplicationNameWithCountry() {
        return getApplicationNameWithoutCountry() + "-" + countryCode;
    }

    private String findBaseName() {
        final String[] nameComponents = nameComponents();
        return nameComponents[nameComponents.length - 1];
    }

    private String[] nameComponents() {
        return path.split("/");
    }

    private String findBaseNameWithoutCountrySuffix(String baseName) {
        if (baseName.endsWith(countryCode)) {
            final int suffixLength = countryCode.length() + 1;
            return baseName.substring(0, baseName.length() - suffixLength);
        } else {
            return baseName;
        }
    }
}
