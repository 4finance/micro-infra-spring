package com.ofg.infrastructure.web.view

import groovy.transform.PackageScope
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@PackageScope
@Controller
/**
 * Test controller that returns {@link SampleBean } which is then serialized to JSON.
 * In development or test environment JSON is supposed to be pretty printed.
 * In production environment JSON should not be pretty printed.
 *
 * Note that this class is created only for tests and is located in test scope (not packaged into jar)!
 */
class TestController {

    @RequestMapping(value = "/test", produces = "application/json", method = RequestMethod.GET)
    ResponseEntity<SampleBean> test() {
        return new ResponseEntity<SampleBean>(new SampleBean(sampleField: "sampleValue"), HttpStatus.OK)
    }
}

@PackageScope
class SampleBean {
    String sampleField
}
