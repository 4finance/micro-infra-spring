package com.ofg.infrastructure.web.resttemplate.fluent

import spock.lang.Specification
import spock.lang.Unroll


class UrlUtilsSpec extends Specification {

    def "should add parameter to query string" () {
        given:
            Map<String, String> parameters = new HashMap<>();
            parameters.put("firstParam", "firstParamValue")
            parameters.put("secondParam", "secondParamValue")
        when:
            
            URI result = UrlUtils.addQueryParametersToUri(new URI(""), parameters)
        then:
            result.toString() == "?firstParam=firstParamValue&secondParam=secondParamValue"
    }

    def "should not add 'null' string to null parameter in query string" () {
        given:
            Map<String, String> parameters = new HashMap<>();
            parameters.put("firstParam", null)
            parameters.put("secondParam", "")
        when:
            
            URI result = UrlUtils.addQueryParametersToUri(new URI(""), parameters)
        then:
            result.toString() == "?firstParam=&secondParam="
    }

    @Unroll("should not add '#paramName' as parameter name")
    def "should not add null or empty parameter name to query string" () {
        given:
            Map<String, String> parameters = new HashMap<>();
            parameters.put(paramName, "valueOne")
        when:
            UrlUtils.addQueryParametersToUri(new URI(""), parameters)
        then:
            def e = thrown(IllegalArgumentException)
            e.message?.contains("Parameter name should be not null or empty")
        where:
            paramName << ["", null]
    }

    def "should deal with good, bad, null and empty parameters in query string" () {
        given:
            Map<String, String> parameters = new HashMap<>();
            parameters.put("firstParameter", "valueOne")
            parameters.put(paramName, "valueTwo")
            parameters.put("secondParameter", null)
        when:
            URI rasult = UrlUtils.addQueryParametersToUri(new URI(""), parameters)         
        then:
            thrown(IllegalArgumentException)
            !rasult.toString().contains("secondParameter")
        where:
            paramName << ["", null]
    }
    
}