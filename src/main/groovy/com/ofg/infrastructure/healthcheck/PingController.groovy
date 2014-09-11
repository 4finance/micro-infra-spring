package com.ofg.infrastructure.healthcheck
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.HEAD

/**
 * {@link RestController} that responds with OK when server is alive
 */
@RestController
@CompileStatic
@PackageScope
@Api(value = "ping", description = "PING API")
class PingController {

    @RequestMapping(value = "/ping", method = [GET, HEAD], produces = TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Ping server", notes = "Returns OK if server is alive")
    String ping() {
        return "OK"
    }
}
