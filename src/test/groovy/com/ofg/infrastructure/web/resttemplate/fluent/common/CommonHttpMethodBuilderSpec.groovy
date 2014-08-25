package com.ofg.infrastructure.web.resttemplate.fluent.common
import com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.HEAD
import static org.springframework.http.HttpMethod.OPTIONS
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpMethod.PUT

class CommonHttpMethodBuilderSpec extends HttpMethodSpec {
    
    def "should be able to send a request and ignore the response"() {
        given:
            httpMethodBuilder = new HttpMethodBuilder(restTemplate)
            String url = 'http://some.url/api/objects'
            HttpEntity expectedHttpEntity = new HttpEntity('')
        when:
            httpMethodBuilder
                            ."${method.toString().toLowerCase()}"()
                                .onUrl(url)          
                                .httpEntity(expectedHttpEntity)
                                    .execute()
        then:
            1 * restTemplate.exchange(new URI(url),
                    method as HttpMethod,
                    expectedHttpEntity,
                    Object)
        where:
            method << [GET, HEAD, OPTIONS, POST, PUT, DELETE]
            
    }

}
