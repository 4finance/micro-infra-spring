package com.ofg.infrastructure.correlationid;

import java.util.UUID;

public class UuidGenerator {

    public String create() {
        return UUID.randomUUID().toString();
    }

}
