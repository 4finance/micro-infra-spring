package com.ofg.infrastructure.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.client.PropertySourceLocator;
import org.springframework.cloud.config.server.SpringApplicationEnvironmentRepository;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class FileSystemLocator implements PropertySourceLocator {

	private static final Logger log = LoggerFactory.getLogger(FileSystemLocator.class);

	private final File propertiesFolder;
	private final String environment;
	private final String applicationName;
	private final String countryCode;

	public FileSystemLocator(File propertiesFolder, String environment, String applicationName, String countryCode) {
		this.propertiesFolder = propertiesFolder;
		this.environment = environment;
		this.applicationName = applicationName;
		this.countryCode = countryCode;
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		final SpringApplicationEnvironmentRepository springEnv = new SpringApplicationEnvironmentRepository();
		final String[] propertiesPath = getConfigFiles();
		log.debug("Loading configuration from {}", Arrays.toString(propertiesPath));
		springEnv.setSearchLocations(propertiesPath);
		final org.springframework.cloud.config.Environment loadedEnvs = springEnv.findOne(applicationName, "prod", null);

		CompositePropertySource composite = new CompositePropertySource(FileSystemLocator.class.getSimpleName());
		for (org.springframework.cloud.config.PropertySource source : loadedEnvs.getPropertySources()) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = decrypt((Map<String, Object>) source.getSource());
			composite.addPropertySource(new MapPropertySource(source.getName(), map));
		}
		return composite;
	}

	private String[] getConfigFiles() {
		return new String[] {
				configFile(".properties"),
				configFile(".yaml"),
				configFile("-" + countryCode + ".properties"),
				configFile("-" + countryCode + ".yaml")
		};
	}

	private String configFile(String suffix) {
		return new File(getConfigPath(), applicationName + suffix).getAbsolutePath();
	}

	public File getConfigPath() {
		final File envFolder = new File(propertiesFolder, environment);
		final File appFolder = new File(envFolder, applicationName);
		return appFolder.getAbsoluteFile();
	}

	private Map<String, Object> decrypt(Map<String, Object> sourceMap) {
		return sourceMap;
	}
}
