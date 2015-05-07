package com.ofg.infrastructure.discovery;

import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MicroserviceConfiguration {
    private final ServicePath servicePath;
    private final List<Dependency> dependencies;

    public MicroserviceConfiguration(ServicePath servicePath, List<Dependency> dependencies) {
        this.servicePath = servicePath;
        this.dependencies = dependencies;
    }

    public MicroserviceConfiguration(ServicePath servicePath) {
        this.servicePath = servicePath;
        this.dependencies = new ArrayList<>();
    }

    public Dependency getDependencyForName(String serviceName) {
        return findDependencyWithName(serviceName);
    }

    private Dependency findDependencyWithName(String serviceName) {
        for (Iterator<Dependency> iter = dependencies.iterator(); iter.hasNext(); ) {
            Dependency item = iter.next();
            if (item.getServiceAlias().getName().equals(serviceName)) {
                return item;
            }
        }
        return null;
    }

    public ServicePath getServicePath() {
        return servicePath;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public static class Dependency {

        private final ServiceAlias serviceAlias;
        private final ServicePath servicePath;
        private final boolean required;
        private final LoadBalancerType loadBalancerType;
        private final String contentTypeTemplate;
        private final String version;
        private final Map<String, String> headers;

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath, boolean required, LoadBalancerType loadBalancerType, String contentTypeTemplate, String version, Map<String, String> headers) {
            this.serviceAlias = serviceAlias;
            this.servicePath = servicePath;
            this.required = required;
            this.loadBalancerType = loadBalancerType;
            this.contentTypeTemplate = contentTypeTemplate;
            this.version = version;
            this.headers = headers;
        }

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath) {
            this.serviceAlias = serviceAlias;
            this.servicePath = servicePath;
            this.required = false;
            this.loadBalancerType = LoadBalancerType.ROUND_ROBIN;
            this.contentTypeTemplate = StringUtils.EMPTY;
            this.version = StringUtils.EMPTY;
            this.headers = new HashMap<>();
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }

        public ServiceAlias getServiceAlias() {
            return serviceAlias;
        }

        public ServicePath getServicePath() {
            return servicePath;
        }

        public boolean getRequired() {
            return required;
        }

        public boolean isRequired() {
            return required;
        }

        public LoadBalancerType getLoadBalancerType() {
            return loadBalancerType;
        }

        public String getContentTypeTemplate() {
            return contentTypeTemplate;
        }

        public String getVersion() {
            return version;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

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
