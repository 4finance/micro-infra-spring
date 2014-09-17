package com.ofg.infrastructure.web.resttemplate.fluent.delete
import com.ofg.infrastructure.web.resttemplate.fluent.common.response.executor.ResponseTypeRelatedRequestsExecutor
import groovy.transform.CompileStatic
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestOperations

import static org.springframework.http.HttpMethod.DELETE
/**
 * Implementation of method execution for the {@link HttpMethod#DELETE} method.
 */
@CompileStatic
class DeleteExecuteForResponseTypeRelated extends ResponseTypeRelatedRequestsExecutor<Object> {

    DeleteExecuteForResponseTypeRelated(Map params, RestOperations restOperations) {
        super(params, restOperations, Object)
    }

    @Override
    HttpMethod getHttpMethod() {
        return DELETE
    }

}
