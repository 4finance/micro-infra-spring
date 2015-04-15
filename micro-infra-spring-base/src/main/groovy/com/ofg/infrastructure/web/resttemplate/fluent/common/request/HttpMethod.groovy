package com.ofg.infrastructure.web.resttemplate.fluent.common.request

import com.netflix.hystrix.HystrixCommand

import java.util.concurrent.Callable

/**
 * Starting point of the fluent interface.
 * 
 * If sending a request to an external service the passed URLs must be treated as absolute paths
 * i.e. url: http://4finance.net should send to http://4finance.net.
 *
 * If sending to a collaborator will be treated as a part of path 
 * i.e. url: ws/api/loans for service http://4finance.net should send to http://4finance.net/ws/api/loans  
 * 
 * @param < U > - class to be returned when URL is passed
 * @param < T > - class to be returned when template URL is passed
 */
interface HttpMethod<U, T> {

    /**
     * Provides a {@link URI} to which you want to send a request.  
     * 
     * @param url
     */
    U onUrl(URI url)

    /**
     * Provides a String url to which you want to send a request.  
     *
     * @param url
     */
    U onUrl(String url)

    /**
     * Provides a template URL to which you want to send a request.  
     *
     * @param url
     */
    T onUrlFromTemplate(String urlTemplate)

    /**
     * Adds Hystrix circuit breaker around every REST call.
     * 
     * Example:
     * <code>
     * .withCircuitBreaker(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group"))
     *      .andCommandKey(HystrixCommandKey.Factory.asKey("Command")))
     * </code>
     *
     * @param setter
     *      Fluent interface for HystrixCommand constructor arguments
     *
     */
    HttpMethod<U, T> withCircuitBreaker(HystrixCommand.Setter setter)

    /**
     * Adds Hystrix circuit breaker with fallback around every REST call.
     *
     * The value returned by the closure will be wrapped in {@link org.springframework.http.ResponseEntity}.
     * You can also provide directly the {@link org.springframework.http.ResponseEntity} and it will get returned.
     *
     * Example:
     * <code>
     * .withCircuitBreaker(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group"))
     *      .andCommandKey(HystrixCommandKey.Factory.asKey("Command")), {return new ResponseEntity<String>("service unavailable", HttpStatus.METHOD_FAILURE)})
     * </code>
     *
     * @param setter
     *      Fluent interface for HystrixCommand constructor arguments
     * @param hystrixFallback
     *      @see HystrixCommand#getFallback()
     */
    HttpMethod<U, T> withCircuitBreaker(HystrixCommand.Setter setter, Callable hystrixFallback)
}
