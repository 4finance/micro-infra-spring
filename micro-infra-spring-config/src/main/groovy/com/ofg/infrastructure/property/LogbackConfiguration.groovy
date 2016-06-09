package com.ofg.infrastructure.property

import groovy.transform.CompileStatic

/**
 * It reads Logback configuration from global.properties file
 * or uses a default configuration if a file doesn't exist or it
 * exists but doesn't contain a logger configuration.
 *
 * This class should be imported in logback.groovy file and used.
 */
@CompileStatic
class LogbackConfiguration {

    private static String DEFAULT_FILENAME = 'logs/application.log'
    private static String DEFAULT_LOG_PATTERN =
            '%d{yyyy-MM-dd HH:mm:ss.SSSZ, Europe/Warsaw} | %-5level | %X{X-B3-TraceId} | %thread | %logger{1} | %m%n'
    private static String DEFAULT_SCAN_TIME = '1 minutes'
    private static String DEFAULT_ROLLING_FILENAME_PATTERN = 'logs/application.%d{yyyy-MM-dd}.log.zip'
    private static int DEFAULT_ROLLING_MAX_HISTORY = 7

    private static final String LOG_PATTERN_KEY = 'logger.log.pattern'
    private static final String SCAN_TIME_KEY = 'logger.scan.time'
    private static final String ROLLING_FILENAME_PATTERN_KEY = 'logger.rolling.filename.pattern'
    private static final String FILENAME_KEY = 'logger.filename'
    private static final String ROLLING_MAX_HISTORY_KEY = 'logger.rolling.history.max'

    private Properties globalProperties

    String getLogPattern() {
        return globalProperty(LOG_PATTERN_KEY) ?: DEFAULT_LOG_PATTERN
    }

    String getScanTime() {
        return globalProperty(SCAN_TIME_KEY) ?: DEFAULT_SCAN_TIME
    }

    String getRollingFilenamePattern() {
        return globalProperty(ROLLING_FILENAME_PATTERN_KEY) ?: DEFAULT_ROLLING_FILENAME_PATTERN
    }

    String getLoggerFilename() {
        return globalProperty(FILENAME_KEY) ?: DEFAULT_FILENAME
    }

    int getRollingMaxHistory() {
        String maxHistory = globalProperty(ROLLING_MAX_HISTORY_KEY)
        return maxHistory != null ? Integer.parseInt(maxHistory) : DEFAULT_ROLLING_MAX_HISTORY
    }

    private String globalProperty(String propKey) {
        return globalProperties()[propKey]
    }

    private Properties globalProperties() {
        if (globalProperties == null) {
            Properties props = new Properties()
            File globalPropsFile = globalPropertiesFile()
            if (globalPropsFile.exists()) {
                globalPropsFile.withInputStream {
                    stream -> props.load(stream)
                }
            }
            globalProperties = props
        }
        return globalProperties
    }

    private File globalPropertiesFile() {
        return new File(PropertiesFolderFinder.find(), '/common/global.properties')
    }
}
