package com.ofg.infrastructure.web.logging.obfuscation

class FieldReplacementStrategy implements ObfuscationFieldStrategy{
    @Override
    String obfuscate(String fieldToObfuscate) {
        return 'REMOVED'
    }
}
