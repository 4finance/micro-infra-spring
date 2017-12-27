package com.ofg.infrastructure.property

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import spock.lang.Subject
import spock.util.environment.RestoreSystemProperties

import static com.ofg.infrastructure.property.AppCoordinates.CONFIG_FOLDER

@RestoreSystemProperties
class LogbackConfigurationSpec extends Specification {

    @Rule
    EnvironmentVariables environmentVariables = new EnvironmentVariables()

    @Subject
    LogbackConfiguration config = new LogbackConfiguration()

    def setup() {
        environmentVariables.set(CONFIG_FOLDER, null)
        System.clearProperty(CONFIG_FOLDER)
    }

    def 'log pattern should contain correlationId'() {
        expect:
            config.getLogPattern().contains('X-B3-TraceId')
    }

    def 'scan time should be in minutes'() {
        expect:
            config.getScanTime().contains('minutes')
    }

    def 'rolled files should be zipped'() {
        expect:
            config.getRollingFilenamePattern().endsWith('zip')
    }

    def 'log keyword should be present in logger filename'() {
        expect:
            config.getLoggerFilename().contains('log')
    }

    def 'rolling max history should be positive'() {
        expect:
            config.getRollingMaxHistory() > 0
    }

    def 'should read logger configuration from global.properties file'() {
        given:
            System.setProperty(CONFIG_FOLDER, getConfigFolder())
        expect:
            config.getLoggerFilename() == 'logs/fake123Logger.log'
    }

    private String getConfigFolder() {
        return getClass().getResource('/test-config-dir').file
    }
}
