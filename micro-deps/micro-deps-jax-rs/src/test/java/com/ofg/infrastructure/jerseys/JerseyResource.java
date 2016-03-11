package com.ofg.infrastructure.jerseys;

import com.ofg.infrastructure.jaxrs.JaxRsApi;
import org.springframework.stereotype.Controller;

@Controller
public class JerseyResource implements JaxRsApi {

    @Override
    public String getHello() {
        return JaxRsApi.HELLO;
    }
}
