package com.ofg.infrastructure.web.view

import groovy.transform.PackageScope
import groovy.transform.ToString
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@PackageScope
@Controller
class TestController {

    @RequestMapping(value = "/test", produces = "application/json", method = RequestMethod.GET)
    ResponseEntity<SampleBean> testGet() {
        return new ResponseEntity<SampleBean>(new SampleBean(sampleField: "sampleValue"), HttpStatus.OK)
    }

    @RequestMapping(value = "/test", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    ResponseEntity<SampleBean> testPost(@RequestBody SampleBean sampleBean) {
        if (sampleBean.sampleField == "sampleValue") {
            return new ResponseEntity<SampleBean>(HttpStatus.CREATED)
        } else {
            return new ResponseEntity<SampleBean>(HttpStatus.BAD_REQUEST)
        }
    }
}

@PackageScope
@ToString(includePackage = false)
class SampleBean {
    String sampleField
}
