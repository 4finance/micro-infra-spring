package com.ofg.infrastructure.web.exception

import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid
import javax.validation.constraints.AssertTrue

@TypeChecked
@RestController
@PackageScope
/**
 *  Example of controller with methods throwing various exceptions (needed to test exception handling).
 *
 *  Note that this class is created only for tests and is located in test scope (not packaged into jar)!
 */
class TestController {

    @RequestMapping(value = "/test", produces = "application/json", method = RequestMethod.POST)
    String test(@RequestBody @Valid TestRequest request, BindingResult result) {
        checkIfResultHasErrors(result)
        return "OK"
    }

    @RequestMapping(value = "/testLowestPrecedence", produces = "application/json", method = RequestMethod.GET)
    String testLowestPrecedence() throws Exception {
        throw new Exception()
    }

    private void checkIfResultHasErrors(BindingResult result) {
        if (result.hasErrors()) {
            throw new BadParametersException(result.allErrors)
        }
    }
}

@PackageScope
class TestRequest {
    @AssertTrue
    boolean shouldBeTrue
}
