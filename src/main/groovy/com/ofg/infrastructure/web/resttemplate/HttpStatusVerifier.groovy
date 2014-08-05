package com.ofg.infrastructure.web.resttemplate

import groovy.transform.TypeChecked
import org.springframework.http.HttpStatus

@TypeChecked
class HttpStatusVerifier {
    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series()
        return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series))
    }
}
