package com.ofg.infrastructure.property.decrypt

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.security.crypto.encrypt.TextEncryptor

/**
 * Simple encryption test util.
 *
 * Note. The class shouldn't be reused as it does not do required clean up and can pollute JVM
 */
@PackageScope
@CompileStatic
class EncryptorTestUtil {

    static void main(String[] args) {
        EncryptionInputData inputData = prepareImputData(args)
        println "Encrypted text: ${decryptAndReturn(inputData)}"
    }

    private static String decryptAndReturn(EncryptionInputData inputData) {
        System.setProperty("encrypt.key", inputData.encryptKey)
        System.setProperty("APP_ENV", "prod")
        System.setProperty("countryCode", "pl")

        def context = new SpringApplicationBuilder(DecryptingPropertyTestApp).web(false).showBanner(false).run()
        def encryptor = context.getBean(TextEncryptor)

        encryptor.encrypt(inputData.textToEncrypt)
    }

    private static EncryptionInputData prepareImputData(String[] args) {
        if (args.length == 2) {
            new EncryptionInputData(args[0], args[1])
        } else {
            createUtilUsingParametersFromConsole()
        }
    }

    private static EncryptionInputData createUtilUsingParametersFromConsole() {
        def reader = new BufferedReader(new InputStreamReader(System.in))   //System.console() returns null from Idea

        print "Enter encryption key: "
        def key = reader.readLine()

        print "Enter text to encrypt: "
        def text = reader.readLine()

        new EncryptionInputData(key, text)
    }

    @ToString
    @TupleConstructor
    private static class EncryptionInputData {
        String encryptKey
        String textToEncrypt
    }
}
