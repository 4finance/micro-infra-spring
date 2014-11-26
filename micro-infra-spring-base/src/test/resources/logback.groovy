import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSSZ} | %-5level | %X{correlationId} | %thread | %logger{1} | %m%n"
    }
}

//TODO: Fix console pollution during tests (it should be only logged to a test log file, but also available in Gradle test reports)
root(INFO, ["CONSOLE"])

logger("com.nurkiewicz.asyncretry", ALL)
logger("com.ofg", DEBUG)
logger("org.springframework.cloud.config.client", DEBUG)
