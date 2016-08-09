package io.fourfinance.activity_tracker.activity;

import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import static com.google.common.base.Preconditions.checkNotNull;

class JoinPointParameters {

    private final Map<String, String> parameters;

    JoinPointParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        List<String> names = signature.getParameterNames() != null ? List.of(signature.getParameterNames()) : List.empty();
        List<Object> values = joinPoint.getArgs() != null ? List.of(joinPoint.getArgs()) : List.empty();
        parameters = HashMap.ofEntries(names.zip(values).map(t -> Tuple.of(t._1, t._2.toString())));
    }

    Option<String> getValue(String parameterName) {
        checkNotNull(parameterName, "JoinPoint param must not be null");
        if (parameters.containsKey(parameterName)) {
            return parameters.get(parameterName);
        } else {
            return Option.none();
        }
    }
}
