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
			def urlTemplate = "/customer/$customerId/order/$orderId"
		and:
			GetMethodBuilder get =
					new GetMethodBuilder(new RestTemplate())
		when:
			ResponseReceivingGetMethod response = get.onUrl(urlTemplate)
		then:
			Map params = response.get(Object).params
			params.urlTemplate == "/customer/{p0}/order/{p1}"
			params.urlVariablesArray == [customerId, orderId]
	}

}
