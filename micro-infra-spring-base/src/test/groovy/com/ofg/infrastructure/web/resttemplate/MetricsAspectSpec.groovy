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
            url                                                             || expectedMetricName
            'http://localhost:8080'                                         || 'localhost.8080'
            'http://foo:8080/'                                              || 'foo.8080'
            'http://bar:7090/buzz'                                          || 'bar.7090.buzz'
            'http://db:1000/customer/{id}/address'                          || 'db.1000.customer.id.address'
            'http://db:1000/data.xml'                                       || 'db.1000.data_xml'
            'http://db/data.xml'                                            || 'db.80.data_xml'
            'http://db:6006/data?foo=bar'                                   || 'db.6006.data'
            'http://192.168.1.100:6006'                                     || '192_168_1_100.6006'
            'http://192.168.1.100:6006/ticket/pay'                          || '192_168_1_100.6006.ticket.pay'
            'https://foo:5757/api/secure'                                   || 'foo.5757.api.secure'
            'http://ws.4finance.net:6006/ticket/pay'                        || 'ws_4finance_net.6006.ticket.pay'
            'http://[2001:db8::1]:80/ticket/pay'                            || '2001_db8_1.80.ticket.pay'
            'https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:443'            || '2001_db8_85a3_8d3_1319_8a2e_370_7348.443'
            'https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:443/api/secure' || '2001_db8_85a3_8d3_1319_8a2e_370_7348.443.api.secure'
            'http://[2001:db8::1428:57ab]:80'                               || '2001_db8_1428_57ab.80'
    }

}
