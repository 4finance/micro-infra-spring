package io.fourfinance.activity_tracker.activity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

class JoinPointParameters {

    private final List<String> parameterNames;

    private final List<Object> parameterValues;

    JoinPointParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        parameterNames = signature.getParameterNames() != null ? asList(signature.getParameterNames()) : java.util
                .Collections.<String>emptyList();
        parameterValues = joinPoint.getArgs() != null ? asList(joinPoint.getArgs()) : emptyList();
    }

    Optional<Object> getValue(String parameterName) {
        Preconditions.checkNotNull(parameterName, "JoinPoint param must not be null");
        if(parameterNames.contains(parameterName)) {
            return Optional.fromNullable(parameterValues.get(indexOfParameter(parameterName)));
        } else {
            return Optional.absent();
        }
    }

    private int indexOfParameter(final String parameterName) {
        return parameterNames.indexOf(parameterName);
    }

}
