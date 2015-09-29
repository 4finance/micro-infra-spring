package com.ofg.infrastructure.web.logging.obfuscation

import groovy.json.JsonSlurper
import org.springframework.core.annotation.Order

@Order(value = 0)
class JsonPayloadObfuscator extends AbstractPayloadObfusctator {

    JsonPayloadObfuscator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        super(obfuscationFieldStrategy)
    }

    String cleanFieldsFromJson(String content, List<String> fields){
        if(content && fields.size() > 0){
            try{
                def nodes = new JsonSlurper().parseText(content)
                if(nodes){
                    cleanRecursive(nodes, fields)
                }
                return nodes.toString()
            }catch (all){
                all.printStackTrace()
            }
        }
        return content
    }

    private void cleanRecursive(Map nodes,  List<String> fields) {
        nodes.each {
            if(isCollectionOrArray(it.value)){
                cleanRecursive(it.value, fields)
            }
            if(fields?.contains(it.key)) {
                nodes.put(it.key, obfuscate(it.value.toString()))
            }
        }
    }

    private void cleanRecursive(List list,  List<String> fields) {
        list.each {
            if(isCollectionOrArray(it)){
                cleanRecursive(it, fields)
            }
        }
    }

    private boolean isCollectionOrArray(object) {
        [Map, Object[], List].any { it.isAssignableFrom(object.getClass()) }
    }

    @Override
    String process(String content, List<String> fieldsToObfuscate) {
        cleanFieldsFromJson(content, fieldsToObfuscate)
    }

    @Override
    String getApplicableContentType() {
        return 'application/json'
    }
}
