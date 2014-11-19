package com.ofg.infrastructure.property

import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
abstract class AbstractIntegrationTest extends Specification {

    def setupSpec() {
        System.setProperty(AppCoordinates.CONFIG_FOLDER, findConfigDirInTestResources())
        System.setProperty(AppCoordinates.APP_ENV, "prod")
    }

    def cleanupSpec() {
        System.properties.with {
            remove("encrypt.key")   //has to be done manually - @RestoreSystemProperties works only at feature (test) level
            remove(AppCoordinates.CONFIG_FOLDER)
            remove(AppCoordinates.APP_ENV)
            remove(AppCoordinates.COUNTRY_CODE)
            remove("spring.cloud.config.server.enabled")
        }
    }

    private String findConfigDirInTestResources() {
        URL resourceInSrcTestRoot = getClass().getResource("/logback-test.groovy")
        String srcTestResources = new File(resourceInSrcTestRoot.file).parent
        return new File(srcTestResources, 'test-config-dir').absolutePath
    }
}
