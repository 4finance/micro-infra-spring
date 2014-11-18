package com.ofg.infrastructure.property.decrypt

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.security.crypto.encrypt.TextEncryptor

class EncryptorTestUtil {

    static void main(String[] args) {
        System.setProperty("encrypt.key", "eKey")
        System.setProperty("APP_ENV", "prod")
        System.setProperty("countryCode", "pl")
        def context = new SpringApplicationBuilder(DecryptingPropertyTestApp).web(false).run()
        def encryptor = context.getBean(TextEncryptor)
        println "Encrypted: ${encryptor.encrypt("value to encrypt")}"
    }
}
