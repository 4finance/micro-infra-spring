package com.ofg.stub.mapping

import groovy.transform.TypeChecked

@TypeChecked
class DescriptorRepository {
    private final File repository

    DescriptorRepository(File repository) {
        this.repository = repository
    }

    List<MappingDescriptor> getAllProjectDescriptors(ProjectMetadata project) {
        List<MappingDescriptor> mappingDescriptors = []
        new File(repository, project.projectName).eachFileRecurse { File file ->
            if (isMappingDescriptor(file)) {
                mappingDescriptors << new MappingDescriptor(file)
            }
        }
        return mappingDescriptors
    }

    private static boolean isMappingDescriptor(File file) {
        return file.isFile() && file.name.endsWith('.json')
    }
}
