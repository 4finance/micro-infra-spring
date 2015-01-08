package com.ofg.infrastructure.base.dsl

import groovy.json.JsonSlurper
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * A class that contains static helper methods that help you match contents
 * of the received responses
 */
class Matchers {
    
    static Matcher<String> equalsReferenceJson(String referenceJson) {
        return new TypeSafeMatcher<String>(){

            @Override
            protected boolean matchesSafely(String item) {
                def expectedRoot = new JsonSlurper().parseText(referenceJson)
                def actualRoot = new JsonSlurper().parseText(item)
                return expectedRoot == actualRoot
            }

            @Override
            void describeTo(Description description) {
                description.appendValue(referenceJson)
            }
        }
    }
    
    static Matcher<String> equalsReferenceMongoJson(String referenceJson) {
        return new TypeSafeMatcher<String>(){

            @Override
            protected boolean matchesSafely(String item) {
                def expectedRoot = new JsonSlurper().parseText(referenceJson)
                def actualRoot = new JsonSlurper().parseText(item)
                expectedRoot.remove('_id')
                actualRoot.remove('_id')
                return expectedRoot == actualRoot
            }

            @Override
            void describeTo(Description description) {
                description.appendValue(referenceJson)
            }
        }
    }
    
}
