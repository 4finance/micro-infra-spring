package com.ofg.infrastructure.base

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static com.ofg.config.BasicProfiles.TEST

/**
 * Base specification class for Spring's web application
 */
@WebAppConfiguration
@ActiveProfiles(TEST)
abstract class IntegrationSpec extends Specification {
}
