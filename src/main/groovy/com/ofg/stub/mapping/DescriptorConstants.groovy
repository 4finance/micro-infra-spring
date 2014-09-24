package com.ofg.stub.mapping

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic
@PackageScope
final class DescriptorConstants {

    public static final String MAPPINGS_FOLDER_NAME = 'mappings'
    public static final String PROJECTS_FOLDER_NAME = 'projects'

    private DescriptorConstants() {
        throw new IllegalAccessException("Can't instantiate an utility class")
    }
}
