package io.fourfinance.activity_tracker.autoconfigure;

import com.codahale.metrics.MetricRegistry;
import io.fourfinance.activity_tracker.activity.ActivityParameters;
import io.fourfinance.activity_tracker.activity.DefaultTrackUserActivityMetrics;
import io.fourfinance.activity_tracker.activity.TrackUserActivityAspect;
import io.fourfinance.activity_tracker.activity.TrackUserActivityMetrics;
import io.fourfinance.activity_tracker.audit.DefaultTrackUserActivityAudits;
import io.fourfinance.activity_tracker.audit.TrackUserActivityAudits;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.fourfinance.activity_tracker.activity.ActivityParameters.emptyActivityParameters;

@Configuration
@ConditionalOnExpression("${com.ofg.infra.microservice.track-activity.enabled:true}")
public class ActivityTrackerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public TrackUserActivityAudits userAuditRepository() {
		return new DefaultTrackUserActivityAudits();
	}

	@Bean
	@ConditionalOnMissingBean
	public MetricRegistry metricRegistry() {
		return new MetricRegistry();
	}

	@Bean
	@ConditionalOnMissingBean
	public TrackUserActivityMetrics userActivityMetrics() {
		return new DefaultTrackUserActivityMetrics(metricRegistry());
	}

	@Bean
	@ConditionalOnMissingBean
	public ActivityParameters activityParameters() {
		return emptyActivityParameters();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TrackUserActivityAspect trackUserActivityAspect(TrackUserActivityAudits auditRepository, 
														   TrackUserActivityMetrics metrics, 
														   ActivityParameters activityParameters) {
		return new TrackUserActivityAspect(auditRepository, metrics, activityParameters);
	}

}
