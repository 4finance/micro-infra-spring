package io.fourfinance.activity_tracker.autoconfigure;

import io.fourfinance.activity_tracker.testapp.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(value = TestApplication.class, initializers = ConfigFileApplicationContextInitializer.class)
@IntegrationTest("com.ofg.infra.microservice.track-activity.enabled = false")
public class ActivityTrackerAutoConfigurationDisabledTest {

    @Autowired
    ApplicationContext context;
    
    @Test 
    public void shouldNotLoadUserActivityTrackingBeans() throws Exception {
        //when
        boolean trackUserActivityAspectPresent = context.containsBean("trackUserActivityAspect");

        //then
        then(trackUserActivityAspectPresent).isFalse();
    }
}
