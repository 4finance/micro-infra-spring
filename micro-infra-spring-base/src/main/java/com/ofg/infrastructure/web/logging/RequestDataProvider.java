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

    public void store(String traceId, HttpData data) {
        Preconditions.checkNotNull(traceId, "traceId cannot be null");
        Preconditions.checkArgument(!traceId.isEmpty(), "traceId cannot be empty");
        storage.put(traceId, data);
    }
    
    public HttpData retrieve(String traceId) {
        return storage.getIfPresent(traceId);
    }

    public void remove(String traceId) {
        storage.invalidate(traceId);
    }
    
    public long size() {
        return storage.size();
    }
}
