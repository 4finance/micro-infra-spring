package com.ofg.infrastructure.web.resttemplate.fluent.head

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod

/**
 * {@link org.springframework.http.HttpMethod#HEAD} method base interface
 *
 * Sample execution for {@link org.springframework.http.HttpHeaders}:
 *
 * <pre>
 * httpMethodBuilder
 *    .head()
 *        .onUrlFromTemplate("client/{personalId}")
 *        .withVariables("123132123")
 *    .andExecuteFor()
 *    .httpHeaders()    
 * </pre>
 *
 * Sample execution for a {@link org.springframework.http.ResponseEntity}:
 *
 * <pre>
 * httpMethodBuilder
 *    .head()
 *        .onUrl("client/123123")
 *    .andExecuteFor()
 *    .aResponseEntity()
 * </pre>
 *
 * Sample execution with headers ignoring response:
 *
 * <pre>
 * httpMethodBuilder
 *    .head()
 *    .onUrl("client/123123")
 *    .withHeaders()
 *        .contentTypeJson()
 *    .andExecuteFor()
 *        .ignoringResponse()
 * </pre>
 *
 * @see com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
 * @see HttpMethod
 * @see ResponseReceivingHeadMethod
 * @see UrlParameterizableHeadMethod
 */
interface HeadMethod extends HttpMethod<ResponseReceivingHeadMethod, UrlParameterizableHeadMethod> {

}
