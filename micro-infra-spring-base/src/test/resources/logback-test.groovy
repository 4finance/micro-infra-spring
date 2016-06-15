import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import org.springframework.cloud.sleuth.Span

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSSZ} | %-5level | %X{${Span.TRACE_ID_NAME}} | %thread | %logger{1} | %m%n"
    }
}

//TODO: Fix console pollution during tests (it should be only logged to a test log file, but also available in Gradle test reports)
root(WARN, ["CONSOLE"])

logger("com", INFO)
logger("org", INFO)
logger("com.ofg.infrastructure.web.resttemplate.custom.LoggingResponseExtractorWrapper", DEBUG)
logger("com.ofg.infrastructure.web.logging.RequestResponseLogger", DEBUG)