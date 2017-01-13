package com.ofg.infrastructure.web.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.MicroserviceMvcWiremockSpec
import com.ofg.infrastructure.discovery.web.HttpMockServer
import com.ofg.infrastructure.web.logging.config.LogsConfig
import feign.Feign
import feign.FeignException
import feign.Headers
import feign.RequestLine
import feign.Response
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
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
class FeignCallObfuscatingLoggerSpec extends MicroserviceMvcWiremockSpec {

    private static final String JSON_REQ_RESOURCE_NAME = 'requestLogging/message_req.json'
    private static final String XML_REQ_RESOURCE_NAME = 'requestLogging/message_req.xml'
    private static final String CONTENT_TYPE = 'Content-Type'
    
    @Autowired private RequestResponseLogger reqResLogger;
    @Autowired private HttpMockServer httpMockServer
    @Autowired private LogsConfig props;
    RequestDataProvider requestDataProvider = new RequestDataProvider(20000);
    
    private TestFeignClient testFeignClient;

    def setup() {
        RequestIdProvider requestIdProvider = Mock(RequestIdProvider)
        requestIdProvider.getRequestId() >> UUID.randomUUID().toString()
        FeignCallObfuscatingLogger logger = new FeignCallObfuscatingLogger(props, requestDataProvider, requestIdProvider, reqResLogger)
        
        testFeignClient = Feign.builder().
                logger(logger).
                logLevel(feign.Logger.Level.FULL).
                target(TestFeignClient, "http://localhost:${httpMockServer.port()}")
    }

    def 'JSON: Should create log REQ/RES with all HTTP elements for POST json message'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPost('/logTestJson', 201, [:])
        when:
            testFeignClient.testPost(readResource(JSON_REQ_RESOURCE_NAME));
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
            stubGet('/logTestJson', 200, readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        when:
            testFeignClient.testGet();
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
            stubGet('/logTestJsonSkip', 200, readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        when:
            testFeignClient.testGetJsonSkip()
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
            stubPost('/logTestJsonSkip', 201, [:])
        when:
            testFeignClient.testPostJsonSkip(readResource(JSON_REQ_RESOURCE_NAME))
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
            stubPost('/logTestJsonSkipHeaders', 201, ['header-req-to-remove-1':'header-req-to-remove-1-value',
                                                             'header-req-to-remove-2':'header-req-to-remove-2-value'])
        when:
            testFeignClient.testPostJsonSkipHeaders(readResource(JSON_REQ_RESOURCE_NAME))
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
            stubPost('/logTestJsonObfuscateFieldsMsg', 201, [:])
        when:
            testFeignClient.testPostJsonObfuscateFieldsMsg(readResource(JSON_REQ_RESOURCE_NAME))
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
            stubGet('/logTestJsonObfuscateFieldsMsg', 200, readResource(JSON_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_JSON.toString()])
        when:
            testFeignClient.testGetJsonObfuscateFieldMsg()
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
            stubPost('/logTestJsonException', 501, [:])
        when:
            testFeignClient.testPostJsonException(readResource(JSON_REQ_RESOURCE_NAME))
        then:
            thrown(FeignException)
        and:
            interaction {
                checkInteraction(mockAppender,'.*REQ CLIENT->.*', 1)
                and:
                checkInteraction(mockAppender,'.*RES CLIENT<-.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }
    
    def 'JSON: Should skip logging response if request data is gone from cache'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            TestFeignClient feignClient = feignClientLosingRequests()
            stubPost('/logTestJson', 201, [:])
        when:
            feignClient.testPost(readResource(JSON_REQ_RESOURCE_NAME));
        then:
            interaction {
                checkInteraction(mockAppender,'.*REQ CLIENT->.*', 1)
                and:
                checkInteraction(mockAppender,'.*RES CLIENT<-.*', 0)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'XML: Should create log REQ/RES for POST call and set REMOVED on fields from REQ due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
            stubPost('/logTestXmlObfuscateFieldsMsg', 201, [:])
        when:
            testFeignClient.testPostXmlObfuscateFieldsMsg(readResource(XML_REQ_RESOURCE_NAME))
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
            stubGet('/logTestXmlObfuscateFieldsMsg', 200, readResource(XML_REQ_RESOURCE_NAME), [(CONTENT_TYPE as String)  : MediaType.APPLICATION_XML.toString()])
        when:
            testFeignClient.testGetXmlObfuscateFieldsMsg();
        then:
            interaction {
                checkInteraction(mockAppender,'.*REQ CLIENT->.*method.*GET.*url.*/logTestXmlObfuscateFieldsMsg.*', 1)
                and:
                checkInteraction(mockAppender,'.*RES CLIENT<-headers.*application/xml.*status.*200.*body.*<acceptNews>REMOVED</acceptNews>.*<location2>REMOVED</location2>.*<actualFrom>REMOVED</actualFrom>.*<declaredBudget>REMOVED</declaredBudget>.*',1)
            }
        cleanup:
            removeAppender(mockAppender)
    }
    
    interface TestFeignClient {
        @RequestLine("GET /logTestJson")
        Response testGet();

        @RequestLine("POST /logTestJson")
        @Headers("Content-type: application/json")
        Response testPost(String content);

        @RequestLine("GET /logTestJsonSkip")
        Response testGetJsonSkip();

        @RequestLine("POST /logTestJsonSkip")
        @Headers("Content-type: application/json")
        Response testPostJsonSkip(String content);

        @RequestLine("POST /logTestJsonSkipHeaders")
        @Headers([
                "Content-type: application/json",
                "header-req-to-remove-1: header-req-to-remove-1-value",
                "header-req-to-remove-2: header-req-to-remove-2-value"
        ])
        Response testPostJsonSkipHeaders(String content);

        @RequestLine("POST /logTestJsonObfuscateFieldsMsg")
        @Headers("Content-type: application/json")
        Response testPostJsonObfuscateFieldsMsg(String content);

        @RequestLine("GET /logTestJsonObfuscateFieldsMsg")
        Response testGetJsonObfuscateFieldMsg();

        @RequestLine("POST /logTestJsonException")
        @Headers("Content-type: application/json")
        void testPostJsonException(String content);

        @RequestLine("POST /logTestXmlObfuscateFieldsMsg")
        @Headers("Content-type: application/xml")
        Response testPostXmlObfuscateFieldsMsg(String content);

        @RequestLine("GET /logTestXmlObfuscateFieldsMsg")
        Response testGetXmlObfuscateFieldsMsg();
    }
    
    private TestFeignClient feignClientLosingRequests() {
        RequestDataProvider alwaysLost = Stub(RequestDataProvider)
        RequestIdProvider requestIdProvider = Mock(RequestIdProvider)
        alwaysLost.retrieve(_) >> null
        FeignCallObfuscatingLogger alwaysLostLogger = new FeignCallObfuscatingLogger(props, alwaysLost, requestIdProvider, reqResLogger);

        return Feign.builder().
                logger(alwaysLostLogger).
                logLevel(feign.Logger.Level.FULL).
                target(TestFeignClient, "http://localhost:${httpMockServer.port()}")
    }
    
    private Appender insertAppender(Appender appender) {
        root().addAppender(appender);
        return appender
    }

    private void removeAppender(Appender appender) {
        root().detachAppender(appender)
    }

    def checkInteraction(Appender appender, String pattern, int count){
        Pattern p = Pattern.compile(pattern)
        count * appender.doAppend({ ILoggingEvent e ->
            p.matcher(e.formattedMessage).matches()
        })
    }

    private Logger root() {
        return (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
    }

    private void stubGet(String path, int status, String responseBody, Map<String, String> responseHeaders){
        ResponseDefinitionBuilder response = aResponse()
        responseHeaders?.each { String key, String value -> response.withHeader(key, value)}
        stubInteraction(get(urlMatching(path)), response.withStatus(status).withBody(responseBody))
    }

    private void stubPost(String path, int status, Map<String, String> responseHeaders){
        ResponseDefinitionBuilder response = aResponse()
        responseHeaders?.each { String key, String value -> response = response.withHeader(key, value)}
        stubInteraction(post(urlMatching(path)), response.withStatus(status))
    }

    static String readResource(String resourcePath) {
        return new String(this.getClass().getResource('/' + resourcePath).getBytes()).replace('\n', '').replace('\r', '');
    }
    
    @Configuration
    @EnableObfuscatedLogging
    @EnableConfigurationProperties
    static class RequestLoggingSpecConfiguration {}
}
