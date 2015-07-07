package com.ofg.infrastructure.discovery;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * Alias to service as used e.g. in {@code ServiceRestClient}
 * You can translate from alias to path using {@link ServiceResolver#resolveAlias(ServiceAlias)}.
 */
public class ServiceAlias {
    private final String name;

    public ServiceAlias(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
