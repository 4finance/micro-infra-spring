package com.ofg.infrastructure.web.resttemplate.fluent.options

import org.springframework.http.HttpMethod

interface AllowHeaderReceiving {

    Set<HttpMethod> allow()

}