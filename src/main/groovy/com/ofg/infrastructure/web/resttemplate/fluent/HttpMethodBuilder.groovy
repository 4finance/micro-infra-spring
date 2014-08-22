package com.ofg.infrastructure.web.resttemplate.fluent

import com.ofg.infrastructure.web.resttemplate.fluent.delete.DeleteMethod
import com.ofg.infrastructure.web.resttemplate.fluent.delete.DeleteMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.get.GetMethod
import com.ofg.infrastructure.web.resttemplate.fluent.get.GetMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.head.HeadMethod
import com.ofg.infrastructure.web.resttemplate.fluent.head.HeadMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.options.OptionsMethod
import com.ofg.infrastructure.web.resttemplate.fluent.options.OptionsMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.post.PostMethod
import com.ofg.infrastructure.web.resttemplate.fluent.post.PostMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.put.PutMethod
import com.ofg.infrastructure.web.resttemplate.fluent.put.PutMethodBuilder
import groovy.transform.CompileStatic
import org.springframework.web.client.RestTemplate

@CompileStatic
class HttpMethodBuilder {

    private final RestTemplate restTemplate
    private final String serviceUrl

    HttpMethodBuilder(RestTemplate restTemplate) {
        this('', restTemplate)
    }

    HttpMethodBuilder(String serviceUrl, RestTemplate restTemplate) {
        this.restTemplate = restTemplate
        this.serviceUrl = serviceUrl
    }

    public DeleteMethod delete() {
        return new DeleteMethodBuilder(serviceUrl, restTemplate)
    }

    public GetMethod get() {
        return new GetMethodBuilder(serviceUrl, restTemplate)
    }

    public HeadMethod head() {
        return new HeadMethodBuilder(serviceUrl, restTemplate)
    }

    public OptionsMethod options() {
        return new OptionsMethodBuilder(serviceUrl, restTemplate)
    }

    public PostMethod post() {
        return new PostMethodBuilder(serviceUrl, restTemplate)
    }

    public PutMethod put() {
        return new PutMethodBuilder(serviceUrl, restTemplate)
    }

}
