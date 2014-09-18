package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

/**
 * Interface for HttpMethods that can return location from Http headers.
 * It's a helper interface since you can always retrieve location from the 
 * {@link org.springframework.http.ResponseEntity}.
 */
interface LocationReceiving {

    URI forLocation()

}