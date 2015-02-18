package com.ofg.infrastructure.hystrix

import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandProperties

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey

class CircuitBreakers {

    static HystrixCommand.Setter anyWithTimeout(int timeoutInMillis) {
        //below workaround comes from https://jira.codehaus.org/browse/GROOVY-6286
        HystrixCommandProperties.Setter commandPropertiesSetter = HystrixCommandProperties.invokeMethod("Setter", null)
        return any().andCommandPropertiesDefaults(commandPropertiesSetter.withTimeoutInMilliseconds(timeoutInMillis))
    }

    static HystrixCommand.Setter any() {
        return withGroupKey(asKey(""))
    }
}
