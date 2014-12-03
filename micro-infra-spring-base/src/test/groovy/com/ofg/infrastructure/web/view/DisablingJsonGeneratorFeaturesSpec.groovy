package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonGenerator
import spock.lang.Unroll

class DisablingJsonGeneratorFeaturesSpec extends JsonJacksonFeaturesSpec implements JacksonGeneratorFeaturesTrait {

    def setupSpec() {
        System.setProperty("json.jackson.generator.off", JACKSON_GENERATOR_FEATURES_AS_LIST.join(','))
    }

    def cleanupSpec() {
        System.properties.remove("json.jackson.generator.off")
    }

    @Unroll
    def "should enable JsonGenerator's feature #generatorFeature"() {
        given:
            def converter = getFirstConfiguredJacksonMessageConverter()
            def feature = JsonGenerator.Feature.valueOf(generatorFeature)
        expect:
            !converter.objectMapper.isEnabled(feature)
        where:
            generatorFeature << JACKSON_GENERATOR_FEATURES_AS_LIST
    }
}
