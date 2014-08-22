package com.ofg.infrastructure.web.resttemplate.fluent.common
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.InvalidHttpMethodParametersException

class InvalidHttpMethodParametersSpec extends HttpMethodSpec {
    
    def "should throw exception if wrong parameters are provided for get"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
        when:
            httpMethodBuilder
                    .get()
                    .onUrl(null)
                    .anObject()
                    .ofType(BigDecimal)
        then:
            thrown(InvalidHttpMethodParametersException)
    }

    def "should throw exception if wrong parameters are provided for post"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
        when:
            httpMethodBuilder
                    .post()
                    .onUrl(null)
                    .body('')                    
                    .anObject()
                    .ofType(BigDecimal)
        then:
            thrown(InvalidHttpMethodParametersException)
    }

    def "should throw exception if wrong parameters are provided for head"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
        when:
            httpMethodBuilder
                    .head()
                    .onUrl(null)
                    .aResponseEntity()
        then:
            thrown(InvalidHttpMethodParametersException)
    }


    def "should throw exception if wrong parameters are provided for options"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
        when:
            httpMethodBuilder
                    .options()
                    .onUrl(null)
                    .aResponseEntity()
                    .ofType(String)
        then:
            thrown(InvalidHttpMethodParametersException)
    }   

    def "should throw exception if wrong parameters are provided for delete"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
        when:
            httpMethodBuilder
                    .delete()
                    .onUrl(null)
                    .aResponseEntity()
        then:
            thrown(InvalidHttpMethodParametersException)
    }   
}
