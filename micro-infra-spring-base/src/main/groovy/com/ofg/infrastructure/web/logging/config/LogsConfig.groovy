package com.ofg.infrastructure.web.logging.config

import org.springframework.boot.context.properties.ConfigurationProperties

import java.util.regex.Pattern

@ConfigurationProperties(prefix="logs")
class LogsConfig {

    private final static LogsConfigElement EMPTY_CONFIG_ELEMENT = new LogsConfigElement()

    private List<LogsConfigElement> config = new ArrayList<LogsConfigElement>()

    List<LogsConfigElement> getConfig() {
        return config
    }

    LogsConfigElement getConfigElementByUrlAndMethod(String url, String method){
        LogsConfigElement result
        if(method && url){
            result =  config.find { LogsConfigElement element ->
                method?.toUpperCase().equals(element.method?.toUpperCase()) &&  Pattern.matches(element?.urlPattern, url)}
        }
        return (result) ? result : EMPTY_CONFIG_ELEMENT
    }
}
