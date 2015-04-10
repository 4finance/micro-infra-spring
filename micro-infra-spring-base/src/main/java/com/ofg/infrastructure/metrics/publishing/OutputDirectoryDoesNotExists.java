package com.ofg.infrastructure.metrics.publishing;

import java.io.File;

public class OutputDirectoryDoesNotExists extends RuntimeException {

    public OutputDirectoryDoesNotExists(File directory) {
        super(directory.getName() + " directory does not exists");
    }
}
