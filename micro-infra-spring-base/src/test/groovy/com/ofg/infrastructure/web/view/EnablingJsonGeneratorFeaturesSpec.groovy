package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonGenerator
import spock.lang.Unroll

import static com.ofg.infrastructure.web.view.JacksonFeaturesTestConstants.JACKSON_GENERATOR_FEATURES_AS_LIST

class EnablingJsonGeneratorFeaturesSpec extends JsonJacksonFeaturesSpec {

    def setupSpec() {
        System.setProperty("json.jackson.generator.on", JACKSON_GENERATOR_FEATURES_AS_LIST.join(','))
    }

    def cleanupSpec() {
        System.properties.remove("json.jackson.generator.on")
    }

    def "should enable JsonGenerator's feature #generatorFeature"() {
        given:
            def converter = getFirstConfiguredJacksonMessageConverter()
            def feature = JsonGenerator.Feature.valueOf(generatorFeature)
        expect:
            converter.objectMapper.isEnabled(feature)
        where:
            generatorFeature << JACKSON_GENERATOR_FEATURES_AS_LIST
    }
}
