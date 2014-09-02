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
import org.springframework.web.client.RestOperations

@CompileStatic
class HttpMethodBuilder {

    private final RestOperations restOperations
    private final String serviceUrl

    HttpMethodBuilder(RestOperations restOperations) {
        this('', restOperations)
    }

    HttpMethodBuilder(String serviceUrl, RestOperations restOperations) {
        this.restOperations = restOperations
        this.serviceUrl = serviceUrl
    }

    public DeleteMethod delete() {
        return new DeleteMethodBuilder(serviceUrl, restOperations)
    }

    public GetMethod get() {
        return new GetMethodBuilder(serviceUrl, restOperations)
    }

    public HeadMethod head() {
        return new HeadMethodBuilder(serviceUrl, restOperations)
    }

    public OptionsMethod options() {
        return new OptionsMethodBuilder(serviceUrl, restOperations)
    }

    public PostMethod post() {
        return new PostMethodBuilder(serviceUrl, restOperations)
    }

    public PutMethod put() {
        return new PutMethodBuilder(serviceUrl, restOperations)
    }

}
