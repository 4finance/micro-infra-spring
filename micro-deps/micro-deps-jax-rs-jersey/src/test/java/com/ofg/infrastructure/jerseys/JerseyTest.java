package com.ofg.infrastructure.jerseys;

import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.discovery.util.MicroDepsService;
import com.ofg.infrastructure.jaxrs.JaxRsApi;
import com.ofg.infrastructure.jaxrs.JaxRsServiceResolver;
import com.ofg.infrastructure.jaxrs.JerseyServiceResolver;
import org.apache.curator.test.TestingServer;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({JerseyApplication.class})
@WebIntegrationTest(randomPort = true)
public class JerseyTest {
    private static final String CONFIG = "{\"ctx\": {\"this\": \"com/ofg/infrastructure/jaxrs\"}}";
    private static final Logger log = LoggerFactory.getLogger(JerseyTest.class);

    @Value("${local.server.port}")
    private int port;

    /**
     * Validates server is up to quickly distinguish between cases when it is not working and jersey issues.
     */
    @Test
    public void restTemplate() {
        String url = "http://localhost:" + port + "/ctx/hello/get";
        log.info("URL=[{}]", url);
        ResponseEntity<String> response = new TestRestTemplate().getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(JaxRsApi.HELLO, response.getBody());
    }

    @Test
    public void jersey() throws Exception {
        TestingServer server = new TestingServer();
        MicroDepsService service = new MicroDepsService(server.getConnectString(), "ctx",  "localhost", port, CONFIG).start();
        ServiceResolver sr = service.getServiceResolver();
        JaxRsServiceResolver subj = new JerseyServiceResolver(sr, new ClientConfig());
        JaxRsApi resource = subj.getLocator(JaxRsApi.class).get();
        Assert.assertEquals(JaxRsApi.HELLO, resource.getHello());
    }

}
