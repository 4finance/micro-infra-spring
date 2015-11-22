package com.ofg.config

import com.ofg.infrastructure.discovery.DiscoveryPropertiesEnabler
import org.springframework.boot.test.EnvironmentTestUtils
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.util.StringUtils
import spock.lang.Specification
import spock.lang.Unroll

import static com.ofg.infrastructure.discovery.DiscoveryPropertiesEnabler.SPRING_AUTOCONFIGURE_EXCLUDE
import static com.ofg.infrastructure.discovery.DiscoveryPropertiesEnabler.SPRING_RIBBON_ZOOKEEPER_ENABLED
import static com.ofg.infrastructure.discovery.DiscoveryPropertiesEnabler.SPRING_ZOOKEEPER_DISCOVERY_ENABLED
import static com.ofg.infrastructure.discovery.DiscoveryPropertiesEnabler.SPRING_ZOOKEEPER_ENABLED

class DiscoveryPropertiesEnablerSpec extends Specification {

    public static final String EXCLUDE_AUTOCONFIG = 'something'

    public static Closure<Void> FALSE_DEFAULT_SPRING_PROPS = { ConfigurableEnvironment environment ->
        assert !Boolean.valueOf(environment.getProperty(SPRING_ZOOKEEPER_ENABLED))
        assert !Boolean.valueOf(environment.getProperty(SPRING_RIBBON_ZOOKEEPER_ENABLED))
        assert !Boolean.valueOf(environment.getProperty(SPRING_ZOOKEEPER_DISCOVERY_ENABLED))
    }

    public static Closure<Boolean> NON_SPRING_CLOUD_COMPARATOR = { ConfigurableEnvironment environment ->
        DiscoveryPropertiesEnablerSpec.FALSE_DEFAULT_SPRING_PROPS(environment)
        assert StringUtils.hasText(environment.getProperty(SPRING_AUTOCONFIGURE_EXCLUDE))
        return true
    }

    public static Closure<Boolean> SPRING_CLOUD_DEFAULTS_COMPARATOR = { ConfigurableEnvironment environment ->
        DiscoveryPropertiesEnablerSpec.FALSE_DEFAULT_SPRING_PROPS(environment)
        assert !StringUtils.hasText(environment.getProperty(SPRING_AUTOCONFIGURE_EXCLUDE))
        return true
    }

    public static Closure<Boolean> CUSTOM_PROPS_COMPARATOR = { ConfigurableEnvironment environment ->
        assert Boolean.valueOf(environment.getProperty(SPRING_ZOOKEEPER_ENABLED))
        assert Boolean.valueOf(environment.getProperty(SPRING_RIBBON_ZOOKEEPER_ENABLED))
        assert Boolean.valueOf(environment.getProperty(SPRING_ZOOKEEPER_DISCOVERY_ENABLED))
        assert environment.getProperty(SPRING_AUTOCONFIGURE_EXCLUDE) == EXCLUDE_AUTOCONFIG
        return true
    }

    @Unroll
    def 'should resolve spring cloud zookeeper props with profile [#profile] to default disabled values'() {
        given:
            ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(DiscoveryPropertiesEnabler)
            context.environment.setActiveProfiles(profile)
        expect:
            comparator(context.environment)
        where:
            profile                                                            || comparator
            BasicProfiles.DEVELOPMENT                                          || NON_SPRING_CLOUD_COMPARATOR
            BasicProfiles.PRODUCTION                                           || NON_SPRING_CLOUD_COMPARATOR
            BasicProfiles.SPRING_CLOUD                                         || SPRING_CLOUD_DEFAULTS_COMPARATOR
            [BasicProfiles.PRODUCTION, BasicProfiles.SPRING_CLOUD] as String[] || SPRING_CLOUD_DEFAULTS_COMPARATOR
    }

    @Unroll
    def 'should take passed properties as the most important ones with profile [#profile]'() {
        given:
            ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(DiscoveryPropertiesEnabler)
            context.environment.setActiveProfiles(profile)
            String pairs = "$SPRING_ZOOKEEPER_ENABLED=true;$SPRING_RIBBON_ZOOKEEPER_ENABLED=true;$SPRING_ZOOKEEPER_DISCOVERY_ENABLED=true;$SPRING_AUTOCONFIGURE_EXCLUDE=$EXCLUDE_AUTOCONFIG"
            EnvironmentTestUtils.addEnvironment(context.environment, pairs.split(';'))
        expect:
            comparator(context.environment)
        where:
            profile                                                            || comparator
            BasicProfiles.DEVELOPMENT                                          || CUSTOM_PROPS_COMPARATOR
            [BasicProfiles.PRODUCTION, BasicProfiles.SPRING_CLOUD] as String[] || CUSTOM_PROPS_COMPARATOR
            BasicProfiles.SPRING_CLOUD                                         || CUSTOM_PROPS_COMPARATOR
    }

}
