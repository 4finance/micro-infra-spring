package com.ofg.infrastructure.property;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class ConfigLocations {

    /**
     * Shared properties across environments.
     */
    private final File commonDir;

    /**
     * Country specific shared properties across environments.
     */
    private final File commonCountryDir;

    /**
     * Properties environment specific.
     */
    private final File envDir;

    /**
     * Properties country specific in given environment.
     */
    private final File envCountryDir;

    ConfigLocations(File commonDir, File envDir, File commonCountryDir, File envCountryDir) {
        this.commonDir = commonDir;
        this.envDir = envDir;
        this.commonCountryDir = commonCountryDir;
        this.envCountryDir = envCountryDir;
    }

    List<Path> getConfigPaths() {
        return Lists.transform(getAllDirs(), new Function<File, Path>() {
            @Override
            public Path apply(File dir) {
                return dir.toPath();
            }
        });
    }

    File commonPropertiesFile(String name) {
        return propertiesFile(commonDir, name);
    }

    File commonYamlFile(String name) {
        return yamlFile(commonDir, name);
    }

    File envPropertiesFile(String name) {
        return propertiesFile(envDir, name);
    }

    File envYamlFile(String name) {
        return yamlFile(envDir, name);
    }

    File commonCountryPropertiesFile(String name) {
        return propertiesFile(commonCountryDir, name);
    }

    File commonCountryYamlFile(String name) {
        return yamlFile(commonCountryDir, name);
    }

    File envCountryPropertiesFile(String name) {
        return propertiesFile(envCountryDir, name);
    }

    File envCountryYamlFile(String name) {
        return yamlFile(envCountryDir, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("commonDir", commonDir)
                .add("envDir", envDir)
                .add("commonCountryDir", commonCountryDir)
                .add("envCountryDir", envCountryDir)
                .toString();
    }

    private List<File> getAllDirs() {
        return Arrays.asList(commonDir, envDir, commonCountryDir, envCountryDir);
    }

    private File propertiesFile(File parent, String name) {
        return new File(parent, name + ".properties");
    }

    private File yamlFile(File parent, String name) {
        return new File(parent, name + ".yaml");
    }
}
