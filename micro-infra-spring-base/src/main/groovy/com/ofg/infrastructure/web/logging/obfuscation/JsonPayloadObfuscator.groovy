package com.ofg.infrastructure.web.logging.obfuscation

import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order

@Order(value = 0)
class JsonPayloadObfuscator extends AbstractPayloadObfuscator {

    private static final Logger log = LoggerFactory.getLogger(JsonPayloadObfuscator.class);

    JsonPayloadObfuscator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        super(obfuscationFieldStrategy)
    }

    String cleanFieldsFromJson(String content, List<String> fields) {
        try {
            def nodes = new JsonSlurper().parseText(content)
            if (nodes) {
                cleanRecursive(nodes, fields)
            }
            return nodes.toString()
        } catch (Exception ex) {
            log.debug("Error with [$content]", ex)
        }
        return content
    }

    private void cleanRecursive(Map nodes, List<String> fields) {
        nodes.each {
            if (isCollectionOrArray(it.value)) {
                cleanRecursive(it.value, fields)
            }
            if (fields?.contains(it.key)) {
                nodes.put(it.key, obfuscate(it.value.toString()))
            }
        }
    }

    private void cleanRecursive(List list, List<String> fields) {
        list.each {
            if (isCollectionOrArray(it)) {
                cleanRecursive(it, fields)
            }
        }
    }

    private boolean isCollectionOrArray(object) {
        [Map, Object[], List, Set].any { it.isAssignableFrom(object.getClass()) }
    }

    @Override
    String process(String content, List<String> fieldsToObfuscate) {
        if (content && fieldsToObfuscate) {
            return cleanFieldsFromJson(content, fieldsToObfuscate)
        } else {
            return content
        }
    }

    @Override
    String getApplicableContentType() {
        return 'application/json'
    }
}
