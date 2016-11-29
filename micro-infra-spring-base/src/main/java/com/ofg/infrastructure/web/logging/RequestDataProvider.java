package com.ofg.infrastructure.web.logging;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.servo.util.Preconditions;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RequestDataProvider {

    private Cache<String, HttpData> storage;

    public RequestDataProvider(int timeToLiveMillis) {
        storage = CacheBuilder.newBuilder().expireAfterWrite(timeToLiveMillis, MILLISECONDS).build();
    }

    public void store(String requestId, HttpData data) {
        Preconditions.checkNotNull(requestId, "requestId cannot be null");
        Preconditions.checkArgument(!requestId.isEmpty(), "requestId cannot be empty");
        storage.put(requestId, data);
    }
    
    public HttpData retrieve(String requestId) {
        return storage.getIfPresent(requestId);
    }

    public void remove(String requestId) {
        storage.invalidate(requestId);
    }
    
    public long size() {
        return storage.size();
    }
}
