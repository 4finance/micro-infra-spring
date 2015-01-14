package com.ofg.infrastructure.web.resttemplate

import spock.lang.Specification
import spock.lang.Unroll


@Unroll
class MetricsAspectSpec extends Specification {

    def 'should turn "#url" into valid metric name: "#expectedMetricName"'() {
        when:
            def metricName = MetricsAspect.metricName(url)

        then:
            metricName == "RestOperations.$expectedMetricName"

        where:
            url                                    || expectedMetricName
            'http://localhost:8080'                || 'localhost.8080'
            'http://foo:8080/'                     || 'foo.8080'
            'http://bar:7090/buzz'                 || 'bar.7090.buzz'
            'http://db:1000/customer/{id}/address' || 'db.1000.customer.id.address'
            'http://db:1000/data.xml'              || 'db.1000.data_xml'
            'http://db/data.xml'                   || 'db.80.data_xml'
            'http://db:6006/data?foo=bar'          || 'db.6006.data'
    }

}
