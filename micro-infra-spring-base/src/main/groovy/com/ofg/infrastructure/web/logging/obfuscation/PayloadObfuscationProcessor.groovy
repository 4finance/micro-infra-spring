package com.ofg.infrastructure.web.logging.obfuscation

class PayloadObfuscationProcessor {

    private final List<AbstractPayloadObfusctator> obfuscatorList

    PayloadObfuscationProcessor() {
        this.obfuscatorList = Collections.emptyList()
    }

    PayloadObfuscationProcessor(List<AbstractPayloadObfusctator> obfuscatorList) {
        this.obfuscatorList = obfuscatorList
    }

    String process(String content, Map<String, String> headers, List<String> fieldsToObfuscate){
        String result = this.obfuscatorList?.find {
            AbstractPayloadObfusctator obfuscator -> obfuscator.isApplicable(headers)
        }?.process(content.replace('\n',''), fieldsToObfuscate)
        return (result) ? result : content
    }
}
