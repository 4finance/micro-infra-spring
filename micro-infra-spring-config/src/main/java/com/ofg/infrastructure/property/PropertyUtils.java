package com.ofg.infrastructure.property;

public class PropertyUtils {

	public static String getProperty(String name, String defValue) {
		final String valOrNull = System.getProperty(name);
		if (valOrNull == null) {
			final String envValueOrNull = System.getenv(name);
			return envValueOrNull != null ? envValueOrNull : defValue;
		} else {
			return valOrNull;
		}
	}

}
