package com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive

import com.ofg.infrastructure.web.resttemplate.custom.RestTemplate
import com.ofg.infrastructure.web.resttemplate.fluent.get.GetMethodBuilder
import com.ofg.infrastructure.web.resttemplate.fluent.get.ResponseReceivingGetMethod
import spock.lang.Specification

class MethodParamsApplierSpec extends Specification {

	def 'should parse GString and replace it with URL template'() {
		given:
			int customerId = 1
			String orderId = "XYZ"
			def urlTemplate = "http://localhost:10000/$customerId/order/$orderId"
		and:
			GetMethodBuilder get =
					new GetMethodBuilder(new RestTemplate())
		when:
			ResponseReceivingGetMethod response = get.onUrl(urlTemplate)
		then:
			Map params = response.get(Object).params
			params.urlTemplate == "http://localhost:10000/{p0}/order/{p1}"
			params.urlVariablesArray == [customerId, orderId]
	}

	def 'should replace first variable with hardcoded host to avoid issues while parsing URI starting with placeholder'() {
		given:
			String address = 'http://localhost:8080'
			int id = 42
		and:
			GetMethodBuilder get = new GetMethodBuilder(new RestTemplate())
		when:
			ResponseReceivingGetMethod response = get
					.onUrlFromTemplate('{address}/order/{id}')
					.withVariables(address, id)
		then:
			Map params = response.get(Object).params
			params.urlTemplate == "http://localhost:8080/order/{id}"
			params.urlVariablesArray == [id]
	}

	def 'should replace first variable with hardcoded host to avoid issues while parsing URI from GString'() {
		given:
			String address = 'http://localhost:8080'
			int id = 42
		and:
			GetMethodBuilder get = new GetMethodBuilder(new RestTemplate())
		when:
			ResponseReceivingGetMethod response = get
					.onUrl("$address/order/$id")
		then:
			Map params = response.get(Object).params
			params.urlTemplate == "http://localhost:8080/order/{p1}"
			params.urlVariablesArray == [id]
	}

	def 'should replace host in placeholder'() {
		given:
			String address = 'http://localhost:8080'
		and:
			GetMethodBuilder get = new GetMethodBuilder(new RestTemplate())
		when:
			ResponseReceivingGetMethod response = get
					.onUrl("$address")
		then:
			Map params = response.get(Object).params
			params.urlTemplate == address
			params.urlVariablesArray == []
	}

}
