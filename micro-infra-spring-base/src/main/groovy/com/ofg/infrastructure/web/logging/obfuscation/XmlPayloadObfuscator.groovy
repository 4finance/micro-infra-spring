package com.ofg.infrastructure.web.logging.obfuscation

import groovy.util.slurpersupport.NodeChild
import groovy.xml.StreamingMarkupBuilder
import org.springframework.core.annotation.Order

@Order(value = 1)
class XmlPayloadObfuscator extends AbstractPayloadObfusctator{

    XmlPayloadObfuscator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        super(obfuscationFieldStrategy)
    }

    static String cleanFieldsFromXml(String content, List<String> fields){
        String result = null
        if(content){
            try{
                def rootNode = new XmlSlurper().parseText(content)
                rootNode.'**'.findAll { fields.contains(it.name()) }.each {NodeChild node -> node.replaceBody(obfuscate(node.toString())) }
                result = new StreamingMarkupBuilder().bindNode(rootNode).toString()
            }catch (all){
            }
        }
        return result
    }

    @Override
    String process(String content, List<String> fieldsToObfuscate) {
        return cleanFieldsFromXml(content, fieldsToObfuscate)
    }

    @Override
    String getApplicableContentType() {
        return 'application/xml'
    }
}
