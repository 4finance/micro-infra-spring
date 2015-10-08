package com.ofg.infrastructure.web.logging.obfuscation;

public class FieldReplacementStrategy implements ObfuscationFieldStrategy {

    @Override
    public String obfuscate(String fieldToObfuscate) {
        return "REMOVED";
    }
}
