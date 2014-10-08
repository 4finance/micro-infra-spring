package com.ofg.infrastructure.web.resttemplate.custom

import groovy.transform.TypeChecked
import org.springframework.http.HttpStatus


@TypeChecked
class HttpStatusVerifier {
    /**
     * Verifies whether the passed {@link HttpStatus} is either client or server error
     *
     * @param status
     * @return
     */
    public static boolean isError(HttpStatus status) {
        return status.series() in [HttpStatus.Series.CLIENT_ERROR, HttpStatus.Series.SERVER_ERROR]
    }
}
