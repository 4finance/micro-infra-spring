package com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class UrlParsingUtilsSpec extends Specification {

    def 'should fail to instantiate a utility class'() {
        when:
            UrlParsingUtils.newInstance()
        then:
            thrown(UnsupportedOperationException)
    }
    
    def "should prepend a slash if path doesn't start with a slash"() {
        given:
            String uriAsString = 'some/path'
            URI uri = new URI(uriAsString)
            String expectedUri = "/$uriAsString"
        when:
            String actualUri = UrlParsingUtils.prependSlashIfPathIsRelative(uri)
        then:
            actualUri == expectedUri  
    }
    
    def 'should leave path as it is if path starts with a slash'() {
        given:
            String uriAsString = '/some/path'
            URI uri = new URI(uriAsString)
            String expectedUri = uriAsString
        when:
            String actualUri = UrlParsingUtils.prependSlashIfPathIsRelative(uri)
        then:
            actualUri == expectedUri  
    }
    
    def "should prepend a slash if string path doesn't start with a slash"() {
        given:
            String uri = 'some/path'
        when:
            String actualUri = UrlParsingUtils.prependSlashIfPathDoesNotStartWithSlash(uri)
        then:
            actualUri == '/some/path'
    }
    
    def 'should leave path as it is if string path starts with a slash'() {
        given:
            String uri = '/some/path'
        when:
            String actualUri = UrlParsingUtils.prependSlashIfPathDoesNotStartWithSlash(uri)
        then:
            actualUri == uri
    }
    
    def 'should leave path as it is if path is absolute'() {
        given:
            String uriAsString = 'http://localhost:1234'
            URI uri = new URI(uriAsString)
            String expectedUri = uriAsString
        when:
            String actualUri = UrlParsingUtils.prependSlashIfPathIsRelative(uri)
        then:
            actualUri == expectedUri  
    }

    def 'should append host "#host" to String path for "#path" to receive url "#expectedUri"'() {
        when:
            String actualUri = UrlParsingUtils.appendPathToHost(host, path)
        then:
            actualUri == expectedUri
        where:
            host                    | path                                           || expectedUri
            ''                      | 'http://localhost:1234'                        || 'http://localhost:1234'
            ''                      | 'http://localhost:1234/some/{var}/path/{var2}' || 'http://localhost:1234/some/{var}/path/{var2}'
            'http://localhost:1234' | 'some/path'                                    || 'http://localhost:1234/some/path'
            'http://localhost:1234' | '/some/path'                                   || 'http://localhost:1234/some/path'
            'http://localhost:1234' | 'some/{var}/path/{var2}'                       || 'http://localhost:1234/some/{var}/path/{var2}'
            'http://localhost:1234' | '/some/{var}/path/{var2}'                      || 'http://localhost:1234/some/{var}/path/{var2}'
            'http://localhost:1234' | 'INVALID_/\\_URI'                              || 'http://localhost:1234/INVALID_/\\_URI'
    }

}
