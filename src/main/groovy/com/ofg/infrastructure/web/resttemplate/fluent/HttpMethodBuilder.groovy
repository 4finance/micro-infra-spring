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

/**
 * Point of entry of the fluent API over {@link RestOperations}.
 * This class gives methods for each of the HttpMethods and delegates to the root of
 * the fluent API of that method.
 * 
 * @see DeleteMethod
 * @see GetMethod
 * @see HeadMethod
 * @see OptionsMethod
 * @see PostMethod
 * @see PutMethod
 */
@CompileStatic
class HttpMethodBuilder {

    private final RestOperations restOperations

    /**
     * URL of an external URL or a service retrieved via service discovery 
     */
    private final String serviceUrl

    HttpMethodBuilder(RestOperations restOperations) {
        this('', restOperations)
    }

    HttpMethodBuilder(String serviceUrl, RestOperations restOperations) {
        this.restOperations = restOperations
        this.serviceUrl = serviceUrl
    }

    DeleteMethod delete() {
        return new DeleteMethodBuilder(serviceUrl, restOperations)
    }

    GetMethod get() {
        return new GetMethodBuilder(serviceUrl, restOperations)
    }

    HeadMethod head() {
        return new HeadMethodBuilder(serviceUrl, restOperations)
    }

    OptionsMethod options() {
        return new OptionsMethodBuilder(serviceUrl, restOperations)
    }

    PostMethod post() {
        return new PostMethodBuilder(serviceUrl, restOperations)
    }

    PutMethod put() {
        return new PutMethodBuilder(serviceUrl, restOperations)
    }

}
