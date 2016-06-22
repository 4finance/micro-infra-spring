package com.ofg.infrastructure.discovery

import spock.lang.Specification
import spock.lang.Unroll

class IgnorePayloadInstanceSerializerSpec extends Specification {

    private static final String INSTANCE_BYTES = '''
       {
            "name":"org/some/service",
            "id":"99873690-1e2b-4d00-8547-f59a97dd78e0",
            "address":"0.0.0.1",
            "port":8080,
            "sslPort":null,
            "payload":###PAYLOAD_CONTENT###,
            "registrationTimeUTC":123456,
            "serviceType":"DYNAMIC",
            "uriSpec":{
                "parts":[
                    {"value":"scheme","variable":true},
                    {"value":"://","variable":false},
                    {"value":"address","variable":true},
                    {"value":":","variable":false},
                    {"value":"port","variable":true}
                ]
            }
       }
    '''

    @Unroll
    def "Should deserialize service instance with payload content #payloadContent"() {
        given:
            byte[] instanceBytes = instanceBytes(payloadContent)
        when:
            new IgnorePayloadInstanceSerializer(Map.class).deserialize(instanceBytes)
        then:
            noExceptionThrown()
        where:
            payloadContent << ['null', '{"@class":"org.springframework.cloud.zookeeper.discovery.ZookeeperInstance","id":"org/some/service:springCloud:8080","name":"org/some/service","metadata":{}}']
    }

    byte[] instanceBytes(String payloadContent) {
        return INSTANCE_BYTES.replace('###PAYLOAD_CONTENT###', payloadContent).bytes
    }

}
