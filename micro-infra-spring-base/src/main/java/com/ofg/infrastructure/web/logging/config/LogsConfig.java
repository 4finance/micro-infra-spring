package com.ofg.infrastructure.web.logging.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ConfigurationProperties(prefix="logs")
public class LogsConfig {

    private final static LogsConfigElement EMPTY_CONFIG_ELEMENT = new LogsConfigElement();
    private List<LogsConfigElement> config = new ArrayList<>();

    public List<LogsConfigElement> getConfig() {
        return config;
    }

    public void setConfig(List<LogsConfigElement> config) {
        this.config = config;
    }

    public LogsConfigElement getConfigElementByUrlAndMethod(final String url, final String method) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(method), "Method can not be empty");
        Preconditions.checkArgument(StringUtils.isNotEmpty(url), "Url can not be empty");
        return Iterables.find(config, new Predicate<LogsConfigElement>() {
            public boolean apply(LogsConfigElement arg) {
                return method.toUpperCase().equals(arg.getMethod().toUpperCase()) && Pattern.matches(arg.getUrlPattern(), url);
            }
        }, EMPTY_CONFIG_ELEMENT);
    }
}
