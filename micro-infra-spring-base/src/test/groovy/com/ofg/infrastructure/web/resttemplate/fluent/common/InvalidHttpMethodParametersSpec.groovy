package com.ofg.infrastructure.web.resttemplate.fluent.common
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.InvalidHttpMethodParametersException

class InvalidHttpMethodParametersSpec extends HttpMethodSpec {
    
    def "should throw exception if wrong parameters are provided for get"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .get()
                    .onUrlFromTemplate(null)
                    .withVariables(null)
                    .anObject()
                    .ofType(BigDecimal)
        then:
            thrown(InvalidHttpMethodParametersException)
    }

    def "should throw exception if wrong parameters are provided for post"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .post()
                    .onUrlFromTemplate(null)
                    .withVariables(null)
                    .body('')                    
                    .anObject()
                    .ofType(BigDecimal)
        then:
            thrown(InvalidHttpMethodParametersException)
    }

    def "should throw exception if wrong parameters are provided for head"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .head()
                    .onUrlFromTemplate(null)
                    .withVariables(null)
                    .aResponseEntity()
        then:
            thrown(InvalidHttpMethodParametersException)
    }


    def "should throw exception if wrong parameters are provided for options"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .options()
                    .onUrlFromTemplate(null)
                    .withVariables(null)
                    .aResponseEntity()
                    .ofType(String)
        then:
            thrown(InvalidHttpMethodParametersException)
    }   

    def "should throw exception if wrong parameters are provided for delete"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restOperations, tracingInfo)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrlFromTemplate(null)
                    .withVariables(null)
                    .aResponseEntity()
        then:
            thrown(InvalidHttpMethodParametersException)
    }   
}
