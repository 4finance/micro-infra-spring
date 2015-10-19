package com.ofg.infrastructure.web.logging.config;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ofg.infrastructure.web.WebConsts.DEFAULT_SKIP_PATTERN_STRING;

public class LogsConfigElement {

    final static LogsConfigElement EMPTY_CONFIG_ELEMENT = new LogsConfigElement();
    final static LogsConfigElement DEFAULT_CONFIG_ELEMENT = defaultSkipConfigElement();
    private String skipAll = Boolean.FALSE.toString();
    private String methodPattern = ".*";
    private String urlPattern = StringUtils.EMPTY;
    private List<String> filteredReqHeaders = new ArrayList<>();
    private List<String> filteredResHeaders = new ArrayList<>();
    private List<String> filteredReqFields = new ArrayList<>();
    private List<String> filteredResFields = new ArrayList<>();

    public String getSkipAll() {
        return skipAll;
    }

    public void setSkipAll(String skipAll) {
        this.skipAll = skipAll;
    }

    public String getMethodPattern() {
        return methodPattern;
    }

    public void setMethodPattern(String methodPattern) {
        this.methodPattern = methodPattern;
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

    private static LogsConfigElement defaultSkipConfigElement(){
        LogsConfigElement defaultSkipConfig = new LogsConfigElement();
        defaultSkipConfig.setSkipAll(Boolean.TRUE.toString());
        defaultSkipConfig.setMethodPattern(".*");
        defaultSkipConfig.setUrlPattern(DEFAULT_SKIP_PATTERN_STRING);
        return defaultSkipConfig;
    }
}
