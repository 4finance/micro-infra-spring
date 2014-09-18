package com.ofg.infrastructure.web.resttemplate.fluent.options

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod

/**
 * {@link org.springframework.http.HttpMethod#OPTIONS} method base interface
 *
 * Sample execution for allow:
 *
 * <pre>
 * httpMethodBuilder
 *    .options()
 *        .onUrlFromTemplate("client/{personalId}")
 *        .withVariables("123132123")
 *    .andExecuteFor()
 *    .allow()    
 * </pre>
 *
 * Sample execution for body of type String:
 *
 * <pre>
 * httpMethodBuilder
 *    .options()
 *        .onUrl("client/123123")
 *    .andExecuteFor()
 *        .anObject()
 *        .ofType(String.class)
 * </pre>
 *
 * Sample execution with headers ignoring response:
 *
 * <pre>
 * httpMethodBuilder
 *    .options()
 *    .onUrl("client/123123")
 *    .withHeaders()
 *        .contentTypeJson()
 *    .andExecuteFor()
 *        .ignoringResponse()
 * </pre>
 *
 * @see com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
 * @see HttpMethod
 * @see ResponseReceivingOptionsMethod
 * @see UrlParameterizableOptionsMethod
 */
interface OptionsMethod extends HttpMethod<ResponseReceivingOptionsMethod, UrlParameterizableOptionsMethod> {

}

