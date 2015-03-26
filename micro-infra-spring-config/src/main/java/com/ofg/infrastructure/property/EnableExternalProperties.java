package com.ofg.infrastructure.property;

import org.springframework.context.annotation.Import;

@Import(ExternalPropertiesConfiguration.class)
public @interface EnableExternalProperties {
}
