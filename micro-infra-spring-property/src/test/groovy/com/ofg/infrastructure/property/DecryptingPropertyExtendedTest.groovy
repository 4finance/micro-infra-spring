package com.ofg.infrastructure.property

import org.springframework.boot.builder.SpringApplicationBuilder
import spock.lang.Ignore

class DecryptingPropertyExtendedTest extends AbstractIntegrationTest {

    def "should fail when encryption key is not provided and there are encrypted passwords"() {
        given:
            System.setProperty("encrypt.key", "wrongKey")
        when:
            def context = new SpringApplicationBuilder(DecryptingPropertyTestApp)
                    .web(false)
                    .showBanner(false)
                    .properties("enc:{cipher}bb3336c80dffc7a6d13faea47cf1920cf391a03319249d8a6e38c289d1de7232")
                    .run()
        then:
            thrown(IllegalStateException)
        cleanup:
            context?.close()
    }

    @Ignore("Currently fails with NoSuchBeanDefinitionException: TextEncryptor")
    def "should not fail when encryption key is not provided and there are no encrypted passwords"() {
        when:
            def context = new SpringApplicationBuilder(DecryptingPropertyTestApp)
                    .web(false)
                    .showBanner(false)
                    .properties("normal.prop=normal.prop.value")
                    .run()
        then:
            context.environment.getProperty("normal.prop") == "normal.prop.value"
        cleanup:
            context?.close()
    }
}
