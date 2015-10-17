package com.ofg.infrastructure.web.logging.obfuscation;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Map;

public class PayloadObfuscationProcessor {

    private final List<AbstractPayloadObfuscator> obfuscatorList;

    public PayloadObfuscationProcessor(List<AbstractPayloadObfuscator> obfuscatorList) {
        this.obfuscatorList = obfuscatorList;
    }

    public String process(String content, final Map<String, String> headers, List<String> fieldsToObfuscate) {
        Iterable<AbstractPayloadObfuscator> obfuscators =  Iterables.filter(obfuscatorList, new Predicate<AbstractPayloadObfuscator>() {
            public boolean apply(AbstractPayloadObfuscator obfuscator) {
                return obfuscator.isApplicable(headers);
            }
        });
        if(obfuscators == null || !obfuscators.iterator().hasNext()){
            return content;
        }else{
            for(AbstractPayloadObfuscator obfuscator : obfuscators){
                content = obfuscator.process(content.replace("\n", ""), fieldsToObfuscate);
            }
            return content;
        }
    }
}
