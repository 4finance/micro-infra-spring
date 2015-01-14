import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSSZ} | %-5level | %X{correlationId} | %thread | %logger{1} | %m%n"
    }
}

root(INFO, ["CONSOLE"])

logger("com.ofg", DEBUG)
