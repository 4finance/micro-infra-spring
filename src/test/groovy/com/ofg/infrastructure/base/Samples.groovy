package com.ofg.infrastructure.base

import groovy.transform.TypeChecked

@TypeChecked
class Samples {
    
    public static final String MICROSERVICE_CONFIG = '''
{
    "pl": {
        "this": "payments",
        "dependencies": {
            "trustly": "com/ofg/trustly-adapter"
        }
    }
}
'''
    
}
