package com.ofg.infrastructure.property

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import spock.lang.Subject
import spock.util.environment.RestoreSystemProperties

import static com.ofg.infrastructure.property.AppCoordinates.CONFIG_FOLDER
import static com.ofg.infrastructure.property.PropertiesFolderFinder.DEFAULT_CONFIG_DIR

@RestoreSystemProperties
class PropertiesFolderFinderSpec extends Specification {

    @Rule
    EnvironmentVariables environmentVariables = new EnvironmentVariables()

    @Subject
    PropertiesFolderFinder propertiesFolderFinder = new PropertiesFolderFinder()

    def setup() {
        environmentVariables.clear(CONFIG_FOLDER)
        System.clearProperty(CONFIG_FOLDER)
    }

    def 'should return default config folder when CONFIG_FOLDER is not set'() {
        expect:
            propertiesFolderFinder.find() == DEFAULT_CONFIG_DIR
    }

    def 'should return config folder from CONFIG_FOLDER variable'() {
        given:
            System.setProperty(CONFIG_FOLDER, 'properties')

        expect:
            propertiesFolderFinder.find() == new File('properties')
    }
}
