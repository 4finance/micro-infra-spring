package com.ofg.infrastructure.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("hello")
public interface JaxRsApi {
    String HELLO = "Hello JAX-RS";

    @GET
    @Path("get")
    String getHello();
}
