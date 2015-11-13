package com.ofg.infrastructure.property.decrypt

import com.ofg.infrastructure.property.AbstractIntegrationSpec
import org.codehaus.groovy.runtime.StackTraceUtils
import org.springframework.context.ConfigurableApplicationContext

class DecryptingPropertyExtendedSpec extends AbstractIntegrationSpec {

    private static final String ENCRYPTED_PROPERTY =
            "propertyKey:{cipher}bb3336c80dffc7a6d13faea47cf1920cf391a03319249d8a6e38c289d1de7232"

    def "should fail when wrong encryption key is provided and there are encrypted passwords"() {
        given:
            setEncryptKey("wrongKey")
        when:
            def context = contextWithProperties(ENCRYPTED_PROPERTY)
        then:
            def e = thrown(IllegalStateException)
            e.message?.contains("propertyKey")
        cleanup:
            context?.close()
    }

    def "should fail when encryption key is not provided and there are encrypted passwords"() {
        when:
            def context = contextWithProperties(ENCRYPTED_PROPERTY)
        then:
            def e = thrown(IllegalStateException)
            StackTraceUtils.extractRootCause(e)?.class == UnsupportedOperationException
        cleanup:
            context?.close()
    }

    def "should not fail when encryption key is not provided and there are no encrypted passwords"() {
        when:
            def context = contextWithProperties("normal.prop=normal.prop.value")
        then:
            context.environment.getProperty("normal.prop") == "normal.prop.value"
        cleanup:
            context?.close()
    }

    private ConfigurableApplicationContext contextWithProperties(String properties) {
        return applicationBuilderWithSources(DecryptingPropertyTestApp)
                .properties(properties)
                .profiles('ok')
                .run()
    }
}
