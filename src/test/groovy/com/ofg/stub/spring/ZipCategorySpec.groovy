package com.ofg.stub.spring

import spock.lang.Specification

class ZipCategorySpec extends Specification {

    def 'should unzip a file to the specified location'() {
        given:
            File zipFile = new File(ZipCategorySpec.classLoader.getResource('file.zip').toURI())
            File tempDir = File.createTempDir()
            tempDir.deleteOnExit()
        when:
            use(ZipCategory) {
                zipFile.unzipTo(tempDir)
            }
        then:
            tempDir.listFiles().find {
                it.name == 'file.txt'
            }?.text?.trim() == 'test'
    }

}
