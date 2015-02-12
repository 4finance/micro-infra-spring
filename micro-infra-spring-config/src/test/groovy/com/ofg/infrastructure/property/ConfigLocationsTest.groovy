package com.ofg.infrastructure.property

import spock.lang.Specification

class ConfigLocationsTest extends Specification {

    static final File COMMON_DIR = new File('props', 'common')
    static final File ENV_DIR = new File('props', 'prod')
    static final File COUNTRY_DIR = new File(ENV_DIR, 'pl')

    static final String TEST_BASE_NAME = 'test'
    static final String TEST_PROPERTIES = 'test.properties'
    static final String TEST_YAML = 'test.yaml'

    ConfigLocations configDirs = new ConfigLocations(COMMON_DIR, ENV_DIR, COUNTRY_DIR)

    def 'should return correct common properties file'() {
        expect:
            configDirs.commonPropertiesFile(TEST_BASE_NAME) == new File(COMMON_DIR, TEST_PROPERTIES)
    }

    def 'should return correct common yaml file'() {
        expect:
            configDirs.commonYamlFile(TEST_BASE_NAME) == new File(COMMON_DIR, TEST_YAML)
    }

    def 'should return correct env properties file'() {
        expect:
            configDirs.envPropertiesFile(TEST_BASE_NAME) == new File(ENV_DIR, TEST_PROPERTIES)
    }

    def 'should return correct env yaml file'() {
        expect:
            configDirs.envYamlFile(TEST_BASE_NAME) == new File(ENV_DIR, TEST_YAML)
    }

    def 'should return correct country properties file'() {
        expect:
            configDirs.countryPropertiesFile(TEST_BASE_NAME) == new File(COUNTRY_DIR, TEST_PROPERTIES)
    }

    def 'should create correct country yaml file'() {
        expect:
            configDirs.countryYamlFile(TEST_BASE_NAME) == new File(COUNTRY_DIR, TEST_YAML)
    }
}
