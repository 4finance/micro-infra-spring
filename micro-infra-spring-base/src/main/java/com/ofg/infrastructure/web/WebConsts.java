package com.ofg.infrastructure.web;

import java.util.regex.Pattern;

public class WebConsts {
    private WebConsts() {}
    public static final String DEFAULT_SKIP_PATTERN_STRING = "/api-docs.*|/fonts.*|/autoconfig|/configprops|/dump|/info|/metrics.*|/mappings|/trace|/swagger.*|.*\\.png|.*\\.ico|.*\\.css|.*\\.js|.*\\.html";
    public static final Pattern DEFAULT_SKIP_PATTERN = Pattern.compile(DEFAULT_SKIP_PATTERN_STRING);
}
