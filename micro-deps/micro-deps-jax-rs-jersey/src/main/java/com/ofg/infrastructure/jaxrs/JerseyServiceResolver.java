package com.ofg.infrastructure.jaxrs;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.ofg.infrastructure.discovery.ServiceAlias;
import com.ofg.infrastructure.discovery.ServicePath;
import com.ofg.infrastructure.discovery.ServiceResolver;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
        return getLocator(clazz, new ServicePath(clazz.getPackage().getName().replace('.', '/')));
    }

    @Override
    public <T> Supplier<T> getLocator(final Class<T> clazz, final ServiceAlias alias) {
        return getLocator(clazz, resolver.resolveAlias(alias));
    }

    public Client getClient() {
        return ClientBuilder.newClient(config);
    }

    private <T> Supplier<T> getLocator(final Class<T> clazz, final ServicePath path) {
        final Client client = getClient();
        return new Supplier<T>() {
            @Override
            public T get() {
                return resolveResource(resolver.getUri(path), client, clazz);
            }
        };
    }

    private static <T> T resolveResource(Optional<URI> uri, Client client, Class<T> clazz) {
        return WebResourceFactory.newResource(clazz, client.target(uri.get()));
    }
}
