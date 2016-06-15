package io.fourfinance.activity_tracker.activity;

import io.fourfinance.activity_tracker.audit.TrackUserActivityAudits;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

@Aspect
public class TrackUserActivityAspect {

    public static final String ACTIVITY_NAME = "activity";
    private final ActivityParameters activityParameters;
    private final TrackUserActivityAudits trackUserActivityAudits;
    private final TrackUserActivityMetrics trackTrackUserActivityMetrics;

    public TrackUserActivityAspect(TrackUserActivityAudits trackUserActivityAudits, TrackUserActivityMetrics trackTrackUserActivityMetrics,
                                   ActivityParameters activityParameters) {
        this.trackUserActivityAudits = trackUserActivityAudits;
        this.trackTrackUserActivityMetrics = trackTrackUserActivityMetrics;
        this.activityParameters = activityParameters;
    }

    @Before("@annotation(io.fourfinance.activity_tracker.activity.TrackUserActivity)")
    public void audit(JoinPoint joinPoint) {
        Map<String, String> values = new HashMap<>();
        values.put(ACTIVITY_NAME, readActivityName(joinPoint));
        for (String key : activityParameters.getParameters()) {
            values.put(key, extractParamValue(joinPoint, key));
        }
        trackUserActivityAudits.audit(values);
    }

    @AfterReturning("@annotation(io.fourfinance.activity_tracker.activity.TrackUserActivity)")
    void processMetrics(JoinPoint joinPoint) {
        String activityName = readActivityName(joinPoint);
        trackTrackUserActivityMetrics.process(activityName);
    }

    private String readActivityName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        TrackUserActivity annotation = signature.getMethod().getAnnotation(TrackUserActivity.class);
        if (annotation != null) {
            return annotation.value();
        }
        return signature.getName();
    }

    private String extractParamValue(JoinPoint joinPoint, final String field) {
        return new JoinPointParameters(joinPoint).getValue(field).or("<not available>").toString();
    }
}
