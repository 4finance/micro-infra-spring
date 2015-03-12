package com.ofg.infrastructure.property

import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

import static com.ofg.infrastructure.property.AppCoordinates.CONFIG_FOLDER
import static com.ofg.infrastructure.property.PropertiesFolderFinder.DEFAULT_CONFIG_DIR

class PropertiesFolderFinderSpec extends Specification {

    PropertiesFolderFinder propertiesFolderFinder = new PropertiesFolderFinder()

    def 'should return default config folder when CONFIG_FOLDER is not set'() {
        expect:
            propertiesFolderFinder.find() == DEFAULT_CONFIG_DIR
    }

    @RestoreSystemProperties
    def 'should return config folder from CONFIG_FOLDER variable'() {
        given:
            System.setProperty(CONFIG_FOLDER, 'properties')

        expect:
            propertiesFolderFinder.find() == new File('properties')
    }
}
