package com.ofg.infrastructure.property

import spock.lang.Specification
import spock.lang.Unroll


@Unroll
class AppCoordinatesTest extends Specification {

    def 'should properly resolve config files for name #name'() {
        given:
            def coords = new AppCoordinates(env, name, country)

        expect:
            coords.getConfigFileNames(new File('/home')) == paths.collect { new File(it) }

        where:
            env    | country | name                || paths
            'prod' | 'pl'    | 'foo'               || ['/home/prod/foo.properties', '/home/prod/foo.yaml', '/home/prod/foo-pl.properties', '/home/prod/foo-pl.yaml']
            'prod' | 'pl'    | '/a/b/c'            || ['/home/prod/a/b/c.properties', '/home/prod/a/b/c.yaml', '/home/prod/a/b/c-pl.properties', '/home/prod/a/b/c-pl.yaml']
            'dev'  | 'es'    | '/com/ofg/fraud-es' || ['/home/dev/com/ofg/fraud.properties', '/home/dev/com/ofg/fraud.yaml', '/home/dev/com/ofg/fraud-es.properties', '/home/dev/com/ofg/fraud-es.yaml']
    }

}
