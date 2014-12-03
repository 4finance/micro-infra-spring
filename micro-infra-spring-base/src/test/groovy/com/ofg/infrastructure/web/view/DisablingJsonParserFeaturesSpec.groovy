package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonParser
import spock.lang.Unroll

class DisablingJsonParserFeaturesSpec extends JsonJacksonFeaturesSpec implements JacksonParserFeaturesTrait {

    def setupSpec() {
        System.setProperty("json.jackson.parser.off", JACKSON_PARSER_FEATURES_AS_LIST.join(','))
    }

    def cleanupSpec() {
        System.properties.remove("json.jackson.parser.off")
    }

    @Unroll
    def "should enable JsonParser's feature #parserFeature"() {
        given:
            def converter = getFirstConfiguredJacksonMessageConverter()
            def feature = JsonParser.Feature.valueOf(parserFeature)
        expect:
            !converter.objectMapper.isEnabled(feature)
        where:
            parserFeature << JACKSON_PARSER_FEATURES_AS_LIST
    }
}
