package com.ofg.infrastructure.property

import spock.lang.Specification
import spock.lang.Unroll


@Unroll
class AppCoordinatesTest extends Specification {

    def 'should properly resolve config files for name #name'() {
        given:
            def coords = new AppCoordinates(env, name, country)

        expect:
            coords.getConfigFileNames(new File('/home')) == paths.collect { new File(new File('/home'), it) }

        where:
            env    | country | name                   || paths
            'prod' | 'pl'    | 'foo'                  || ['/prod/foo.properties', '/prod/foo.yaml', '/prod/foo-pl.properties', '/prod/foo-pl.yaml']
            'prod' | 'pl'    | '/a/b/c'               || ['/prod/a/b/c.properties', '/prod/a/b/c.yaml', '/prod/a/b/c-pl.properties', '/prod/a/b/c-pl.yaml']
            'dev'  | 'es'    | '/com/ofg/fraud-es'    || ['/dev/com/ofg/fraud.properties', '/dev/com/ofg/fraud.yaml', '/dev/com/ofg/fraud-es.properties', '/dev/com/ofg/fraud-es.yaml']
            'dev'  | 'pl'    | '/com/ofg/pl/fraud-pl' || ['/dev/com/ofg/fraud.properties', '/dev/com/ofg/fraud.yaml', '/dev/com/ofg/fraud-pl.properties', '/dev/com/ofg/fraud-pl.yaml']
            'dev'  | 'pl'    | '/com/ofg/pl/fraud'    || ['/dev/com/ofg/fraud.properties', '/dev/com/ofg/fraud.yaml', '/dev/com/ofg/fraud-pl.properties', '/dev/com/ofg/fraud-pl.yaml']
    }

}
