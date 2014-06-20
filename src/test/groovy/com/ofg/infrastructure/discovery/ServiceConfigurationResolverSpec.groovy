package com.ofg.infrastructure.discovery

import spock.lang.Specification

class ServiceConfigurationResolverSpec extends Specification {
    
    def "should parse proper json"() {
        given:
            String json = """
                            {
                                "microservice": {
                                    "this": "pl/payments/20",
                                    "dependencies": {
                                        "clients": "pl/clients/10",
                                        "loans": "pl/loans/15"
                                    }
                                }
                            }
                            """
        when:
            def resolver = new ServiceConfigurationResolver(json)    
        then:
            resolver.basePath == "microservice"
            resolver.microserviceName == "pl/payments/20"
            resolver.dependencies == ["clients": "pl/clients/10",
                                      "loans": "pl/loans/15"]
    }
    
    def "should fail when 'this' element is not present "() {
        given:
            String json = """
                            {
                                "microservice": {
                                    "dependencies": {
                                        "clients": "pl/clients/10",
                                        "loans": "pl/loans/15"
                                    }
                                }
                            }
                            """
        when:
            new ServiceConfigurationResolver(json)
        then:
            thrown(BadConfigurationException)
    }
    
    def "should fail when 'dependencies' element is not present "() {
        given:
            String json = """
                            {
                                "microservice": {
                                    "this": "pl/payments/20"
                                }
                            }
                            """
        when:
            new ServiceConfigurationResolver(json)
        then:
            thrown(BadConfigurationException)
    }
    
    def "should fail on multiple root elements"() {
        given:
            String json = """
                            {
                                "microservice": {
                                    "this": "pl/payments/20",
                                    "dependencies": {
                                        "clients": "pl/clients/10",
                                        "loans": "pl/loans/15"
                                    }
                                },
                                "everything": 1
                            }
                            """
        when:
            new ServiceConfigurationResolver(json)
        then:
            thrown(BadConfigurationException)
    }
}
