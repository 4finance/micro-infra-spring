package com.ofg.infrastructure.web.exception

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
class TestController {
    @RequestMapping(value = "/test", produces = "application/json", method = RequestMethod.POST)
    String test(@RequestBody @Valid TestRequest request, BindingResult result) {
        checkIfResultHasErrors(result)
        return "OK"
    }

    private void checkIfResultHasErrors(BindingResult result) {
        if (result.hasErrors()) {
            throw new BadParametersException(result.allErrors)
        }
    }
}

class TestRequest {
    @AssertTrue
    boolean shouldBeTrue
}
