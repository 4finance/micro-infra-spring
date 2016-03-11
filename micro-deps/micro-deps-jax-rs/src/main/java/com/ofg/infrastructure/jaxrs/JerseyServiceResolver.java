package com.ofg.infrastructure.jaxrs;

import com.google.common.base.Supplier;
import com.ofg.infrastructure.discovery.ServicePath;
import com.ofg.infrastructure.discovery.ServiceResolver;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

@Named
public class JerseyServiceResolver implements JaxRsServiceResolver {
    private final ServiceResolver resolver;
    private final ClientConfig config;

    @Inject
    public JerseyServiceResolver(ServiceResolver resolver, ClientConfig config) {
        this.config = config;
        this.resolver = resolver;
    }

    @Override
    public <T> Supplier<T> getLocator(final Class<T> clazz) {
        final ServicePath path = new ServicePath(clazz.getPackage().getName().replace('.', '/'));
        final Client client = getClient();
        return new Supplier<T>() {
            @Override
            public T get() {
                URI uri = resolver.getUri(path).get();
                WebTarget t = client.target(uri);
                return WebResourceFactory.newResource(clazz, t);
            }
        };
    }

    public Client getClient() {
        return ClientBuilder.newClient(config);
    }
}
