package com.ofg.infrastructure.healthcheck;

public enum CollaboratorStatus {
    UP, DOWN;

    public static CollaboratorStatus of(boolean isUp) {
        return isUp ? UP : DOWN;
    }

}
