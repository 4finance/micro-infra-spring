package com.ofg.infrastructure.web.logging.obfuscation;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public abstract class AbstractPayloadObfuscator {

    private final static String CONTENT_TYPE_HEADER_NAME = "content-type";

    private final ObfuscationFieldStrategy obfuscationFieldStrategy;

    protected AbstractPayloadObfuscator(ObfuscationFieldStrategy obfuscationFieldStrategy) {
        this.obfuscationFieldStrategy = obfuscationFieldStrategy;
    }

    public abstract String process(String content, List<String> fieldsToObfuscate);

    public final boolean isApplicable(Map<String, String> headers){
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if(normalizeString(entry.getKey()).equals(CONTENT_TYPE_HEADER_NAME)  &&
                    normalizeString(entry.getValue()).contains(normalizeString(getApplicableContentType()))) {
            return true;
            }
        }
        return false;
    }

    public abstract String getApplicableContentType();

    protected String obfuscate(String value){
        return obfuscationFieldStrategy.obfuscate(value);
    }

    private String normalizeString(String value){
        Preconditions.checkArgument(StringUtils.isNotEmpty(value), "value should not be empty");
        return value.trim().toLowerCase();
    }

}
