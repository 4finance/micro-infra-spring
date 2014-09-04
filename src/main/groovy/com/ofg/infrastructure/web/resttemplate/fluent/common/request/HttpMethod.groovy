package com.ofg.infrastructure.web.resttemplate.fluent.common.request

/**
 * Starting point of the fluent interface.
 * 
 * If sending a request to an external service thr passed URLs must be treated as absolute paths 
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

}
