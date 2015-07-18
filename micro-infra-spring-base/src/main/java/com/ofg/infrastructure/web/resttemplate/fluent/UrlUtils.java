package com.ofg.infrastructure.web.resttemplate.fluent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class UrlUtils {

    private static final String FIRST_PARAMETER_CONCATENATOR = "?";
    private static final String PARAMETERS_ASSIGNMENT = "=";
    private static final String SECOND_AND_LATER_CONCATENATOR = "&";

    public static URI addQueryParametersToUri(URI uri, Map<String, Object> params) throws URISyntaxException {
        StringBuilder urlParametersBuilder = new StringBuilder();
        if (uri == null) {
            throw new IllegalArgumentException("Define URL before URL parameters");
        }
        urlParametersBuilder.append(uri.toString());
        String concatenator = FIRST_PARAMETER_CONCATENATOR;
        for (String paramName : params.keySet()) {
            validateParameters(paramName);
            urlParametersBuilder.append(concatenator);
            urlParametersBuilder.append(paramName);
            Object parameterValue = params.get(paramName);

            if (parameterValue != null && !parameterValue.toString().isEmpty()) {
                urlParametersBuilder.append(PARAMETERS_ASSIGNMENT);
                urlParametersBuilder.append(parameterValue.toString());
            }
            concatenator = SECOND_AND_LATER_CONCATENATOR;
        }
        return new URI(urlParametersBuilder.toString());
    }

    private static void validateParameters(String paramName) {
        if (isNullOrEmpty(paramName)) {
            throw new IllegalArgumentException("Parameter name should be not null or empty");
        }
    }
}
