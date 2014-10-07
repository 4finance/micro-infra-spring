package com.ofg.stub.mapping

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@PackageScope
@CompileStatic
class ProjectMetadataRepository {
    @PackageScope final File path

    ProjectMetadataRepository(File repository) {
        if (!repository.isDirectory()) {
            throw new InvalidRepositoryLayout('missing project metadata repository')
        }
        path = repository
    }

    URI getMetadataLocation(String metadataFileName) {
        return new File(path, metadataFileName).toURI()
    }
}
