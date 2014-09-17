package com.ofg.infrastructure.web.resttemplate.fluent.get

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod

/**
 * {@link org.springframework.http.HttpMethod#GET} method base interface
 *
 * Sample execution for object of type BigDecimal
 *
 * <pre>
 * httpMethodBuilder
 *    .get()
 *        .onUrlFromTemplate("client/{personalId}")
 *        .withVariables("123132123")
 *    .andExecuteFor()
 *        .anObject()
 *        .ofType(BigDecimal)   
 * </pre>
 *
 * Sample execution for body of type {@link org.springframework.http.ResponseEntity}:
 *
 * <pre>
 * httpMethodBuilder
 *    .get()
 *        .onUrl("client/123123")
 *    .andExecuteFor()
 *        .aResponseEntity()
 *        .ofType(BigDecimal)
 * </pre>
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
 * @see ResponseReceivingGetMethod
 * @see UrlParameterizableGetMethod
 */
interface GetMethod extends HttpMethod<ResponseReceivingGetMethod, UrlParameterizableGetMethod> {

}
