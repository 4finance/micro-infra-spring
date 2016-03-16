package com.ofg.infrastructure.jerseys;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("ctx")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(JerseyResource.class);
    }
}
