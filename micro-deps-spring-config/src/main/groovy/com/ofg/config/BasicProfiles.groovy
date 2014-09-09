package com.ofg.config

import groovy.transform.CompileStatic

/**
 * 4finance default Spring profiles
 */
@CompileStatic
interface BasicProfiles {

    String DEVELOPMENT = 'dev'

    String PRODUCTION = 'prod'

    String TEST = 'test'

}