package com.ofg.infrastructure.web.logging.obfuscation

import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractPayloadObfusctator {

    private final static String CONTENT_TYPE_HEADER_NAME = 'content-type'

    private final ObfuscationFieldStrategy obfuscationFieldStrategy

    protected AbstractPayloadObfusctator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        this.obfuscationFieldStrategy = obfuscationFieldStrategy
    }

    abstract String process(String content, List<String> fieldsToObfuscate)

    final boolean isApplicable(Map<String, String> headers){
        return headers.findAll {Map.Entry<String, String> entry ->
            normalizeString(entry.key.toString()) == CONTENT_TYPE_HEADER_NAME  &&
                    normalizeString(entry.value.toString()).contains(normalizeString(getApplicableContentType()))}.size() > 0
    }

    abstract String getApplicableContentType()

    protected String obfuscate(String value){
        return obfuscationFieldStrategy.obfuscate(value)
    }

    private String normalizeString(String value){
        assert value, 'value should not be empty'
        return value.trim().toLowerCase()
    }

}
