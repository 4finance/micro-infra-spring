package com.ofg.config

import groovy.transform.CompileStatic

/**
 * 4finance default Spring profiles
 */
@CompileStatic
class BasicProfiles {

    public static final String DEVELOPMENT = 'dev'

    public static final String PRODUCTION = 'prod'

    public static final String TEST = 'test'

    public static List<String> all() {
        return [DEVELOPMENT, PRODUCTION, TEST]
    }
}