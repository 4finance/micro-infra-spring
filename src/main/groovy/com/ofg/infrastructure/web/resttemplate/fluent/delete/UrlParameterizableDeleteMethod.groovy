package com.ofg.infrastructure.web.resttemplate.fluent.delete

import com.ofg.infrastructure.web.resttemplate.fluent.common.request.ParametrizedUrlHavingWith

/**
 * {@link org.springframework.http.HttpMethod#DELETE} methods can have its headers parametrized
 */
interface UrlParameterizableDeleteMethod extends ParametrizedUrlHavingWith<ResponseReceivingDeleteMethod> {

}
