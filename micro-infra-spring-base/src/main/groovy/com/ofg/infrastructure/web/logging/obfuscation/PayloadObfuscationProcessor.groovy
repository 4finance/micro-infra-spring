package com.ofg.infrastructure.web.logging.obfuscation

class PayloadObfuscationProcessor {

    List<AbstractPayloadObfusctator> obfuscatorList

    PayloadObfuscationProcessor(List<AbstractPayloadObfusctator> obfuscatorList) {
        this.obfuscatorList = obfuscatorList
    }

    String process(String content, Map<String, String> headers, List<String> fieldsToObfuscate){
        String result = this.obfuscatorList.find {
            AbstractPayloadObfusctator obfusctator -> obfusctator.isApplicable(headers)
        }?.process(content, fieldsToObfuscate)
        return (result) ? result : content
    }
}
