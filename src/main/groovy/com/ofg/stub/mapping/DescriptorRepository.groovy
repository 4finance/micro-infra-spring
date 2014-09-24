package com.ofg.stub.mapping

import groovy.transform.TypeChecked

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.GLOBAL
import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

@TypeChecked
class DescriptorRepository {
    private final File repository

    DescriptorRepository(File repository) {
        this.repository = repository
    }

    List<MappingDescriptor> getAllProjectDescriptors(ProjectMetadata project) {
        List<MappingDescriptor> mappingDescriptors = []
        mappingDescriptors.addAll(globalContextDescriptorsFrom(project))
        mappingDescriptors.addAll(realmContextDescriptorsFrom(project))
        return mappingDescriptors
    }

    private List<MappingDescriptor> globalContextDescriptorsFrom(ProjectMetadata project) {
        List<MappingDescriptor> mappingDescriptors = []
        new File(repository, project.projectName).eachFileRecurse { File file ->
            if (isMappingDescriptor(file)) {
                mappingDescriptors << new MappingDescriptor(file, GLOBAL)
            }
        }
        return mappingDescriptors
    }

    private List<MappingDescriptor> realmContextDescriptorsFrom(ProjectMetadata project) {
        List<MappingDescriptor> mappingDescriptors = []
        new File(repository, project.fullPath).eachFileRecurse { File file ->
            if (isMappingDescriptor(file)) {
                mappingDescriptors << new MappingDescriptor(file, REALM_SPECIFIC)
            }
        }
        return mappingDescriptors
    }

    URI getLocation() {
        return repository.toURI()
    }

    private static boolean isMappingDescriptor(File file) {
        return file.isFile() && file.name.endsWith('.json')
    }
}
