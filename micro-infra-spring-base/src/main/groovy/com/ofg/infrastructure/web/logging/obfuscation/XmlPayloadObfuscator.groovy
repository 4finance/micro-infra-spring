package com.ofg.infrastructure.web.logging.obfuscation

import com.google.common.base.Preconditions
import groovy.util.slurpersupport.NodeChild
import groovy.xml.StreamingMarkupBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order

@Order(value = 1)
class XmlPayloadObfuscator extends AbstractPayloadObfuscator {

    private static final Logger log = LoggerFactory.getLogger(XmlPayloadObfuscator.class);

    XmlPayloadObfuscator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        super(obfuscationFieldStrategy)
    }

    String cleanFieldsFromXml(String content, List<String> fields) {
        try {
            Preconditions.checkArgument(content != null, "Content is {}", content)
            def rootNode = new XmlSlurper().parseText(content)
            rootNode.'**'.findAll {
                fields?.contains(it.name())
            }.each { NodeChild node -> node.replaceBody(obfuscate(node.toString())) }
            return new StreamingMarkupBuilder().bindNode(rootNode).toString()
        } catch (Exception ex) {
            log.debug("Error with [$content]", ex)
        }
        return content
    }

    @Override
    String process(String content, List<String> fieldsToObfuscate) {
        if (content && fieldsToObfuscate) {
            return cleanFieldsFromXml(content, fieldsToObfuscate)
        } else {
            return content
        }
    }

    @Override
    String getApplicableContentType() {
        return 'application/xml'
    }
}
