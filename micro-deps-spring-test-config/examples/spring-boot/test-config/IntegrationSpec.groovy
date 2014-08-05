package com.ofg.base

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

import static com.ofg.microservice.config.Profiles.TEST

@ContextConfiguration(classes = [ServiceDiscoveryStubbingApplicationConfiguration], loader = SpringApplicationContextLoader)
@WebAppConfiguration
@ActiveProfiles([TEST])
abstract class IntegrationSpec extends Specification {
}
