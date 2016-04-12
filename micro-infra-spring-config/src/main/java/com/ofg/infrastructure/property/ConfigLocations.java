package com.ofg.infrastructure.property;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.MoreObjects;

class ConfigLocations {

    static final String BASE_FILENAME_FOR_GLOBAL_CONFIG = "global";

    private static final String COMMON_DIR_NAME = "common";

    private File rootFolder;

    private String microservicePathPrefix;

    private String environment;

    private String countryCode;

    ConfigLocations(File rootFolder, String microservicePathPrefix, String environment, String countryCode) {
        this.rootFolder = rootFolder;
        this.microservicePathPrefix = microservicePathPrefix;
        this.environment = environment;
        this.countryCode = countryCode;
    }

    List<File> getAllDirs() {
        return Arrays.asList(
                getGlobalConfigFolder(),
                getCommonConfigFolder(),
                getEnvConfigFolder(),
                getCommonCountryConfigFolder(),
                getCountryConfigFolder()
        );
    }

    File commonPropertiesFile(String name) {
        return propertiesFile(getCommonConfigFolder(), name);
    }

    File globalPropertiesFile() {
        return propertiesFile(getGlobalConfigFolder(), BASE_FILENAME_FOR_GLOBAL_CONFIG);
    }

    File globalYamlFile() {
        return yamlFile(getGlobalConfigFolder(), BASE_FILENAME_FOR_GLOBAL_CONFIG);
    }

    File commonYamlFile(String name) {
        return yamlFile(getCommonConfigFolder(), name);
    }

    File envPropertiesFile(String name) {
        return propertiesFile(getEnvConfigFolder(), name);
    }

    File envYamlFile(String name) {
        return yamlFile(getEnvConfigFolder(), name);
    }

    File commonCountryPropertiesFile(String name) {
        return propertiesFile(getCommonCountryConfigFolder(), name);
    }

    File commonCountryYamlFile(String name) {
        return yamlFile(getCommonCountryConfigFolder(), name);
    }

    File envCountryPropertiesFile(String name) {
        return propertiesFile(getCountryConfigFolder(), name);
    }

    File envCountryYamlFile(String name) {
        return yamlFile(getCountryConfigFolder(), name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dirs", getAllDirs())
                .toString();
    }

    private File propertiesFile(File parent, String name) {
        return new File(parent, name + ".properties");
    }

    private File yamlFile(File parent, String name) {
        return new File(parent, name + ".yaml");
    }

    /**
     * Shared properties across all microservices.
     */
    File getGlobalConfigFolder() {
        return new File(rootFolder, COMMON_DIR_NAME);
    }

    /**
     * Shared properties across environments.
     */
    File getCommonConfigFolder() {
        return new File(getGlobalConfigFolder(), microservicePathPrefix);
    }

    /**
     * Country specific shared properties across environments.
     */
    File getCommonCountryConfigFolder() {
        if (StringUtils.isNotEmpty(countryCode)) {
            return new File(getCommonConfigFolder(), countryCode);
        } else {
            return null;
        }
    }

    /**
     * Properties environment specific.
     */
    File getEnvConfigFolder() {
        return new File(getEnvFolder(), microservicePathPrefix);
    }

    /**
     * Properties country specific in given environment.
     */
    File getCountryConfigFolder() {
        return new File(getEnvConfigFolder(), countryCode);
    }

    private File getEnvFolder() {
        return new File(rootFolder, environment);
    }
}
