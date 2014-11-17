package com.ofg.infrastructure.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.io.File;

@Configuration
public class PropertiesConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	@Autowired
	private TextEncryptor textEncryptor;

	@Bean
	public FileSystemLocator fileSystemLocator() {
		return new FileSystemLocator(
				findPropertiesFolder(),
				appCoordinates(),
				textEncryptor);
	}

	@Bean
	public AppCoordinates appCoordinates() {
		return new AppCoordinates();
	}

	private File findPropertiesFolder() {
		final File defaultConfigDirectory = new File(System.getProperty("user.home"), "config");
		final String configFolder = PropertyUtils.getProperty(AppCoordinates.CONFIG_FOLDER, defaultConfigDirectory.getAbsolutePath());
		return new File(configFolder);
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
