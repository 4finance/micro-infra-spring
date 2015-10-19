package com.ofg.infrastructure.discovery;

import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Path to dependency as registered in service resolver, like ZooKeeper
 */
public class ServicePath {
    private final String path;

    public ServicePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getLastName() {
        if (path == null) {
            return StringUtils.EMPTY;
        }
        return Iterables.getLast(Arrays.asList(path.split("/")));
    }

    public String getPathToLastName() {
        if (path == null) {
            return StringUtils.EMPTY;
        }
        int lastOccurenceOfSlash = path.lastIndexOf("/");
        if (thereIsNoSlash(lastOccurenceOfSlash)) {
            return path;
        }
        return getPathWithoutLastName(lastOccurenceOfSlash);
    }

    private String getPathWithoutLastName(int lastOccurenceOfSlash) {
        return path.substring(0, lastOccurenceOfSlash);
    }

    private boolean thereIsNoSlash(int lastOccurenceOfSlash) {
        return lastOccurenceOfSlash == -1;
    }

    public String getPathWithStartingSlash() {
        return path.startsWith("/") ? path : "/" + path;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServicePath that = (ServicePath) o;

        return !(path != null ? !path.equals(that.path) : that.path != null);

    }
}
