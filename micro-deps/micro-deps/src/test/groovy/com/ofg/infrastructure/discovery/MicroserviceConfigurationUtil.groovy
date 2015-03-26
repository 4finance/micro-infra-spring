package com.ofg.infrastructure.discovery

class MicroserviceConfigurationUtil {

    public static final String LOAD_BALANCING_DEPENDENCIES = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": {
                                        "ping" : {
                                            "path": "com/ofg/ping",
                                            "load-balancer": "STICKY"
                                        },
                                        "pong" : {
                                            "path": "com/ofg/pong"
                                        },
                                        "some" : {
                                            "path": "com/ofg/some",
                                            "load-balancer": "random"
                                        },
                                        "another" : {
                                            "path": "com/ofg/another",
                                            "load-balancer": "roundrobin"
                                        },
                                        "another2" : {
                                            "path": "com/ofg/another2",
                                            "load-balancer": "round-robin"
                                        }
                                    }
                                }
                            }
                            """

    public static final String REQUIRED_DEPENDENCY = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": {
                                        "ping" : {
                                            "path": "com/ofg/ping",
                                            "required" : true,
                                            "version": "v123",
                                            "contentTypeTemplate": "application/vnd.mymoid-adapter.v123+json",
                                            "headers": {
                                                "someHeader": "its value",
                                                "anotherHeader": "another value"
                                            }
                                        }
                                    }
                                }
                            }
                            """

    public static final String FLAT_CONFIGURATION = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": {
                                        "ping" : "com/ofg/ping",
                                        "pong" : "com/ofg/pong"
                                    }
                                }
                            }
                            """

    public static final String CONFIGURATION_WITH_PATH_ELEM = """
                            {
                                "pl": {
                                    "this": "com/ofg/service",
                                    "dependencies": {
                                        "ping" : {
                                            "path": "com/ofg/ping"
                                        },
                                        "pong" : {
                                            "path": "com/ofg/pong"
                                        }
                                    }
                                }
                            }
                            """

    public static final String MISSING_THIS_ELEMENT = """
                            {
                                "pl": {
                                    "dependencies": {
                                        "ping" : {
                                            "ping" : {
                                                "path": "com/ofg/ping"
                                            }
                                        }
                                    }
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
