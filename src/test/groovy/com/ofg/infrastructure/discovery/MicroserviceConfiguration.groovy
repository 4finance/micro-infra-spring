package com.ofg.infrastructure.discovery

class MicroserviceConfiguration {

    public static final String REQUIRED_DEPENDENCY = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": [{
                                        "name": "ping",
                                        "path": "com/ofg/ping",
                                        "required" : true,
                                        "version": "v123",
                                        "contentTypeTemplate": "application/vnd.mymoid-adapter.v123+json",
                                        "headers": {
                                            "someHeader": "its value",
                                            "anotherHeader": "another value"
                                        }
                                    }]
                                }
                            }
                            """

    public static final String VALID_CONFIGURATION = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": [{
                                        "name" : "ping",
                                        "path": "com/ofg/ping"
                                    }, {
                                        "name" : "pong",
                                        "path": "com/ofg/pong"
                                    }]
                                }
                            }
                            """

    public static final String MISSING_THIS_ELEMENT = """
                            {
                                "pl": {
                                    "dependencies": [{
                                        "name" : "ping",
                                        "path": "com/ofg/ping"
                                    }]
                                }
                            }
                            """

    public static final String INVALID_DEPENDENCIES_ELEMENT = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": "no"
                                }
                            }
                            """

    public static final String MULTIPLE_ROOT_ELEMENTS = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                },
                                "lv": {
                                    "this": "com/ofg/ping",
                                }
                            }
                            """

    public static final String ONLY_REQUIRED_ELEMENTS = """
                            {
                                "pl": {
                                    "this": "com/ofg/service"
                                    }
                                }
                            }
                            """
}
