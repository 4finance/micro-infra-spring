package io.fourfinance.activity_tracker.autoconfigure;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.fourfinance.activity_tracker.testapp.TestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(value = TestApplication.class, initializers = ConfigFileApplicationContextInitializer.class)
public class ActivityTrackerAutoConfigurationEnabledTest {

    @Autowired
    ApplicationContext context;
    
    @Test
    public void shouldLoadUserActivityTrackingBeans() throws Exception {
        //when
        boolean trackUserActivityAspectPresent = context.containsBean("trackUserActivityAspect");

        //then
        then(trackUserActivityAspectPresent).isTrue();
    }
}