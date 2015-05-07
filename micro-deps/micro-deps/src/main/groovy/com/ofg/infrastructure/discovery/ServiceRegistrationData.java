package com.ofg.infrastructure.discovery;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class ServiceRegistrationData {
    private final String realm;
    private final MicroserviceConfiguration microserviceConfiguration;

    public ServiceRegistrationData(String realm, MicroserviceConfiguration microserviceConfiguration) {
        this.realm = realm;
        this.microserviceConfiguration = microserviceConfiguration;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
