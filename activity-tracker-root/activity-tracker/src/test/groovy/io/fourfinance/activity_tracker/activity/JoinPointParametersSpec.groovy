package io.fourfinance.activity_tracker.activity

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.junit.Test
import spock.lang.Specification

public class JoinPointParametersSpec extends Specification {

    JoinPoint joinPoint = Mock(JoinPoint)

    void setup() {
        MethodSignature signature = Mock(MethodSignature)
        joinPoint.signature >> signature
        signature.getParameterNames() >> ['foo']
    }

    @Test
    public void shouldReturnEmptyValueWhenThereIsNoValue() {
        given:
            JoinPointParameters joinPointParameters = new JoinPointParameters(joinPoint)
        expect:
            joinPointParameters.getValue("foo").isEmpty()
    }


}