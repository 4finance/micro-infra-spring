package io.fourfinance.activity_tracker.audit;

import java.util.Map;

public interface TrackUserActivityAudits {

    void audit(Map<String, String> parameters);
}
