package com.ofg.infrastructure.web.view

import com.fasterxml.jackson.core.JsonParser
import spock.lang.Unroll

import static com.ofg.infrastructure.web.view.JacksonFeaturesTestConstants.JACKSON_PARSER_FEATURES_AS_LIST


class EnablingJsonParserFeaturesSpec extends JsonJacksonFeaturesSpec {

    def setupSpec() {
        System.setProperty("json.jackson.parser.on", JACKSON_PARSER_FEATURES_AS_LIST.join(','))
    }

    def cleanupSpec() {
        System.properties.remove("json.jackson.parser.on")
    }

    def "should enable JsonParser's feature #parserFeature"() {
        given:
            def converter = getFirstConfiguredJacksonMessageConverter()
            def feature = JsonParser.Feature.valueOf(parserFeature)
        expect:
            converter.objectMapper.isEnabled(feature)
        where:
            parserFeature << JACKSON_PARSER_FEATURES_AS_LIST
    }
}
