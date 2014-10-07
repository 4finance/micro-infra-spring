package com.ofg.stub.mapping

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ProjectMetadataRepositorySpec extends Specification {

    @Rule public TemporaryFolder tempPath = new TemporaryFolder();

    def 'should fail to initialize repository when projects metadata directory is missing'() {
        given:
            File doesNotExists = new File(tempPath.newFolder(), 'doesNotExists')
        when:
            new ProjectMetadataRepository(doesNotExists)
        then:
            thrown(InvalidRepositoryLayout)
    }
}
