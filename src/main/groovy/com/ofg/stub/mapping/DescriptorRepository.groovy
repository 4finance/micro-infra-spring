package com.ofg.stub.mapping

import com.ofg.stub.mapping.MappingDescriptor.MappingType
import groovy.transform.TypeChecked

import static com.ofg.stub.mapping.DescriptorConstants.MAPPINGS_FOLDER_NAME
import static com.ofg.stub.mapping.MappingDescriptor.MappingType.GLOBAL
import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

@TypeChecked
class DescriptorRepository {
    private final File repositoryRoot

    DescriptorRepository(File repositoryRoot) {
        this.repositoryRoot = repositoryRoot
    }

    List<MappingDescriptor> getAllProjectDescriptors(ProjectMetadata project) {
        List<MappingDescriptor> mappingDescriptors = []
        mappingDescriptors.addAll(globalContextDescriptorsFrom(project))
        mappingDescriptors.addAll(realmContextDescriptorsFrom(project))
        return mappingDescriptors
    }

    private List<MappingDescriptor> globalContextDescriptorsFrom(ProjectMetadata project) {
        File filesToIterate = new File(appendMappingsSuffix(repositoryRoot.path), project.projectRelativePath)
        if (!filesToIterate.exists()) {
            return []
        }
        return collectMappingDescriptors(filesToIterate, GLOBAL)
    }

    private List<MappingDescriptor> realmContextDescriptorsFrom(ProjectMetadata project) {
        File filesToIterate = new File(appendMappingsSuffix(repositoryRoot.path), project.pathWithContext)
        if (!filesToIterate.exists()) {
            return []
        }
        return collectMappingDescriptors(filesToIterate, REALM_SPECIFIC)
    }

    private List<MappingDescriptor> collectMappingDescriptors(File filesToIterate, MappingType mappingType) {
        List<MappingDescriptor> mappingDescriptors = []
        filesToIterate.eachFileRecurse { File file ->
            if (isMappingDescriptor(file)) {
                mappingDescriptors << new MappingDescriptor(file, mappingType)
            }
        }
        return mappingDescriptors
    }

    URI getLocation() {
        return repositoryRoot.toURI()
    }

    private static boolean isMappingDescriptor(File file) {
        return file.isFile() && file.name.endsWith('.json')
    }

    private static String appendMappingsSuffix(String path) {
        return "$path/$MAPPINGS_FOLDER_NAME"
    }
}
