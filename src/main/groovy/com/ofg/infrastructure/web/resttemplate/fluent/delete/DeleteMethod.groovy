package com.ofg.infrastructure.web.resttemplate.fluent.delete

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.HttpMethod

/**
 * {@link org.springframework.http.HttpMethod#DELETE} method base interface
 *
 * Sample execution for a {@link org.springframework.http.ResponseEntity}:
 *
 * <pre>
 * httpMethodBuilder
 *    .delete()
 *        .onUrl("client/123123")
 *    .andExecuteFor()
 *        .aResponseEntity()
 * </pre>
 *
 * Sample execution with headers ignoring response:
 *
 * <pre>
 * httpMethodBuilder
 *    .delete()
 *    .onUrl("client/123123")
 *    .withHeaders()
 *        .contentTypeJson()
 *    .andExecuteFor()
 *        .ignoringResponse()
 * </pre>
 *
 * @see com.ofg.infrastructure.web.resttemplate.fluent.HttpMethodBuilder
 * @see HttpMethod
 * @see ResponseReceivingDeleteMethod
 * @see UrlParameterizableDeleteMethod
 */
interface DeleteMethod extends HttpMethod<ResponseReceivingDeleteMethod, UrlParameterizableDeleteMethod> {

}
