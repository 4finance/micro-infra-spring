package com.ofg.infrastructure.base

import groovy.transform.CompileStatic
import org.junit.runner.RunWith
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration

import static com.ofg.config.BasicProfiles.TEST

/**
 * Base JUnit test class for Spring's web application
 */
@CompileStatic
@RunWith(SpringRunner)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@WebAppConfiguration
@ActiveProfiles(TEST)
abstract class IntegrationTest {
}
