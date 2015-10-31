package com.ofg.infrastructure.discovery.config

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@CompileStatic
class PropertySourceConfiguration {
	private static String zookeeperConnectString = "localhost:2181"

	@Bean
	static PropertySourcesPlaceholderConfigurer propertiesConfigurer() {
		Properties properties = new Properties()
		properties.setProperty('stubrunner.stubs.repository.root', 'http://dl.bintray.com/4finance/micro')
		properties.setProperty('stubrunner.stubs.group', 'com.ofg')
		properties.setProperty('stubrunner.stubs.module', 'stub-runner-examples')
		properties.setProperty('microservice.config.file', 'classpath:stub-microservice.json')
		properties.setProperty('service.resolver.url', zookeeperConnectString)
		return new PropertySourcesPlaceholderConfigurer(properties: properties)
	}

	static def setZookeeperConnectString(String zookeeperConnectString) {
		this.zookeeperConnectString = zookeeperConnectString
	}
}
