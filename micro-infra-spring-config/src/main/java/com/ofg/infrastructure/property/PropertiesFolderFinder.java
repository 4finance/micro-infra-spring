package com.ofg.infrastructure.property;

import static com.ofg.infrastructure.property.AppCoordinates.CONFIG_FOLDER;

import java.io.File;

class PropertiesFolderFinder {

    public static final File DEFAULT_CONFIG_DIR = new File(System.getProperty("user.home"), "config");

    static File find() {
        final String configFolder = PropertyUtils.getProperty(CONFIG_FOLDER, null);
        return configFolder != null ? new File(configFolder) : DEFAULT_CONFIG_DIR;
    }
}
