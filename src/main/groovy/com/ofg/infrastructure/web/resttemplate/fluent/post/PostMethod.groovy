package com.ofg.infrastructure.web.resttemplate.fluent.post

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod

/**
 * {@link org.springframework.http.HttpMethod#POST} HTTP method base interface
 *
 * Sample execution for location:
 *
 * <pre>
 * httpMethodBuilder
 *    .post()
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
 *    .post()
 *        .onUrl("client/123123")
 *    .body("{'name':'smith'}")
 *    .andExecuteFor()
 *        .anObject()
 *        .ofType(String.class)
 * </pre>
 *
 * Sample execution with headers ignoring response:
 *
 * <pre>
 * httpMethodBuilder
 *    .post()
 *    .onUrl("client/123123")
 *    .body("{'name':'smith'")
 *    .withHeaders()
 *        .contentTypeJson()
 *    .andExecuteFor()
 *        .ignoringResponse()
 * </pre>
 *
 * @see com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
 * @see HttpMethod
 * @see RequestHavingPostMethod
 * @see UrlParameterizablePostMethod
 */
interface PostMethod extends HttpMethod<RequestHavingPostMethod, UrlParameterizablePostMethod> {

}
