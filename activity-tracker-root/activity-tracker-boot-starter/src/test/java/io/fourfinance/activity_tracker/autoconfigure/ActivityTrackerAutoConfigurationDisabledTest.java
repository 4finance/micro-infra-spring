package io.fourfinance.activity_tracker.autoconfigure;

import io.fourfinance.activity_tracker.testapp.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "com.ofg.infra.microservice.track-activity.enabled = false", webEnvironment = NONE, classes = TestApplication.class)
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
