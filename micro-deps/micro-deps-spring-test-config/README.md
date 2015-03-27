Microservice Dependency Manager Spring Test Configuration
=========================================================

## Integration tests

Just extend the __IntegrationSpec__ for Spock (or __IntegrationTest__ for JUnit) and you're ready to go!

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded

## MVC integration tests

Just extend the __MvcIntegrationSpec__ for Spock (or __MvcIntegrationTest__ for JUnit) and you're ready to go!

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded
* Spring MVC test support enabled
* access to application context
* access to web application context

## MVC integration tests with [WireMock](http://wiremock.org/)

Just extend the __MvcWiremockIntegrationSpec__ for Spock (or __MvcWiremockIntegrationTest__ for JUnit) and you're ready to go!

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded
* Spring MVC test support enabled
* __WireMock__ server running
* access to application context
* access to web application context
* access to __stubInteraction()__ method that allows you to stub __WireMock__.
