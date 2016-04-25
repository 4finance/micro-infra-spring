package com.ofg.infrastructure.property

import spock.lang.Specification
import spock.lang.Unroll


class AppCoordinatesSpec extends Specification {

    def 'should properly resolve config files for name #name'() {
        given:
            def coords = new AppCoordinates(env, name, country)

        expect:
            coords.getConfigFiles(new File('/home')) == paths.collect { new File(new File('/home'), it) }

        where:
            env        | country | name                                   || paths
           'prod'      | 'pl'    | 'foo'                                  || ['/common/global.properties',
                                                                              '/common/global.yaml',
                                                                              '/common/foo.properties',
                                                                              '/common/foo.yaml',
                                                                              '/prod/foo.properties',
                                                                              '/prod/foo.yaml',
                                                                              '/common/pl/foo-pl.properties',
                                                                              '/common/pl/foo-pl.yaml',
                                                                              '/prod/pl/foo-pl.properties',
                                                                              '/prod/pl/foo-pl.yaml']
            'prod'     | 'pl'    | '/a/b/c'                               || ['/common/global.properties',
                                                                              '/common/global.yaml',
                                                                              '/common/a/b/c.properties',
                                                                              '/common/a/b/c.yaml',
                                                                              '/prod/a/b/c.properties',
                                                                              '/prod/a/b/c.yaml',
                                                                              '/common/a/b/pl/c-pl.properties',
                                                                              '/common/a/b/pl/c-pl.yaml',
                                                                              '/prod/a/b/pl/c-pl.properties',
                                                                              '/prod/a/b/pl/c-pl.yaml']
            'dev'      | 'es'    | '/com/ofg/fraud-es'                    || ['/common/global.properties',
                                                                              '/common/global.yaml',
                                                                              '/common/com/ofg/fraud.properties',
                                                                              '/common/com/ofg/fraud.yaml',
                                                                              '/dev/com/ofg/fraud.properties',
                                                                              '/dev/com/ofg/fraud.yaml',
                                                                              '/common/com/ofg/es/fraud-es.properties',
                                                                              '/common/com/ofg/es/fraud-es.yaml',
                                                                              '/dev/com/ofg/es/fraud-es.properties',
                                                                              '/dev/com/ofg/es/fraud-es.yaml']
            'dev'      | 'pl'    | '/com/ofg/pl/fraud-pl'                 || ['/common/global.properties',
                                                                              '/common/global.yaml',
                                                                              '/common/com/ofg/fraud.properties',
                                                                              '/common/com/ofg/fraud.yaml',
                                                                              '/dev/com/ofg/fraud.properties',
                                                                              '/dev/com/ofg/fraud.yaml',
                                                                              '/common/com/ofg/pl/fraud-pl.properties',
                                                                              '/common/com/ofg/pl/fraud-pl.yaml',
                                                                              '/dev/com/ofg/pl/fraud-pl.properties',
                                                                              '/dev/com/ofg/pl/fraud-pl.yaml']
            'dev'      | 'pl'    | '/com/ofg/pl/fraud'                    || ['/common/global.properties',
                                                                              '/common/global.yaml',
                                                                              '/common/com/ofg/fraud.properties',
                                                                              '/common/com/ofg/fraud.yaml',
                                                                              '/dev/com/ofg/fraud.properties',
                                                                              '/dev/com/ofg/fraud.yaml',
                                                                              '/common/com/ofg/pl/fraud-pl.properties',
                                                                              '/common/com/ofg/pl/fraud-pl.yaml',
                                                                              '/dev/com/ofg/pl/fraud-pl.properties',
                                                                              '/dev/com/ofg/pl/fraud-pl.yaml']
            'stage-01' | 'ro'    | 'com/ofg/loans/ro/backoffice-vivus-ro' || ['/common/global.properties',
                                                                              '/common/global.yaml',
                                                                              '/common/com/ofg/loans/backoffice-vivus.properties',
                                                                              '/common/com/ofg/loans/backoffice-vivus.yaml',
                                                                              '/stage-01/com/ofg/loans/backoffice-vivus.properties',
                                                                              '/stage-01/com/ofg/loans/backoffice-vivus.yaml',
                                                                              '/common/com/ofg/loans/ro/backoffice-vivus-ro.properties',
                                                                              '/common/com/ofg/loans/ro/backoffice-vivus-ro.yaml',
                                                                              '/stage-01/com/ofg/loans/ro/backoffice-vivus-ro.properties',
                                                                              '/stage-01/com/ofg/loans/ro/backoffice-vivus-ro.yaml']
    }
}
