package io.fourfinance.activity_tracker.autoconfigure;

import io.fourfinance.activity_tracker.testapp.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
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
