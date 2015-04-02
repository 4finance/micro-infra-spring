package com.ofg.infrastructure.web.resttemplate.fluent.config

import org.springframework.http.client.SimpleClientHttpRequestFactory

/**
 * Convenient way to configure RestClient default params.
 * @see {@link ServiceRestClientConfigurer }
 *
 * @since 0.8.17
 */
class RestClientConfigurer {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory()
    Integer maxLogResponseChars = 4096

    RestClientConfigurer readTimeout(Integer readTimeout) {
        if (readTimeout != null) {
            this.requestFactory.readTimeout = readTimeout
        }
        return this
    }

    RestClientConfigurer connectTimeout(Integer connectTimeout) {
        if (connectTimeout != null) {
            this.requestFactory.connectTimeout = connectTimeout
        }
        return this
    }

    RestClientConfigurer maxLogResponseChars(Integer maxLogResponseChars) {
        if (maxLogResponseChars != null) {
            this.maxLogResponseChars = maxLogResponseChars
        }
        return this
    }
}