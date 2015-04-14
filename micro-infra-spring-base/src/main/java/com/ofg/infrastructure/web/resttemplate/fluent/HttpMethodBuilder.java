package com.ofg.infrastructure.web.resttemplate.fluent;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.nurkiewicz.asyncretry.SyncRetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.PredefinedHttpHeaders;
import com.ofg.infrastructure.web.resttemplate.fluent.delete.DeleteMethod;
import com.ofg.infrastructure.web.resttemplate.fluent.delete.DeleteMethodBuilder;
import com.ofg.infrastructure.web.resttemplate.fluent.get.GetMethod;
import com.ofg.infrastructure.web.resttemplate.fluent.get.GetMethodBuilder;
import com.ofg.infrastructure.web.resttemplate.fluent.head.HeadMethod;
import com.ofg.infrastructure.web.resttemplate.fluent.head.HeadMethodBuilder;
import com.ofg.infrastructure.web.resttemplate.fluent.options.OptionsMethod;
import com.ofg.infrastructure.web.resttemplate.fluent.options.OptionsMethodBuilder;
import com.ofg.infrastructure.web.resttemplate.fluent.post.PostMethod;
import com.ofg.infrastructure.web.resttemplate.fluent.post.PostMethodBuilder;
import com.ofg.infrastructure.web.resttemplate.fluent.put.PutMethod;
import com.ofg.infrastructure.web.resttemplate.fluent.put.PutMethodBuilder;
import groovy.transform.CompileStatic;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.Callable;

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
public class HttpMethodBuilder {
    public HttpMethodBuilder(RestOperations restOperations) {
        this(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "";
            }

        }, restOperations, PredefinedHttpHeaders.NO_PREDEFINED_HEADERS);
    }

    public HttpMethodBuilder(Callable<String> serviceUrl, RestOperations restOperations, PredefinedHttpHeaders predefinedHeaders) {
        this.predefinedHeaders = predefinedHeaders;
        this.restOperations = restOperations;
        this.serviceUrl = serviceUrl;
    }

    /**
     * Attaches pre-configured {@link RetryExecutor} to this request in order to retry in case of failure.
     * The easiest way to add retry to your requests is to inject built-in {@link AsyncRetryExecutor}
     * and customize it, e.g. to retry 3 times after failure with 500 millisecond delays between attempts:
     * <p/>
     * <pre>
     * {@code
     *
     * @param retryExecutor
     * @Autowired AsyncRetryExecutor executor
     * <p/>
     * //...
     * <p/>
     * .retryUsing(executor.withMaxRetries(3).withFixedBackoff(500))
     * }
     * </pre>
     * @see <a href="https://github.com/nurkiewicz/async-retry">github.com/nurkiewicz/async-retry</a>
     */
    public HttpMethodBuilder retryUsing(RetryExecutor retryExecutor) {
        this.retryExecutor = retryExecutor;
        return this;
    }

    public DeleteMethod delete() {
        return new DeleteMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor);
    }

    public GetMethod get() {
        return new GetMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor);
    }

    public HeadMethod head() {
        return new HeadMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor);
    }

    public OptionsMethod options() {
        return new OptionsMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor);
    }

    public PostMethod post() {
        return new PostMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor);
    }

    public PutMethod put() {
        return new PutMethodBuilder(serviceUrl, restOperations, predefinedHeaders, retryExecutor);
    }

    private final RestOperations restOperations;
    public static final Callable<String> EMPTY_HOST = new Callable<String>() {
        @Override
        public String call() throws Exception {
            return "";
        }

    };
    /**
     * URL of an external URL or a service retrieved via service discovery
     */
    private final Callable<String> serviceUrl;
    private final PredefinedHttpHeaders predefinedHeaders;
    private RetryExecutor retryExecutor = SyncRetryExecutor.INSTANCE;
}
