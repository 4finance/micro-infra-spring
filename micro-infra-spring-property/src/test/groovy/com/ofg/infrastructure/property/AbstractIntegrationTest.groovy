package com.ofg.infrastructure.property

import spock.lang.Specification

import static com.ofg.infrastructure.property.PropertiesConfiguration.APP_ENV
import static com.ofg.infrastructure.property.PropertiesConfiguration.COUNTRY_CODE


abstract class AbstractIntegrationTest extends Specification {

    def setupSpec() {
        System.setProperty(PropertiesConfiguration.CONFIG_FOLDER, findConfigDirInTestResources())
        System.setProperty(APP_ENV, "prod")
        System.setProperty(COUNTRY_CODE, "pl")
    }

    private String findConfigDirInTestResources() {
        URL resourceInSrcTestRoot = getClass().getResource("/logback-test.groovy")
        String srcTestResources = new File(resourceInSrcTestRoot.file).parent
        return new File(srcTestResources, 'test-config-dir').absolutePath
    }
}
