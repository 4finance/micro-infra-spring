package com.ofg.infrastructure.discovery;

import java.util.Map;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

public class IgnorePayloadInstanceSerializer<T> extends JsonInstanceSerializer {

    private final ObjectMapper mapper;
    private final Class<T> payloadClass;
    private final JavaType type;

    public IgnorePayloadInstanceSerializer(Class<T> payloadClass) {
        super(payloadClass);

        this.payloadClass = payloadClass;
        this.mapper = new ObjectMapper();
        this.type = mapper.getTypeFactory().constructType(ServiceInstance.class);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public ServiceInstance<T> deserialize(byte[] bytes) throws Exception {
        String serviceInstanceDataWithoutPayload = nullifyPayloadContent(bytes);
        ServiceInstance rawServiceInstance = mapper.readValue(serviceInstanceDataWithoutPayload, type);
        payloadClass.cast(rawServiceInstance.getPayload()); // just to verify that it's the correct type
        return (ServiceInstance<T>) rawServiceInstance;
    }

    private String nullifyPayloadContent(byte[] bytes) throws java.io.IOException {
        Map map = mapper.readValue(bytes, Map.class);
        map.put("payload", null);
        return mapper.writeValueAsString(map);
    }

}
