package io.fourfinance.activity_tracker.audit;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fourfinance.activity_tracker.activity.TrackUserActivityAspect;

public class DefaultTrackUserActivityAudits implements TrackUserActivityAudits {

    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void audit(Map<String, String> parameters) {
        LOG.info("Activity {}", parameters.get(TrackUserActivityAspect.ACTIVITY_NAME));
    }
}
