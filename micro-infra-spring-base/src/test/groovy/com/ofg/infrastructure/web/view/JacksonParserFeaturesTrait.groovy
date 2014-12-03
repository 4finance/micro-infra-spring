package com.ofg.infrastructure.web.view

trait JacksonParserFeaturesTrait {

    static final List<String> JACKSON_PARSER_FEATURES_AS_LIST = ['AUTO_CLOSE_SOURCE',
                                                    'ALLOW_UNQUOTED_FIELD_NAMES',
                                                    'ALLOW_COMMENTS',
                                                    'ALLOW_YAML_COMMENTS',
                                                    'ALLOW_SINGLE_QUOTES',
                                                    'ALLOW_UNQUOTED_CONTROL_CHARS',
                                                    'ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER',
                                                    'ALLOW_NUMERIC_LEADING_ZEROS',
                                                    'ALLOW_NON_NUMERIC_NUMBERS',
                                                    'STRICT_DUPLICATE_DETECTION']

//    //Fails when placed in a trait
//    static final String JACKSON_PARSER_FEATURES_AS_STRING = JACKSON_PARSER_FEATURES_AS_LIST.join(',')
}
