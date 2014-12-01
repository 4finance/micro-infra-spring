package com.ofg.infrastructure.property

import spock.lang.Specification
import spock.lang.Unroll


@Unroll
class AppCoordinatesTest extends Specification {

    def 'should properly resolve config files for name #name'() {
        given:
            def coords = new AppCoordinates(env, name, country)

        expect:
            coords.getConfigFiles(new File('/home')) == paths.collect { new File(new File('/home'), it) }

        where:
            env        | country | name                                   || paths
            'prod'     | 'pl'    | 'foo'                                  || ['/prod/foo.properties',
                                                                              '/prod/foo.yaml',
                                                                              '/prod/pl/foo-pl.properties',
                                                                              '/prod/pl/foo-pl.yaml']
            'prod'     | 'pl'    | '/a/b/c'                               || ['/prod/a/b/c.properties',
                                                                              '/prod/a/b/c.yaml',
                                                                              '/prod/a/b/pl/c-pl.properties',
                                                                              '/prod/a/b/pl/c-pl.yaml']
            'dev'      | 'es'    | '/com/ofg/fraud-es'                    || ['/dev/com/ofg/fraud.properties',
                                                                              '/dev/com/ofg/fraud.yaml',
                                                                              '/dev/com/ofg/es/fraud-es.properties',
                                                                              '/dev/com/ofg/es/fraud-es.yaml']
            'dev'      | 'pl'    | '/com/ofg/pl/fraud-pl'                 || ['/dev/com/ofg/fraud.properties',
                                                                              '/dev/com/ofg/fraud.yaml',
                                                                              '/dev/com/ofg/pl/fraud-pl.properties',
                                                                              '/dev/com/ofg/pl/fraud-pl.yaml']
            'dev'      | 'pl'    | '/com/ofg/pl/fraud'                    || ['/dev/com/ofg/fraud.properties',
                                                                              '/dev/com/ofg/fraud.yaml',
                                                                              '/dev/com/ofg/pl/fraud-pl.properties',
                                                                              '/dev/com/ofg/pl/fraud-pl.yaml']
            'stage-01' | 'ro'    | 'com/ofg/loans/ro/backoffice-vivus-ro' || ['/stage-01/com/ofg/loans/backoffice-vivus.properties',
                                                                              '/stage-01/com/ofg/loans/backoffice-vivus.yaml',
                                                                              '/stage-01/com/ofg/loans/ro/backoffice-vivus-ro.properties',
                                                                              '/stage-01/com/ofg/loans/ro/backoffice-vivus-ro.yaml']
    }
}
