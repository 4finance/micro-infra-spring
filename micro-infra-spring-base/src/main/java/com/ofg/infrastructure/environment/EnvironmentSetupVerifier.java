package com.ofg.infrastructure.environment;

import groovy.transform.CompileStatic;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * Spring {@link ApplicationListener} that verifies that you have provided a spring profile upon
 * application execution (via spring.profiles.active system property). If it's not provided the
 * application will close with error.
 *
 * @see ApplicationListener
 */
public class EnvironmentSetupVerifier implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public EnvironmentSetupVerifier(List<String> allPossibleSpringProfiles) {
        this.allPossibleSpringProfiles = allPossibleSpringProfiles;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        String[] activeProfiles = event.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 0 || !allPossibleSpringProfiles.containsAll(DefaultGroovyMethods.toList(activeProfiles))) {
            DefaultGroovyMethods.println(this, StringGroovyMethods.stripIndent("\\n            This app requires an explicit profile\n            Please setup a profile in environment variable 'spring.profiles.active'\n            or pass -Dspring.profiles.active=NAME_OF_PROFILE as a JVM param\n            Possible profiles: " + DefaultGroovyMethods.join(allPossibleSpringProfiles, ", ")));
            System.exit(1);
        }

        DefaultGroovyMethods.println(this, "Application is run with these active profiles: " + DefaultGroovyMethods.join(activeProfiles, ", "));
    }

    private final List<String> allPossibleSpringProfiles;
}
