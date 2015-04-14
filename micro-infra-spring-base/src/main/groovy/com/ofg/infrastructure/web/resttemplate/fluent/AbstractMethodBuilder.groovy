package com.ofg.infrastructure.web.resttemplate.fluent

import org.apache.commons.lang.StringUtils

class AbstractMethodBuilder {
    protected final Map params = [:]

    protected def replaceFirstPlaceholderWithValue() {
        final String template = params.urlTemplate
        final String skippedFirstPlaceholder = StringUtils.substringAfter(template, '}')
        final Object[] variables = params.urlVariablesArray
        params.urlTemplate = variables.head() + skippedFirstPlaceholder
        params.urlVariablesArray = variables.tail()
    }

    protected boolean templateStartsWithPlaceholder() {
        return params.urlTemplate.startsWith('{')
    }
}
