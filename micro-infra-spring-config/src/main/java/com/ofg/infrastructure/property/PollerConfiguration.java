package com.ofg.infrastructure.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@Profile("!test")
public class PollerConfiguration {

	@Autowired
	private ConfigurableEnvironment environment;

	@Autowired
	private RefreshScope refreshScope;

	@Autowired FileSystemLocator fileSystemLocator;

	@Bean
	public FileSystemPoller fileSystemPoller() {
		return new FileSystemPoller(fileSystemLocator, environment, refreshScope);
	}
}
