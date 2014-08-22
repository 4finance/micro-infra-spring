package com.ofg.infrastructure.web.resttemplate.fluent.delete
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.DELETE

class DeleteExecuteForResponseTypeRelated extends ResponseTypeRelatedRequestsExecutor<Object> {

    DeleteExecuteForResponseTypeRelated(Map params, RestTemplate restTemplate) {
        super(params, restTemplate, Object)
    }

    @Override
    HttpMethod getHttpMethod() {
        return DELETE
    }

}
