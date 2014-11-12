package com.ofg.infrastructure.property;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.File;
import java.util.Objects;

@Configuration
public class PropertiesConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	public static final String CONFIG_FOLDER = "CONFIG_FOLDER";
	public static final String APP_ENV = "APP_ENV";
	public static final String COUNTRY_CODE = "countryCode";

	@Bean
	public FileSystemLocator fileSystemLocator() {
		return new FileSystemLocator(
				findPropertiesFolder(),
				findEnvironment(),
				findApplicationName(),
				findCountry());
	}

	private String findCountry() {
		return Objects.requireNonNull(System.getProperty(COUNTRY_CODE), "No " + COUNTRY_CODE + " property found");
	}

	private String findApplicationName() {
		return "micro-app";
	}

	private File findPropertiesFolder() {
		final File defaultConfigDirectory = new File(System.getProperty("user.home"), "config");
		final String configFolder = getProperty(CONFIG_FOLDER, defaultConfigDirectory.getAbsolutePath());
		return new File(configFolder);
	}

	private String findEnvironment() {
		final String envOrNull = getProperty(APP_ENV, null);
		return Objects.requireNonNull(envOrNull, "No " + APP_ENV + " property found");
	}

	private static String getProperty(String name, String defValue) {
		final String valOrNull = System.getProperty(name);
		if (valOrNull == null) {
			final String envValueOrNull = System.getenv(name);
			return envValueOrNull != null ? envValueOrNull : defValue;
		} else {
			return valOrNull;
		}
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
