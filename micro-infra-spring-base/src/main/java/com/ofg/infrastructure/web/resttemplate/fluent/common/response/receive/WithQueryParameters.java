package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive;

import com.google.common.base.Throwables;
import com.ofg.infrastructure.web.resttemplate.fluent.UrlUtils;
import groovy.transform.TypeChecked;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Default implementation of query parameters setting for requests
 *
 * @param < T > - original class to be returned once query parameters setting has finished
 */
@TypeChecked
public class WithQueryParameters<T> implements QueryParametersSetting<T>, QueryParametersHaving<T> {
    private final T parent;
    private final Map<String, Object> queryParams = new TreeMap<>();
    private Map params;

    public WithQueryParameters(T parent, Map<String, Object> params) {
        this.parent = parent;
        this.params = params;
    }

    @Override
    public T andExecuteFor() {
        return parent;
    }

    @Override
    public QueryParametersSetting<T> withQueryParameters() {
        return this;
    }

    @Override
    public QueryParametersSetting<T> parameter(String key, Object value) {
        queryParams.put(key, value);
        try {
            params.put("urlWithQueryParameters", UrlUtils.addQueryParametersToUri((URI) params.get("url"), queryParams));
        } catch (URISyntaxException e) {
            Throwables.propagate(e);
        }
        return this;
    }
}
