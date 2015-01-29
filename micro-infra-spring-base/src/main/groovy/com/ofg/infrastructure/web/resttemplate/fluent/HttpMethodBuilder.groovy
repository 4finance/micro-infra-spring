package com.ofg.infrastructure.web.resttemplate.fluent

import com.nurkiewicz.asyncretry.AsyncRetryExecutor
import com.nurkiewicz.asyncretry.RetryExecutor
import com.nurkiewicz.asyncretry.SyncRetryExecutor
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders
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

import java.util.concurrent.Executors

import static com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders.NO_PREDEFINED_HEADERS

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
    private final PredefinedHttpHeaders predefinedHeaders
    private RetryExecutor retryExecutor = SyncRetryExecutor.INSTANCE

    HttpMethodBuilder(RestOperations restOperations) {
        this('', restOperations, NO_PREDEFINED_HEADERS)
    }

    HttpMethodBuilder(String serviceUrl, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders) {
        this.predefinedHeaders = predefinedHeaders
        this.restOperations = restOperations
        this.serviceUrl = serviceUrl
    }

    /**
     * Attaches pre-configured {@link RetryExecutor} to this request in order to retry in case of failure.
     * The easiest way to add retry to your requests is to inject built-in {@link AsyncRetryExecutor}
     * and customize it, e.g. to retry 3 times after failure with 500 millisecond delays between attempts:
     *
     * <pre>
     * {@code
     * @Autowired AsyncRetryExecutor executor
     *
     * //...
     *
     * .retryUsing(executor.withMaxRetries(3).withFixedBackoff(500))
     * }
     * </pre>
     *
     * @param retryExecutor
     * @see <a href="https://github.com/nurkiewicz/async-retry">github.com/nurkiewicz/async-retry</a>
     */
    HttpMethodBuilder retryUsing(RetryExecutor retryExecutor) {
        this.retryExecutor = retryExecutor
        return this
    }

    DeleteMethod delete() {
        return new DeleteMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor)
    }

    GetMethod get() {
        return new GetMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor)
    }

    HeadMethod head() {
        return new HeadMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor)
    }

    OptionsMethod options() {
        return new OptionsMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor)
    }

    PostMethod post() {
        return new PostMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor)
    }

    PutMethod put() {
        return new PutMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor)
    }

}
