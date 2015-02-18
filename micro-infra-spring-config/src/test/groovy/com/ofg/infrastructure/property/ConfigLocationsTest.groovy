package com.ofg.infrastructure.property

import spock.lang.Specification

class ConfigLocationsTest extends Specification {

    static final File COMMON_DIR = new File('props', 'common')
    static final File ENV_DIR = new File('props', 'prod')
    static final File COMMON_COUNTRY_DIR = new File(COMMON_DIR, 'pl')
    static final File ENV_COUNTRY_DIR = new File(ENV_DIR, 'pl')

    static final String TEST_BASE_NAME = 'test'
    static final String TEST_PROPERTIES = 'test.properties'
    static final String TEST_YAML = 'test.yaml'

    ConfigLocations configDirs = new ConfigLocations(
            COMMON_DIR, ENV_DIR, COMMON_COUNTRY_DIR, ENV_COUNTRY_DIR)

    def 'should return correct common properties file'() {
        expect:
            configDirs.commonPropertiesFile(TEST_BASE_NAME) ==
                    new File(COMMON_DIR, TEST_PROPERTIES)
    }

    def 'should return correct common yaml file'() {
        expect:
            configDirs.commonYamlFile(TEST_BASE_NAME) ==
                    new File(COMMON_DIR, TEST_YAML)
    }

    def 'should return correct env properties file'() {
        expect:
            configDirs.envPropertiesFile(TEST_BASE_NAME) ==
                    new File(ENV_DIR, TEST_PROPERTIES)
    }

    def 'should return correct env yaml file'() {
        expect:
            configDirs.envYamlFile(TEST_BASE_NAME) ==
                    new File(ENV_DIR, TEST_YAML)
    }

    def 'should return correct common country properties file'() {
        expect:
            configDirs.commonCountryPropertiesFile(TEST_BASE_NAME) ==
                    new File(COMMON_COUNTRY_DIR, TEST_PROPERTIES)
    }

    def 'should return correct common country yaml file'() {
        expect:
            configDirs.commonCountryYamlFile(TEST_BASE_NAME) ==
                    new File(COMMON_COUNTRY_DIR, TEST_YAML)
    }

    def 'should return correct env country properties file'() {
        expect:
            configDirs.envCountryPropertiesFile(TEST_BASE_NAME) ==
                    new File(ENV_COUNTRY_DIR, TEST_PROPERTIES)
    }

    def 'should create correct env country yaml file'() {
        expect:
            configDirs.envCountryYamlFile(TEST_BASE_NAME) ==
                    new File(ENV_COUNTRY_DIR, TEST_YAML)
    }
}
