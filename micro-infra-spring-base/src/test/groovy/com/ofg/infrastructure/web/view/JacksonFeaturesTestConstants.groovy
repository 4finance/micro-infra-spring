package com.ofg.infrastructure.web.view

import groovy.transform.CompileStatic

@CompileStatic
interface JacksonFeaturesTestConstants {

    public static final List<String> JACKSON_GENERATOR_FEATURES_AS_LIST = ['AUTO_CLOSE_JSON_CONTENT',
                                                                           'AUTO_CLOSE_TARGET',
                                                                           'ESCAPE_NON_ASCII',
                                                                           'FLUSH_PASSED_TO_STREAM',
                                                                           'QUOTE_FIELD_NAMES',
                                                                           'QUOTE_NON_NUMERIC_NUMBERS',
                                                                           'STRICT_DUPLICATE_DETECTION',
                                                                           'WRITE_BIGDECIMAL_AS_PLAIN',
                                                                           'WRITE_NUMBERS_AS_STRINGS']

    public static final List<String> JACKSON_PARSER_FEATURES_AS_LIST = ['AUTO_CLOSE_SOURCE',
                                                                        'ALLOW_UNQUOTED_FIELD_NAMES',
                                                                        'ALLOW_COMMENTS',
                                                                        'ALLOW_YAML_COMMENTS',
                                                                        'ALLOW_SINGLE_QUOTES',
                                                                        'ALLOW_UNQUOTED_CONTROL_CHARS',
                                                                        'ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER',
                                                                        'ALLOW_NUMERIC_LEADING_ZEROS',
                                                                        'ALLOW_NON_NUMERIC_NUMBERS',
                                                                        'STRICT_DUPLICATE_DETECTION']
}
