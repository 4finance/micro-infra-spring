package com.ofg.infrastructure.environment;

import com.google.common.base.Joiner;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Spring {@link ApplicationListener} that verifies that you have provided a spring profile upon
 * application execution (via spring.profiles.active system property). If it's not provided the
 * application will close with error.
 *
 * @see ApplicationListener
 */
public class EnvironmentSetupVerifier implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private final List<String> allPossibleSpringProfiles;

    public EnvironmentSetupVerifier(List<String> allPossibleSpringProfiles) {
        this.allPossibleSpringProfiles = allPossibleSpringProfiles;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        String[] activeProfiles = event.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 0 || !allPossibleSpringProfiles.containsAll(asList(activeProfiles))) {
            System.out.println(new StringBuilder().
                    append("This app requires an explicit profile").append("\n").
                    append("Please setup a profile in environment variable 'spring.profiles.active'").append("\n").
                    append("or pass -Dspring.profiles.active=NAME_OF_PROFILE as a JVM param").append("\n").
                    append("Possible profiles: ").append(Joiner.on(", ").join(allPossibleSpringProfiles)));
            System.exit(1);
        }

        System.out.println(new StringBuilder().
                append("Application is run with these active profiles: ").
                append(Joiner.on(", ").join(activeProfiles))
        );
    }
}
