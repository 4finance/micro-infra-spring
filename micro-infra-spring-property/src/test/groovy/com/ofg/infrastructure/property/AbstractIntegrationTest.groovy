package com.ofg.infrastructure.property

import spock.lang.Specification

import static com.ofg.infrastructure.property.PropertiesConfiguration.APP_ENV
import static com.ofg.infrastructure.property.PropertiesConfiguration.COUNTRY_CODE


abstract class AbstractIntegrationTest extends Specification {

    def setupSpec() {
        System.setProperty(APP_ENV, "prod")
        System.setProperty(COUNTRY_CODE, "pl")
    }
}
