package com.ofg.infrastructure.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.client.PropertySourceLocator;
import org.springframework.cloud.config.server.SpringApplicationEnvironmentRepository;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileSystemLocator implements PropertySourceLocator {

	private static final Logger log = LoggerFactory.getLogger(FileSystemLocator.class);
	public static final String CIPHER_PREFIX = "{cipher}";

	private final File propertiesFolder;
	private final AppCoordinates appCoordinates;
	private final TextEncryptor encryptor;

	public FileSystemLocator(File propertiesFolder, AppCoordinates appCoordinates, TextEncryptor encryptor) {
		this.propertiesFolder = propertiesFolder;
		this.appCoordinates = appCoordinates;
		this.encryptor = encryptor;
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		final SpringApplicationEnvironmentRepository springEnv = new SpringApplicationEnvironmentRepository();
		final String[] propertiesPath = getConfigFiles();
		log.debug("Loading configuration from {}", Arrays.toString(propertiesPath));
		springEnv.setSearchLocations(propertiesPath);
		final org.springframework.cloud.config.Environment loadedEnvs = springEnv.findOne(appCoordinates.getApplicationName(), "prod", null);

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
				configFile("-" + appCoordinates.getCountryCode() + ".properties"),
				configFile("-" + appCoordinates.getCountryCode() + ".yaml")
		};
	}

	private String configFile(String suffix) {
		return new File(getConfigPath(), appCoordinates.getApplicationName() + suffix).getAbsolutePath();
	}

	public File getConfigPath() {
		final File envFolder = new File(propertiesFolder, appCoordinates.getEnvironment());
		final File appFolder = new File(envFolder, appCoordinates.getApplicationName());
		return appFolder.getAbsoluteFile();
	}

	private Map<String, Object> decrypt(Map<String, Object> sourceMap) {
		final HashMap<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			result.put(entry.getKey(), decryptIfEncrypted(entry.getValue()));
		}
		return result;
	}

	private Object decryptIfEncrypted(Object obj) {
		if (obj.toString().startsWith(CIPHER_PREFIX)) {
			return decrypt(obj.toString());
		} else {
			return obj;
		}
	}

	private String decrypt(String encrypted) {
		final String encryptedStr = encrypted.substring(CIPHER_PREFIX.length());
		return encryptor.decrypt(encryptedStr);
	}
}
