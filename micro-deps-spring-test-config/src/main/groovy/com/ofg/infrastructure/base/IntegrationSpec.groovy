package com.ofg.infrastructure.base

import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
abstract class IntegrationSpec extends Specification {
}
