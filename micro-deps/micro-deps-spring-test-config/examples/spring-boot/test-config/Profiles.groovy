package com.ofg.microservice.config

import groovy.transform.TypeChecked

@TypeChecked
class Profiles {
    // WARNING: these have to be explicit public (even though it's groovy) because otherwise we cannot use them in tests
    // for more info: http://jira.codehaus.org/browse/GROOVY-3278
    public static final String PRODUCTION = "prod"
    public static final String TEST = "test"
    public static final String DEVELOPMENT = "dev"

    static List<String> all() {
        return [PRODUCTION, TEST, DEVELOPMENT]
    }
}