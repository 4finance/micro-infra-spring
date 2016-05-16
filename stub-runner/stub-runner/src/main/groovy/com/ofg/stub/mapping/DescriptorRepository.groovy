package com.ofg.stub.mapping

import com.ofg.stub.mapping.MappingDescriptor.MappingType
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import static com.ofg.stub.mapping.MappingDescriptor.MappingType.GLOBAL
import static com.ofg.stub.mapping.MappingDescriptor.MappingType.REALM_SPECIFIC

@PackageScope
@CompileStatic
class DescriptorRepository {
    private final File path

    DescriptorRepository(File repository) {
        if (!repository.isDirectory()) {
            throw new InvalidRepositoryLayout('missing descriptor repository')
        }
        path = repository
    }

    List<MappingDescriptor> getProjectDescriptors(ProjectMetadata project) {
        List<MappingDescriptor> mappingDescriptors = []
        mappingDescriptors.addAll(globalContextDescriptorsFrom(project))
        mappingDescriptors.addAll(realmContextDescriptorsFrom(project))
        return mappingDescriptors
    }

    private List<MappingDescriptor> globalContextDescriptorsFrom(ProjectMetadata project) {
        File descriptorsDirectory = new File(path, project.projectRelativePath)
        return descriptorsDirectory.exists() ?
                collectMappingDescriptors(descriptorsDirectory, GLOBAL) : emptyMappingDescriptors()
    }

    private List<MappingDescriptor> realmContextDescriptorsFrom(ProjectMetadata project) {
        File descriptorsDirectory = new File(path, project.pathWithContext)
        return descriptorsDirectory.exists() ?
                collectMappingDescriptors(descriptorsDirectory, REALM_SPECIFIC) : emptyMappingDescriptors()
    }

    private ArrayList<MappingDescriptor> emptyMappingDescriptors() {
        new ArrayList<MappingDescriptor>()
    }

    private List<MappingDescriptor> collectMappingDescriptors(File descriptorsDirectory, MappingType mappingType) {
        List<MappingDescriptor> mappingDescriptors = []
        descriptorsDirectory.eachFileRecurse { File file ->
            if (isMappingDescriptor(file)) {
                mappingDescriptors << new MappingDescriptor(file, mappingType)
            }
        }
        return mappingDescriptors
    }

    private static boolean isMappingDescriptor(File file) {
        return file.isFile() && file.name.endsWith('.json')
    }
}
