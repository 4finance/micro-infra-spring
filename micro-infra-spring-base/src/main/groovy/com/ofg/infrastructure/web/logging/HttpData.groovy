package com.ofg.infrastructure.web.logging

import groovy.transform.CompileStatic

@CompileStatic
class HttpData {
    Map<String, String> headers

    String httpMethod

    String url

    String path

    int httpStatus

    String content
}
