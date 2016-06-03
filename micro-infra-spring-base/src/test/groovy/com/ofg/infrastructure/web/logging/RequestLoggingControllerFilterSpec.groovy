package com.ofg.infrastructure.web.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.annotation.JsonValue
import com.ofg.infrastructure.base.BaseConfiguration
import com.ofg.infrastructure.base.ConfigurationWithoutServiceDiscovery
import com.ofg.infrastructure.base.MvcIntegrationSpec
import com.ofg.infrastructure.web.view.ViewConfiguration
import groovy.transform.CompileStatic
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.util.NestedServletException

import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import java.util.regex.Pattern

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = [RequestLoggingSpecConfiguration, BaseConfiguration, ConfigurationWithoutServiceDiscovery, ViewConfiguration],
        loader = SpringApplicationContextLoader)
class RequestLoggingControllerFilterSpec extends MvcIntegrationSpec {

    private static final String JSON_REQ_RESOURCE_NAME = 'requestLogging/message_req.json'

    @Autowired
    Filter createHttpControllerCallLogger

    @Override
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
        super.configureMockMvcBuilder(mockMvcBuilder)
        mockMvcBuilder.addFilter(this.createHttpControllerCallLogger)
    }

    def 'JSON: Should create log REQ/RES with all HTTP elements for POST json message'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(post("/logTestJson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(readResource(JSON_REQ_RESOURCE_NAME)))
                    .andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*POST.*url.*/logTestJson.*headers.*application/json.*body.*affiliates.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*status.*201.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES with all HTTP elements for GET json message'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(get("/logTestJson"))
                    .andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*GET.*url.*/logTestJson.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*application/json.*status.*200.*body.*affiliates.*',1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should not create log REQ/RES for GET call due to skip configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(get("/logTestJsonSkip"))
                    .andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*GET.*url.*/logTestJson.*', 0)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*application/json.*status.*200.*body.*response.*',0)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should not create log REQ/RES for GET call due to default skip configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(get("/swagger"))
                    .andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*GET.*url.*/swagger.*', 0)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*status.*body.*response.*',0)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST call due to skip configuration only for GET'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(post("/logTestJsonSkip")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(readResource(JSON_REQ_RESOURCE_NAME)))
                    .andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*POST.*url.*/logTestJson.*headers.*application/json.*body.*affiliates.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*status.*201.*', 1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST call and remove REQ/RES headers due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(post("/logTestJsonSkipHeaders")
                    .header('header-req-to-remove-1','header-req-to-remove-1-value')
                    .header('header-req-to-remove-2','header-req-to-remove-2-value')
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(readResource(JSON_REQ_RESOURCE_NAME)))
                    .andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*POST.*url.*/logTestJsonSkipHeaders.*headers.*header-req-to-remove.*', 0)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*header-res-to-remove.*status.*201.*', 0)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST call and set REMOVED on fields from REQ due to configuration'() {
        given:
        final Appender mockAppender = insertAppender(Mock(Appender))
        when:
        mockMvc.perform(post("/logTestJsonObfuscateFieldsMsg")
                .contentType(MediaType.APPLICATION_JSON)
                .content(readResource(JSON_REQ_RESOURCE_NAME)))
                .andReturn()
        then:
        interaction {
                checkInteraction(mockAppender,'.*REQ CONTROLLER->.*companyBankAccount=REMOVED.*activeFrom=REMOVED.*limit=REMOVED.*requiredAdditionalDocuments=REMOVED.*', 1)
            and:
                checkInteraction(mockAppender,'.*RES CONTROLLER<-.*', 1)
        }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for GET call and set REMOVED on fields from RES due to configuration'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(get("/logTestJsonObfuscateFieldsMsg")).andReturn()
        then:
            interaction {
                    checkInteraction(mockAppender,'.*REQ CONTROLLER->.*method.*GET.*url.*/logTestJsonObfuscateFieldsMsg.*', 1)
                and:
                    checkInteraction(mockAppender,'.*RES CONTROLLER<-headers.*application/json.*status.*200.*body.*companyBankAccount=REMOVED.*activeFrom=REMOVED.*limit=REMOVED.*requiredAdditionalDocuments=REMOVED.*',1)
            }
        cleanup:
            removeAppender(mockAppender)
    }

    def 'JSON: Should create log REQ/RES for POST finished with exception'() {
        given:
            final Appender mockAppender = insertAppender(Mock(Appender))
        when:
            mockMvc.perform(post("/logTestJsonExceptionPost")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(readResource(JSON_REQ_RESOURCE_NAME))).andReturn()
        then:
                thrown(NestedServletException)
            and:
                interaction {
                        checkInteraction(mockAppender,'.*REQ CONTROLLER->.*', 1)
                    and:
                        checkInteraction(mockAppender,'.*RES CONTROLLER<-.*', 1)
                }
        cleanup:
            removeAppender(mockAppender)
    }

    def checkInteraction(Appender appender, String pattern, int count){
        Pattern p = Pattern.compile(pattern)
        count * appender.doAppend({ILoggingEvent e ->
            p.matcher(e.formattedMessage).matches()
        })
    }

    static String readResource(String resourcePath) {
        return new String(this.getClass().getResource('/' + resourcePath).getBytes())
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

    @Configuration
    @EnableObfuscatedLogging
    static class RequestLoggingSpecConfiguration {
        @Bean
        RequestLoggingTestingController requestLoggingTestingController() {
            return new RequestLoggingTestingController()
        }
    }
}

@Controller
@CompileStatic
class RequestLoggingTestingController {

    private static final String JSON_RES_RESOURCE_NAME = 'requestLogging/message_res.json'

    @RequestMapping(value = "/logTestJson", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    ResponseEntity<String> logTestJsonPost(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<String>(HttpStatus.CREATED)
    }

    @RequestMapping(value = "/logTestJson", produces = "application/json", method = RequestMethod.GET)
    ResponseEntity<String> logTestJsonGet(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<String>(RequestLoggingControllerFilterSpec.readResource(JSON_RES_RESOURCE_NAME),HttpStatus.OK)
    }

    @RequestMapping(value = "/logTestJsonSkip", produces = "application/json", method = RequestMethod.GET)
    ResponseEntity<String> logTestJsonSkipGet(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<String>(RequestLoggingControllerFilterSpec.readResource(JSON_RES_RESOURCE_NAME),HttpStatus.OK)
    }

    @RequestMapping(value = "/swagger", produces = "application/json", method = RequestMethod.GET)
    ResponseEntity<String> logTestSwaggerGet(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<String>(RequestLoggingControllerFilterSpec.readResource(JSON_RES_RESOURCE_NAME),HttpStatus.OK)
    }

    @RequestMapping(value = "/logTestJsonSkip", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    ResponseEntity<String> logTestJsonSkipPost(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<String>(HttpStatus.CREATED)
    }

    @RequestMapping(value = "/logTestJsonSkipHeaders", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    ResponseEntity<String> logTestJsonSkipHeaders(HttpServletRequest request) {
        request.getInputStream().bytes
        HttpHeaders responseHeaders = new HttpHeaders()
        responseHeaders.set("header-res-to-remove-1", "header-res-to-remove-1-value")
        responseHeaders.set("header-res-to-remove-2", "header-res-to-remove-2-value")
        return new ResponseEntity<String>('',responseHeaders,HttpStatus.CREATED)
    }

    @RequestMapping(value = "/logTestJsonObfuscateFieldsMsg", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    ResponseEntity<String> logTestJsonObfuscateFieldsMsgPost(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<String>(HttpStatus.CREATED)
    }

    @RequestMapping(value = "/logTestJsonObfuscateFieldsMsg", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity<Json> logTestJsonObfuscateFieldsMsgGet(HttpServletRequest request) {
        request.getInputStream().bytes
        return new ResponseEntity<Json>(new Json(RequestLoggingControllerFilterSpec.readResource(JSON_RES_RESOURCE_NAME)), HttpStatus.OK)
    }

    @RequestMapping(value = "/logTestJsonExceptionPost", consumes = "application/json", produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<String> logTestJsonExceptionPost(HttpServletRequest request) {
        request.getInputStream().bytes
        throw new IllegalStateException('Error')
    }

    class Json {

        private final String value;

        public Json(String value) {
            this.value = value;
        }

        @JsonValue
        @JsonRawValue
        public String value() {
            return value;
        }
    }
}

