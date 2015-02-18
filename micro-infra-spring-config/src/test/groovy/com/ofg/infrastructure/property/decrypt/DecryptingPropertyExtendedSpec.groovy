package com.ofg.infrastructure.property.decrypt

import com.ofg.infrastructure.property.AbstractIntegrationSpec
import org.codehaus.groovy.runtime.StackTraceUtils
import org.springframework.boot.builder.SpringApplicationBuilder

class DecryptingPropertyExtendedSpec extends AbstractIntegrationSpec {

    def "should fail when wrong encryption key is provided and there are encrypted passwords"() {
        given:
            System.setProperty("encrypt.key", "wrongKey")
        when:
            def context = defaultTestSpringApplicationBuilder()
                    .properties("propertyKey:{cipher}bb3336c80dffc7a6d13faea47cf1920cf391a03319249d8a6e38c289d1de7232")
                    .run()
        then:
            def e = thrown(IllegalStateException)
            e.message?.contains("propertyKey")
        cleanup:
            context?.close()
    }

    def "should fail when encryption key is not provided and there are encrypted passwords"() {
        when:
            def context = defaultTestSpringApplicationBuilder()
                    .properties("propertyKey:{cipher}bb3336c80dffc7a6d13faea47cf1920cf391a03319249d8a6e38c289d1de7232")
                    .run()
        then:
            def e = thrown(IllegalStateException)
            StackTraceUtils.extractRootCause(e)?.class == UnsupportedOperationException
        cleanup:
            context?.close()
    }

    def "should not fail when encryption key is not provided and there are no encrypted passwords"() {
        when:
            def context = defaultTestSpringApplicationBuilder()
                    .properties("normal.prop=normal.prop.value")
                    .run()
        then:
            context.environment.getProperty("normal.prop") == "normal.prop.value"
        cleanup:
            context?.close()
    }

    private static SpringApplicationBuilder defaultTestSpringApplicationBuilder() {
        new SpringApplicationBuilder(DecryptingPropertyTestApp)
                .web(false)
                .showBanner(false)
    }
}
