package com.ofg.infrastructure.web.resttemplate.fluent.common.request

interface HttpMethod<U, T> {

    U onUrl(URI url)

    T onUrlFromTemplate(String urlTemplate)

}
