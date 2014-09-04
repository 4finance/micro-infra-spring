package com.ofg.infrastructure.web.resttemplate.fluent.put

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod

/**
 * {@link org.springframework.http.HttpMethod#POST} HTTP method base interface
 *
 * Sample execution for location:
 * 
 * <pre>
 * httpMethodBuilder
 *    .put()
 *        .onUrlFromTemplate("client/{personalId}")
 *        .withVariables("123132123")
 *    .body("{'name':'smith'}")
 *    .forLocation()    
 * </pre>
 * 
 * Sample execution for body of type String:
 *
 * <pre>
 * httpMethodBuilder
 *    .put()
 *        .onUrl("client/123123")
 *    .body("{'name':'smith'}")
 *    .andExecuteFor()
 *        .anObject()
 *        .ofType(String.class)
 * </pre>
 * 
 * 
 * Sample execution with headers ignoring response:
 *
 * <pre>
 * httpMethodBuilder
 *    .get()
 *    .onUrl("client/123123")
 *    .withHeaders()
 *        .contentTypeJson()
 *    .andExecuteFor()
 *        .ignoringResponse()
 * </pre>
 * 
 * @see com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
 * @see HttpMethod
 * @see RequestHavingPutMethod
 * @see UrlParameterizablePutMethod
 */
interface PutMethod extends HttpMethod<RequestHavingPutMethod, UrlParameterizablePutMethod> {

}
