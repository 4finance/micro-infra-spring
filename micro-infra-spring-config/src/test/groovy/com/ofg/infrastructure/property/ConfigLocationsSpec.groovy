package com.ofg.infrastructure.property

import spock.lang.Specification

class ConfigLocationsSpec extends Specification {

    private static final String TEST_BASE_NAME = 'test'
    private static final String TEST_PROPERTIES = 'test.properties'
    private static final String TEST_YAML = 'test.yaml'

    ConfigLocations configDirs = new ConfigLocations(new File('properties'), 'com/ofg', 'prod', 'pl')

    def 'should return correct global properties file'() {
        expect:
            configDirs.globalPropertiesFile() ==
                    getFile('properties/common/global.properties')
    }

    def 'should return correct global yaml file'() {
        expect:
            configDirs.globalYamlFile() ==
                    getFile('properties/common/global.yaml')
    }

    def 'should return correct common properties file'() {
        expect:
            configDirs.commonPropertiesFile(TEST_BASE_NAME) ==
                    propertiesTestFile('properties/common/com/ofg/')
    }

    def 'should return correct common yaml file'() {
        expect:
            configDirs.commonYamlFile(TEST_BASE_NAME) ==
                    yamlTestFile('properties/common/com/ofg/')
    }

    def 'should return correct env properties file'() {
        expect:
            configDirs.envPropertiesFile(TEST_BASE_NAME) ==
                    propertiesTestFile('properties/prod/com/ofg/')
    }

    def 'should return correct env yaml file'() {
        expect:
            configDirs.envYamlFile(TEST_BASE_NAME) ==
                    yamlTestFile('properties/prod/com/ofg/')
    }

    def 'should return correct common country properties file'() {
        expect:
            configDirs.commonCountryPropertiesFile(TEST_BASE_NAME) ==
                    propertiesTestFile('properties/common/com/ofg/pl/')
    }

    def 'should return correct common country yaml file'() {
        expect:
            configDirs.commonCountryYamlFile(TEST_BASE_NAME) ==
                    yamlTestFile('properties/common/com/ofg/pl/')
    }

    def 'should return correct env country properties file'() {
        expect:
            configDirs.envCountryPropertiesFile(TEST_BASE_NAME) ==
                    propertiesTestFile('properties/prod/com/ofg/pl/')
    }

    def 'should create correct env country yaml file'() {
        expect:
            configDirs.envCountryYamlFile(TEST_BASE_NAME) ==
                    yamlTestFile('properties/prod/com/ofg/pl/')
    }

    private File propertiesTestFile(String basePath) {
        return getFile(basePath + TEST_PROPERTIES)
    }

    private File yamlTestFile(String basePath) {
        return getFile(basePath + TEST_YAML)
    }

    private File getFile(String path) {
        return new File(path)
    }
}
