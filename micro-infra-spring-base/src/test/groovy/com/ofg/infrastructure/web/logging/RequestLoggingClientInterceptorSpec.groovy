package com.ofg.infrastructure.web.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.web.resttemplate.custom.ResponseException
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration

import java.util.regex.Pattern

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching

@ContextConfiguration(classes = [RequestLoggingSpecConfiguration, BaseConfiguration], loader = SpringApplicationContextLoader)
class RequestLoggingClientInterceptorSpec extends MicroserviceMvcWiremockSpec {

    @Autowired private ServiceRestClient serviceRestClient
    @Autowired private HttpMockServer httpMockServer
    private static final String CONTENT_TYPE = 'Content-Type'
    private static final String JSON_REQ_RESOURCE_NAME = 'requestLogging/message_req.json'
    private static final String XML_REQ_RESOURCE_NAME = 'requestLogging/message_req.xml'

    def 'JSON: Should create log REQ/RES with all HTTP elements for POST json message'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPostIntegration('/logTestJson', 201, [:])
        when:
            callPost('/logTestJson', readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->method.*POST.*url.*/logTestJson.*headers.*application/json.*body.*affiliates.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*status.*201.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES with all HTTP elements for GET json message'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubGetIntegration('/logTestJson', 200, readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        when:
            callGet('/logTestJson', [:])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->method.*GET.*url.*/logTestJson.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*application/json.*status.*200.*body.*affiliates.*',1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should not create log REQ/RES for GET call due to skip configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubGetIntegration('/logTestJsonSkip', 200, readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        when:
            callGet('/logTestJsonSkip', [:])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->method.*GET.*url.*/logTestJson.*', 0)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*application/json.*status.*200.*body.*response.*',0)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST call due to skip configuration only for GET'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPostIntegration('/logTestJsonSkip', 201, [:])
        when:
            callPost('/logTestJsonSkip', readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->method.*POST.*url.*/logTestJson.*headers.*application/json.*body.*affiliates.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*status.*201.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }


    def 'JSON: Should create log REQ/RES for POST call and remove REQ/RES headers due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPostIntegration('/logTestJsonSkipHeaders', 201, ['header-req-to-remove-1':'header-req-to-remove-1-value',
                                                                 'header-req-to-remove-2':'header-req-to-remove-2-value'])
        when:
            callPost('/logTestJsonSkipHeaders', readResource(JSON_REQ_RESOURCE_NAME), ['header-req-to-remove-1':'header-req-to-remove-1-value',
                                                                                       'header-req-to-remove-2':'header-req-to-remove-2-value',
                                                                                       (CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->method.*POST.*url.*/logTestJsonSkipHeaders.*headers.*header-req-to-remove.*', 0)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*header-res-to-remove.*status.*201.*', 0)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST call and set REMOVED on fields from REQ due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPostIntegration('/logTestJsonObfuscateFieldsMsg', 201, [:])
        when:
            callPost('/logTestJsonObfuscateFieldsMsg', readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String) : MediaType.APPLICATION_JSON.toString()])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->.*companyBankAccount=REMOVED.*activeFrom=REMOVED.*limit=REMOVED.*requiredAdditionalDocuments=REMOVED.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for GET call and set REMOVED on fields from RES due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubGetIntegration('/logTestJsonObfuscateFieldsMsg', 200, readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        when:
            callGet('/logTestJsonObfuscateFieldsMsg', [:])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->.*method.*GET.*url.*/logTestJsonObfuscateFieldsMsg.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*application/json.*status.*200.*body.*companyBankAccount=REMOVED.*activeFrom=REMOVED.*limit=REMOVED.*requiredAdditionalDocuments=REMOVED.*',1)
            }
        cleanup:
        removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST finished with exception'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPostIntegration('/logTestJsonException', 501, [:])
        when:
            callPost('/logTestJsonException', readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String) : MediaType.APPLICATION_JSON.toString()])
        then:
            thrown(ResponseException)
        and:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'XML: Should create log REQ/RES for POST call and set REMOVED on fields from REQ due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPostIntegration('/logTestXmlObfuscateFieldsMsg', 201, [:])
        when:
            callPost('/logTestXmlObfuscateFieldsMsg', readResource(XML_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String) : MediaType.APPLICATION_XML.toString()])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->.*<acceptNews>REMOVED</acceptNews>.*<location2>REMOVED</location2>.*<actualFrom>REMOVED</actualFrom>.*<declaredBudget>REMOVED</declaredBudget>.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'XML: Should create log REQ/RES for GET call and set REMOVED on fields from RES due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubGetIntegration('/logTestXmlObfuscateFieldsMsg', 200, readResource(XML_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_XML.toString()])
        when:
            callGet('/logTestXmlObfuscateFieldsMsg', [:])
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CLIENT->.*method.*GET.*url.*/logTestXmlObfuscateFieldsMsg.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CLIENT<-headers.*application/xml.*status.*200.*body.*<acceptNews>REMOVED</acceptNews>.*<location2>REMOVED</location2>.*<actualFrom>REMOVED</actualFrom>.*<declaredBudget>REMOVED</declaredBudget>.*',1)
            }
        cleanup:
            removeAppender(mockAppender)
    }


    @Configuration
    @EnableObfuscatedLogging
    static class RequestLoggingSpecConfiguration {}

    private void callGet(String path, Map<String, String> requestHeaders) {
        serviceRestClient.forExternalService()
                .get()
                .onUrl(new URI("http://localhost:${httpMockServer.port()}${path}"))
                .withHeaders().headers(requestHeaders)
                .andExecuteFor()
                .aResponseEntity()
                .ofType(String)
    }

    private void callPost(String path, String requestBody, Map<String, String> requestHeaders) {
        serviceRestClient.forExternalService()
                .post()
                .onUrl(new URI("http://localhost:${httpMockServer.port()}${path}"))
                .body(requestBody)
                .withHeaders().headers(requestHeaders)
                .andExecuteFor()
                .aResponseEntity()
                .ofType(String)
    }

    def checkInteraction(Appender appender, String pattern, int count){
        Pattern p = Pattern.compile(pattern)
        count * appender.doAppend({ILoggingEvent e ->
            p.matcher(e.formattedMessage).matches()
        })
    }

    static String readResource(String resourcePath) {
        return new String(this.getClass().getResource('/' + resourcePath).getBytes()).replace('\n', '').replace('\r', '');
    }

    private Appender insertAppender(Appender appender) {
        root().addAppender(appender);
        return appender
    }

    private void removeAppender(Appender appender) {
        root().detachAppender(appender)
    }

    private Logger root() {
        return (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
    }

    private void stubGetIntegration(String path, int status, String responseBody, Map<String, String> responseHeaders){
        ResponseDefinitionBuilder response = aResponse()
        responseHeaders?.each { String key, String value -> response.withHeader(key, value)}
        stubInteraction(get(urlMatching(path)), response.withStatus(status).withBody(responseBody))
    }

    private void stubPostIntegration(String path, int status, Map<String, String> responseHeaders){
        ResponseDefinitionBuilder response = aResponse()
        responseHeaders?.each { String key, String value -> response.withHeader(key, value)}
        stubInteraction(post(urlMatching(path)), response.withStatus(status))
    }
}
