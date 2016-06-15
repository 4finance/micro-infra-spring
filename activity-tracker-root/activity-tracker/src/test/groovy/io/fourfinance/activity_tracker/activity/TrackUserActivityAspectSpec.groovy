package io.fourfinance.activity_tracker.activity

import io.fourfinance.activity_tracker.audit.TrackUserActivityAudits
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import spock.lang.Specification
import spock.lang.Subject

import java.lang.reflect.Method

class TrackUserActivityAspectSpec extends Specification {

    private TrackUserActivityAudits trackUserActivityAudits = Mock(TrackUserActivityAudits)
    private TrackUserActivityMetrics userActivityMetrics = Mock(TrackUserActivityMetrics)
    private JoinPoint joinPoint = Mock()
    private MethodSignature signature = Mock()

    @Subject
    private TrackUserActivityAspect trackUserActivityAspect = new TrackUserActivityAspect(
            trackUserActivityAudits,
            userActivityMetrics,
            new ActivityParameters("agentId", "personalId", "customerId"),
    )

    void setup() {
        joinPoint.signature >> signature
    }

    def "Should audit agentId"() {
        given:
            signature.method >> fooMethod('foo')
            recordMethodInvocationWithParams(agentId: 'superAgent')
        when:
            trackUserActivityAspect.audit(joinPoint)
        then:
            1 * trackUserActivityAudits.audit(*_) >> {
                Map arg -> assert arg.agentId == 'superAgent'
            }
    }

    def "Should audit customerId"() {
        given:
            signature.method >> fooMethod('foo')
            recordMethodInvocationWithParams(customerId: 'superCustomer')
        when:
            trackUserActivityAspect.audit(joinPoint)
        then:
            1 * trackUserActivityAudits.audit(*_) >> {
                Map arg -> assert arg.customerId == 'superCustomer'
            }
    }

    def "Should audit agentId and personalId"() {
        given:
            signature.method >> fooMethod('foo')
            recordMethodInvocationWithParams(agentId: 123, customerId: 'superCustomer')
        when:
            trackUserActivityAspect.audit(joinPoint)
        then:
            1 * trackUserActivityAudits.audit(*_) >> {
                Map arg -> 
                    assert arg.customerId == 'superCustomer'
                    assert arg.agentId == '123'
            }
    }
    
    def "Should audit not specified params as <not available>"() {
        given:
            signature.method >> fooMethod('foo')
        when:
            trackUserActivityAspect.audit(joinPoint)
        then:
            1 * trackUserActivityAudits.audit(*_) >> {
                Map arg ->
                    assert arg.agentId == '<not available>'
                    assert arg.customerId == '<not available>'
            }
    }

    def "Should audit invoked method using api operation description"() {
        given:
            signature.method >> fooMethod('foo')
        when:
            trackUserActivityAspect.audit(joinPoint)
        then:
            1 * trackUserActivityAudits.audit(*_) >> {
                Map arg -> assert arg.activity == 'Foo action'
            }
    }

    def "Should audit invoked using method name when api operation is missing"() {
        given:
            signature.method >> fooMethod('fooWithoutApiOperation')
        when:
            trackUserActivityAspect.audit(joinPoint)
        then:
            1 * trackUserActivityAudits.audit(*_) >> {
                Map arg -> assert arg.activity == 'empty'
            }
    }
    
    public Method fooMethod(String methodName) {
        return this.class.getDeclaredMethod(methodName)
    }

    private void recordMethodInvocationWithParams(Map params) {
        signature.parameterNames >> params.keySet()
        joinPoint.args >> params.values()
    }


    @TrackUserActivity('Foo action')
    public void foo() {

    }
    
    @TrackUserActivity
    public void fooWithoutApiOperation() {

    }
}
