package com.ofg.infrastructure.discovery;

import com.ofg.config.NotSpringCloudProfile;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Configuration that registers a bean related to microservice's address and port providing.
 *
 * @see MicroserviceAddressProvider
 */
@Configuration
@Deprecated
@NotSpringCloudProfile
public class AddressProviderConfiguration {

    private static final Logger log = LoggerFactory.getLogger(lookup().lookupClass());

    static final Integer DEFAULT_SERVER_PORT = 8080;
    @Autowired private Environment environment;

    @Bean MicroserviceAddressProvider microserviceAddressProvider(@Value("${microservice.host:#{T(com.ofg.infrastructure.discovery.AddressProviderConfiguration).resolveMicroserviceLocalhost()}}") String microserviceHost, @Value("${microservice.port:#{T(com.ofg.infrastructure.discovery.AddressProviderConfiguration).resolveMicroservicePort(@environment)}}") int microservicePort) {
        return new MicroserviceAddressProvider(microserviceHost, microservicePort);
    }

    public static String resolveMicroserviceLocalhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Exception occurred while trying to retrieve localhost", e);
            return StringUtils.EMPTY;
        }
    }

    public static Integer resolveMicroservicePort(Environment environment) {
        final String property = System.getProperty("port");
        String port = StringUtils.isNotBlank(property) ? property : environment.getProperty("server.port");
        return port != null ? Integer.valueOf(port) : DEFAULT_SERVER_PORT;
    }

}
