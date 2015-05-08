package com.ofg.infrastructure.discovery;

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
