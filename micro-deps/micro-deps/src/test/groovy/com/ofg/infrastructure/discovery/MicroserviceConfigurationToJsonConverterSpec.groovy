package com.ofg.infrastructure.discovery

import com.ofg.infrastructure.discovery.util.LoadBalancerType
import groovy.json.JsonSlurper
import spock.lang.Specification

class MicroserviceConfigurationToJsonConverterSpec extends Specification {

    def "should convert classes to json representation of microservices.json"() {
        given:
            String basePath = 'pl'
            MicroserviceConfiguration microserviceConfiguration = new MicroserviceConfiguration(new ServicePath('/com/ofg/foo-bar'), [
                    new MicroserviceConfiguration.Dependency(new ServiceAlias('alias'), new ServicePath('/com/ofg/alias'),
                            true, LoadBalancerType.STICKY, 'a.b.c.$version', 'v1', [a:'b'],
                            new MicroserviceConfiguration.Dependency.StubsConfiguration(new ServicePath('a/b/c'))),
                    new MicroserviceConfiguration.Dependency(new ServiceAlias('simple'), new ServicePath('/com/ofg/simple'))
            ])
        when:
            String configuration = MicroserviceConfigurationToJsonConverter.fromConfiguration(basePath, microserviceConfiguration)
        then:
            new JsonSlurper().parseText(configuration) == new JsonSlurper().parseText('''
{
  "pl": {
    "this": "/com/ofg/foo-bar",
    "dependencies": {
      "alias": {
        "path": "/com/ofg/alias",
        "stubs": "a.b:c:stubs",
        "load-balancer": "STICKY",
        "required": true,
        "contentTypeTemplate": "a.b.c.$version",
        "headers": {
          "a": "b"
        },
        "version": "v1"
      },
      "simple": {
        "path": "/com/ofg/simple",
        "stubs": "com.ofg:simple:stubs",
        "load-balancer": "ROUND_ROBIN",
        "required": false,
        "contentTypeTemplate": "",
        "headers": {
          
        },
        "version": ""
      }
    }
  }
}
''')
    }

}
