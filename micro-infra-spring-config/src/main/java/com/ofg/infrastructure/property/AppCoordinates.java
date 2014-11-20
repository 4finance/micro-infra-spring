package com.ofg.infrastructure.property;

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AppCoordinates {

    public static final String CONFIG_FOLDER = "CONFIG_FOLDER";
    public static final String APP_ENV = "APP_ENV";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String MICROSERVICE_JSON = "microservice.json";

    private final String environment;
    private final String applicationName;
    private final String countryCode;

    public static AppCoordinates defaults() {
        final URL res = AppCoordinates.class.getResource("/" + MICROSERVICE_JSON);
        Objects.requireNonNull(res, MICROSERVICE_JSON + " not found");
        try {
            final String configJson = IOUtils.toString(res);
            final ServiceConfigurationResolver configurationResolver = new ServiceConfigurationResolver(configJson);
            final String appName = configurationResolver.getMicroserviceName();
            final String countryName = configurationResolver.getBasePath();
            return new AppCoordinates(findEnvironment(), appName, countryName);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read " + MICROSERVICE_JSON, e);
        }
    }

    AppCoordinates(String environment, String applicationName, String countryCode) {
        this.environment = environment;
        this.applicationName = applicationName;
        this.countryCode = countryCode;
    }

    public File getConfigFolder(File rootFolder) {
        File folder = new File(rootFolder, environment);
        final String[] components = nameComponents();
        for (int i = 0; i < components.length - 1; i++) {
            folder = new File(folder, components[i]);
        }
        return useParentIfLastChildIsCountry(folder);
    }

    private File useParentIfLastChildIsCountry(File folder) {
        if (folder.getName().equals(countryCode)) {
            return folder.getParentFile();
        } else {
            return folder;
        }
    }

    private static String findEnvironment() {
        final String envOrNull = PropertyUtils.getProperty(APP_ENV, null);
        return Objects.requireNonNull(envOrNull, "No " + APP_ENV + " property found");
    }

    public String getEnvironment() {
        return environment;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public List<File> getConfigFileNames(File rootConfigFolder) {
        final String coreName = findBaseNameWithoutCountrySuffix(findBaseName());
        final File root = getConfigFolder(rootConfigFolder);
        return Arrays.asList(
                new File(root, coreName + ".properties"),
                new File(root, coreName + ".yaml"),
                new File(root, coreName + "-" + countryCode + ".properties"),
                new File(root, coreName + "-" + countryCode + ".yaml"));
    }

    private String findBaseName() {
        final String[] nameComponents = nameComponents();
        return nameComponents[nameComponents.length - 1];
    }

    private String[] nameComponents() {
        return applicationName.split("/");
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
