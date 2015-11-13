package com.ofg.infrastructure.discovery;

import com.google.common.base.Joiner;
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
        private final StubsConfiguration stubs;

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath, boolean required,
                          LoadBalancerType loadBalancerType, String contentTypeTemplate, String version, Map<String, String> headers) {
            this.serviceAlias = serviceAlias;
            this.servicePath = servicePath;
            this.required = required;
            this.loadBalancerType = loadBalancerType;
            this.contentTypeTemplate = contentTypeTemplate;
            this.version = version;
            this.headers = ImmutableMap.copyOf(headers);
            this.stubs = new StubsConfiguration(servicePath);
        }

        public Dependency(ServiceAlias serviceAlias, ServicePath servicePath, boolean required, LoadBalancerType
                loadBalancerType, String contentTypeTemplate, String version, Map<String, String> headers, StubsConfiguration stubsConfiguration) {
            this.serviceAlias = serviceAlias;
            this.servicePath = servicePath;
            this.required = required;
            this.loadBalancerType = loadBalancerType;
            this.contentTypeTemplate = contentTypeTemplate;
            this.version = version;
            this.headers = ImmutableMap.copyOf(headers);
            this.stubs = MoreObjects.firstNonNull(stubsConfiguration, new StubsConfiguration(servicePath));
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

        public StubsConfiguration getStubs() {
            return stubs;
        }

        public static class StubsConfiguration {
            private static final String DEFAULT_STUBS_CLASSIFIER = "stubs";

            private final String stubsGroupId;
            private final String stubsArtifactId;
            private final String stubsClassifier;

            public StubsConfiguration(String stubsGroupId, String stubsArtifactId, String stubsClassifier) {
                this.stubsGroupId = stubsGroupId;
                this.stubsArtifactId = stubsArtifactId;
                this.stubsClassifier = StringUtils.defaultIfEmpty(stubsClassifier, DEFAULT_STUBS_CLASSIFIER);
            }

            public StubsConfiguration(ServicePath servicePath) {
                this.stubsGroupId = pathToLastNameWithoutStartingSlash(servicePath);
                this.stubsArtifactId = servicePath.getLastName();
                this.stubsClassifier = DEFAULT_STUBS_CLASSIFIER;
            }

            private String pathToLastNameWithoutStartingSlash(ServicePath servicePath) {
                if (servicePath.getPathToLastName().startsWith("/")) {
                    return replaceAllSlashesWithDots(servicePath).substring(1);
                }
                return replaceAllSlashesWithDots(servicePath);
            }

            private String replaceAllSlashesWithDots(ServicePath servicePath) {
                return servicePath.getPathToLastName().replaceAll("/", ".");
            }

            public String getStubsGroupId() {
                return stubsGroupId;
            }

            public String getStubsArtifactId() {
                return stubsArtifactId;
            }

            public String getStubsClassifier() {
                return stubsClassifier;
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

            public String toColonSeparatedDependencyNotation() {
                return Joiner.on(":").join(getStubsGroupId(), getStubsArtifactId(), getStubsClassifier());
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
