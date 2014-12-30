package com.ofg.infrastructure.healthcheck


enum CollaboratorStatus {

    UP, DOWN

    static CollaboratorStatus of(boolean isUp) {
        return isUp? UP : DOWN
    }

}