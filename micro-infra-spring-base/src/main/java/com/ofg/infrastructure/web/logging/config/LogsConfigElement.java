package com.ofg.infrastructure.web.logging.config;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogsConfigElement {

    String skipAll = Boolean.FALSE.toString();
    String method = StringUtils.EMPTY;
    String urlPattern = StringUtils.EMPTY;
    List<String> filteredReqHeaders = new ArrayList<>();
    List<String> filteredResHeaders = new ArrayList<>();
    List<String> filteredReqFields = new ArrayList<>();
    List<String> filteredResFields = new ArrayList<>();

    public String getSkipAll() {
        return skipAll;
    }

    public void setSkipAll(String skipAll) {
        this.skipAll = skipAll;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public List<String> getFilteredReqHeaders() {
        return filteredReqHeaders;
    }

    public void setFilteredReqHeaders(List<String> filteredReqHeaders) {
        this.filteredReqHeaders = filteredReqHeaders;
    }

    public List<String> getFilteredResHeaders() {
        return filteredResHeaders;
    }

    public void setFilteredResHeaders(List<String> filteredResHeaders) {
        this.filteredResHeaders = filteredResHeaders;
    }

    public List<String> getFilteredReqFields() {
        return filteredReqFields;
    }

    public void setFilteredReqFields(List<String> filteredReqFields) {
        this.filteredReqFields = filteredReqFields;
    }

    public List<String> getFilteredResFields() {
        return filteredResFields;
    }

    public void setFilteredResFields(List<String> filteredResFields) {
        this.filteredResFields = filteredResFields;
    }

    public boolean isSkipAll(){
        return Boolean.valueOf(getSkipAll());
    }

    boolean isRequestHeaderAllowed(final String headerName) {
        return !Iterables.any(filteredReqHeaders, new Predicate<String>() {
            public boolean apply(String arg) {
                return arg.toUpperCase().equals(headerName.toUpperCase());
            }
        });
    }

    boolean isResponseHeaderAllowed(final String headerName){
        return !Iterables.any(filteredResHeaders, new Predicate<String>() {
            public boolean apply(String arg) {
                return arg.toUpperCase().equals(headerName.toUpperCase());
            }
        });
    }

    public Map<String, String> getRequestHeadersToLogging(Map<String, String> httpHeaders) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
            if(this.isRequestHeaderAllowed(entry.getKey())) builder.put(entry);
        }
        return builder.build();
    }

    public Map<String, String>  getResponseHeadersToLogging(Map<String, String> httpHeaders) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
            if(this.isResponseHeaderAllowed(entry.getKey())) builder.put(entry);
        }
        return builder.build();
    }
}
