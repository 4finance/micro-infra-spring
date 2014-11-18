package com.ofg.infrastructure.property

import spock.lang.Specification


abstract class AbstractIntegrationTest extends Specification {

    def setupSpec() {
        System.setProperty(AppCoordinates.CONFIG_FOLDER, findConfigDirInTestResources())
        System.setProperty(AppCoordinates.APP_ENV, "prod")
    }

    private String findConfigDirInTestResources() {
        URL resourceInSrcTestRoot = getClass().getResource("/logback-test.groovy")
        String srcTestResources = new File(resourceInSrcTestRoot.file).parent
        return new File(srcTestResources, 'test-config-dir').absolutePath
    }
}
