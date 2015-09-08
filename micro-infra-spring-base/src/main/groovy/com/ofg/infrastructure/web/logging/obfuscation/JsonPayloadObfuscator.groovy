package com.ofg.infrastructure.web.logging.obfuscation

import groovy.json.JsonSlurper
import org.springframework.core.annotation.Order

@Order(value = 0)
class JsonPayloadObfuscator extends AbstractPayloadObfusctator{

    JsonPayloadObfuscator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        super(obfuscationFieldStrategy)
    }

    static String cleanFieldsFromJson(String content, List<String> fields){
        String result = null
        if(content){
            try{
                Map nodes = new JsonSlurper().parseText(content)
                if(nodes){
                    cleanRecursive(nodes, fields)
                }
                result = nodes.toString()
            }catch (all){
            }
        }
        return result
    }

    private static void cleanRecursive(Map nodes,  List<String> fields) {
        nodes.each {
            if(isCollectionOrArray(it.value)){
                cleanRecursive(it.value, fields)
            }
            if(fields?.contains(it.key)) {
                nodes.put(it.key, obfuscate(it.value))
            }
        }
    }

    private static void cleanRecursive(List list,  List<String> fields) {
        list.each {
            if(isCollectionOrArray(it)){
                cleanRecursive(it, fields)
            }
        }
    }

    private static boolean isCollectionOrArray(object) {
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
