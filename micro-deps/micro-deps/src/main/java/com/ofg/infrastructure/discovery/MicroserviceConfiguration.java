package com.ofg.infrastructure.discovery;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ofg.infrastructure.discovery.util.LoadBalancerType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ofg.infrastructure.discovery.util.CollectionUtils.find;
import static com.ofg.infrastructure.discovery.util.LoadBalancerType.ROUND_ROBIN;

public class MicroserviceConfiguration {
    private final ServicePath servicePath;
    private final List<Dependency> dependencies;

    public MicroserviceConfiguration(ServicePath servicePath, List<Dependency> dependencies) {
        this.servicePath = servicePath;
        this.dependencies = ImmutableList.copyOf(dependencies);
    }

    public MicroserviceConfiguration(ServicePath servicePath) {
        this(servicePath, Collections.<Dependency>emptyList());
    }

    /**
     *
     * @deprecated since 0.9.1, use {@link #getDependency(ServiceAlias serviceAlias)} instead
     */
    @Deprecated
    public Dependency getDependencyForName(String serviceName) {
        return getDependency(new ServiceAlias(serviceName));
    }

    public Dependency getDependency(final ServiceAlias serviceAlias) {
        return find(dependencies, new Predicate<Dependency>() {
            @Override
            public boolean apply(Dependency dependency) {
                return dependency.getServiceAlias().equals(serviceAlias);
            }
        });
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
        private final StubConfiguration stubs;

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath, boolean required,
                          LoadBalancerType loadBalancerType, String contentTypeTemplate, String version, Map<String, String> headers) {
            this.serviceAlias = serviceAlias;
            this.servicePath = servicePath;
            this.required = required;
            this.loadBalancerType = loadBalancerType;
            this.contentTypeTemplate = contentTypeTemplate;
            this.version = version;
            this.headers = ImmutableMap.copyOf(headers);
            this.stubs = new StubConfiguration(servicePath);
        }

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath, boolean required, LoadBalancerType
                loadBalancerType, String contentTypeTemplate, String version, Map<String, String> headers, StubConfiguration stubConfiguration) {
            this.serviceAlias = serviceAlias;
            this.servicePath = servicePath;
            this.required = required;
            this.loadBalancerType = loadBalancerType;
            this.contentTypeTemplate = contentTypeTemplate;
            this.version = version;
            this.headers = ImmutableMap.copyOf(headers);
            this.stubs = MoreObjects.firstNonNull(stubConfiguration, new StubConfiguration(servicePath));
        }

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath) {
            this(serviceAlias, servicePath, false, ROUND_ROBIN, "", "", Collections.<String, String>emptyMap());
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

        public StubConfiguration getStubs() {
            return stubs;
        }

        public static class StubConfiguration {
            private static final String DEFAULT_STUB_SUFFIX = "stubs";

            private final String stubGroupId;
            private final String stubArtifactId;
            private final String stubClassifier;

            public StubConfiguration(String stubGroupId, String stubArtifactId) {
                this.stubGroupId = stubGroupId;
                this.stubArtifactId = stubArtifactId;
                this.stubClassifier = DEFAULT_STUB_SUFFIX;
            }

            public StubConfiguration(String stubGroupId, String stubArtifactId, String stubClassifier) {
                this.stubGroupId = stubGroupId;
                this.stubArtifactId = stubArtifactId;
                this.stubClassifier = StringUtils.defaultIfEmpty(stubClassifier, DEFAULT_STUB_SUFFIX);
            }

            public StubConfiguration(ServicePath servicePath) {
                this.stubGroupId = servicePath.getPathToLastName().replaceAll("/", ".");
                this.stubArtifactId = servicePath.getLastName();
                this.stubClassifier = DEFAULT_STUB_SUFFIX;
            }

            public String getStubGroupId() {
                return stubGroupId;
            }

            public String getStubArtifactId() {
                return stubArtifactId;
            }

            public String getStubClassifier() {
                return stubClassifier;
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
