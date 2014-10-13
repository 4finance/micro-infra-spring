package com.ofg.infrastructure.metrics.publishing

import com.codahale.metrics.MetricRegistry
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class CsvPublisherSpec extends Specification {
    private final TimeUnit VALID_TIME_UNIT = TimeUnit.SECONDS

    @ClassRule @Shared TemporaryFolder temp = new TemporaryFolder()

    def 'should throw OutputDirectoryDoesNotExists when output directory does not exists'() {
        when:
            initializeCsvPublisher(outputDirectory)
        then:
            thrown(OutputDirectoryDoesNotExists)
        where:
            outputDirectory << [new File(temp.newFolder(), 'missingDir'), temp.newFile()]
    }

    def 'should initialize when output directory exists'() {
        given:
            File outputDirectory = temp.newFolder()
        when:
            initializeCsvPublisher(outputDirectory)
        then:
            noExceptionThrown()
    }

    private CsvPublisher initializeCsvPublisher(File outputDirectory) {
        return new CsvPublisher(outputDirectory, Stub(PublishingInterval), Stub(MetricRegistry), VALID_TIME_UNIT, VALID_TIME_UNIT)
    }
}
