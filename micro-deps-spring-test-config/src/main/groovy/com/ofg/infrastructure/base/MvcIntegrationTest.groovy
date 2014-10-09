package com.ofg.infrastructure.base
import com.ofg.infrastructure.discovery.StubbedServiceResolver
import groovy.transform.CompileStatic
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static com.ofg.config.BasicProfiles.TEST
/**
 * Base for specifications that use Spring's {@link MockMvc}. Provides also {@link WebApplicationContext}, 
 * {@link ApplicationContext} and {@link StubbedServiceResolver}. The latter you can use to specify what
 * kind of address should be returned for a given dependency name. 
 * 
 * @see WebApplicationContext
 * @see ApplicationContext
 * @see StubbedServiceResolver
 */
@CompileStatic
@RunWith(SpringJUnit4ClassRunner)
@WebAppConfiguration
@ActiveProfiles(TEST)
abstract class MvcIntegrationTest extends IntegrationTest {
    
    @Autowired WebApplicationContext webApplicationContext
    @Autowired ApplicationContext applicationContext
    @Autowired StubbedServiceResolver stubbedServiceResolver
    protected MockMvc mockMvc

    @Before
    void setup() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext).
                build()
    }

}