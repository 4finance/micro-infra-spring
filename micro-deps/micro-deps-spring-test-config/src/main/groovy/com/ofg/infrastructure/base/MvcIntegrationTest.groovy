package com.ofg.infrastructure.base

import groovy.transform.CompileStatic
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static com.ofg.config.BasicProfiles.TEST

/**
 * Base for specifications that use Spring's {@link MockMvc}. Provides also {@link WebApplicationContext}, 
 * {@link ApplicationContext}. The latter you can use to specify what
 * kind of address should be returned for a given dependency name. 
 * 
 * @see WebApplicationContext
 * @see ApplicationContext
 */
@CompileStatic
@RunWith(SpringJUnit4ClassRunner)
@WebAppConfiguration
@ActiveProfiles(TEST)
abstract class MvcIntegrationTest extends IntegrationTest {

    @Autowired protected WebApplicationContext webApplicationContext
    @Autowired protected ApplicationContext applicationContlext

    protected MockMvc mockMvc

    @Before
    void setup() {
        ConfigurableMockMvcBuilder mockMvcBuilder = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        configureMockMvcBuilder(mockMvcBuilder)
        mockMvc = mockMvcBuilder.build()
    }

    /**
     * Override in a subclass to modify mockMvcBuilder configuration (e.g. add filter).
     *
     * The method from super class should be called.
     */
    protected void configureMockMvcBuilder(ConfigurableMockMvcBuilder mockMvcBuilder) {
    }
}
