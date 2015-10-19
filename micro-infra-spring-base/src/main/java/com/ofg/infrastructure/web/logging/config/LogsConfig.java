package com.ofg.infrastructure.web.logging.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.ofg.infrastructure.web.logging.config.LogsConfigElement.DEFAULT_CONFIG_ELEMENT;
import static com.ofg.infrastructure.web.logging.config.LogsConfigElement.EMPTY_CONFIG_ELEMENT;

@ConfigurationProperties(prefix="logs")
public class LogsConfig {

    private List<LogsConfigElement> config = new ArrayList<>();

    public LogsConfig() {

        config.add(DEFAULT_CONFIG_ELEMENT);
    }

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
                return Pattern.matches(arg.getMethodPattern(), method) && Pattern.matches(arg.getUrlPattern(), url);
            }
        }, EMPTY_CONFIG_ELEMENT);
    }
}
