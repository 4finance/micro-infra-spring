import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import org.springframework.cloud.sleuth.Span

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSSZ} | %-5level | %X{${Span.TRACE_ID_NAME}} | %thread | %logger{1} | %m%n"
    }
}

root(INFO, ["CONSOLE"])

logger("com.ofg", DEBUG)
