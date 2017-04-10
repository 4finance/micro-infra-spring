package com.ofg.infrastructure.web.logging;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RequestDataProvider {

    private Cache<String, HttpData> storage;

    public RequestDataProvider(int timeToLiveMillis) {
        storage = CacheBuilder.newBuilder().expireAfterWrite(timeToLiveMillis, MILLISECONDS).build();
    }

    public void store(String requestId, HttpData data) {
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
