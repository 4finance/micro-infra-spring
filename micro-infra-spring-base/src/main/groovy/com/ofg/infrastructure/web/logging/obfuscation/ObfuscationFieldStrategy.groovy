package com.ofg.infrastructure.web.logging.obfuscation

interface ObfuscationFieldStrategy {
    String obfuscate(String fieldToObfuscate)
}