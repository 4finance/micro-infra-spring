package com.ofg.infrastructure.property;

import java.util.Objects;

class AppCoordinates {

	public static final String CONFIG_FOLDER = "CONFIG_FOLDER";
	public static final String APP_ENV = "APP_ENV";
	public static final String COUNTRY_CODE = "countryCode";

	private final String environment;
	private final String applicationName;
	private final String countryCode;

	public AppCoordinates() {
		this(findEnvironment(), findApplicationName(), findCountry());
	}

	public AppCoordinates(String environment, String applicationName, String countryCode) {
		this.environment = environment;
		this.applicationName = applicationName;
		this.countryCode = countryCode;
	}

	private static String findCountry() {
		return Objects.requireNonNull(System.getProperty(COUNTRY_CODE), "No " + COUNTRY_CODE + " property found");
	}

	private static String findApplicationName() {
		return "micro-app";
	}

	private static String findEnvironment() {
		final String envOrNull = PropertyUtils.getProperty(APP_ENV, null);
		return Objects.requireNonNull(envOrNull, "No " + APP_ENV + " property found");
	}

	public String getEnvironment() {
		return environment;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getCountryCode() {
		return countryCode;
	}
}
