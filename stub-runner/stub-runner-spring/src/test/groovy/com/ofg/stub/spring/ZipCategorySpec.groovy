package com.ofg.stub.spring

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import spock.lang.Specification

import static groovyx.net.http.Method.HEAD
import static groovy.grape.Grape.addResolver
import static groovy.grape.Grape.resolve
import static groovy.io.FileType.FILES

@Slf4j
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
