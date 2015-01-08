package com.ofg.stub.mapping

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DescriptorRepositorySpec extends Specification {

    @Rule public TemporaryFolder tempPath = new TemporaryFolder();

    def 'should fail to initialize stub repository when descriptors directory is missing'() {
        given:
            File doesNotExists = new File(tempPath.newFolder(), 'doesNotExists')
        when:
            new DescriptorRepository(doesNotExists)
        then:
            thrown(InvalidRepositoryLayout)
    }
}
