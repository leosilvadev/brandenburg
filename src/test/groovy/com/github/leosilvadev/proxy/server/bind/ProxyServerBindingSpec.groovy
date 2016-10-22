package com.github.leosilvadev.proxy.server.bind

import static io.restassured.RestAssured.*
import static io.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*
import groovy.json.JsonOutput
import io.restassured.http.ContentType
import io.vertx.core.json.Json

import org.apache.http.HttpStatus

import spock.util.concurrent.AsyncConditions

import com.github.leosilvadev.proxy.IntegrationSpec
import com.github.leosilvadev.proxy.server.ProxyServerFixture

class ProxyServerBindingSpec extends IntegrationSpec {

	def setupSpec() {
		deployProxyVerticle(8001, 'routes-bind.json')

		def conds = new AsyncConditions()
		def server = ProxyServerFixture.buildServer vertx
		server.listen(9001) { res ->
			conds.evaluate { assert res.succeeded() }
		}
		conds.await 5
	}

	def 'Should forward a GET request to /users'() {
		given:
		def request = given().accept(ContentType.JSON)

		when:
		def response = request.get('http://localhost:8001/users')

		then:
		response.statusCode() == HttpStatus.SC_OK

		and:
		response.contentType() == 'application/json'

		and:
		response.header('application') == 'vertx-proxy'
	}

	def 'Should forward a GET request to /users/params with query parameters'() {
		given:
		def request = given().accept(ContentType.JSON)

		when:
		def response = request.get('http://localhost:8001/users/params?param1=1&param2=2')

		then:
		response.statusCode() == HttpStatus.SC_OK

		and:
		response.contentType() == 'application/json'

		and:
		def json = Json.decodeValue(response.body().asString(), Map)
		json.param1 == '1'
		json.param2 == '2'
	}

	def 'Should forward a POST request to /users'() {
		given:
		def user = [name: 'leonardo']
		def request = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(JsonOutput.toJson(user))

		when:
		def response = request.post('http://localhost:8001/users')

		then:
		response.statusCode() == HttpStatus.SC_CREATED

		and:
		response.contentType() == 'application/json'

		and:
		response.header('application') == 'vertx-proxy'
	}

	def 'Should return timeout when calling /timeout'() {
		given:
		def request = given().accept(ContentType.JSON)

		when:
		def response = request.get('http://localhost:8001/users/timeout')

		then:
		response.statusCode() == HttpStatus.SC_GATEWAY_TIMEOUT
	}

	def 'Should return status 400 when calling /badRequest'() {
		given:
		def request = given().accept(ContentType.JSON)

		when:
		def response = request.get('http://localhost:8001/users/badRequest')

		then:
		response.statusCode() == HttpStatus.SC_BAD_REQUEST

		and:
		response.header('application') == 'vertx-proxy'
	}

	def 'Should forward a GET request to /users/xml, asking for an xml response'() {
		given:
		def user = [name: 'leonardo']
		def request = given().accept(ContentType.XML)

		when:
		def response = request.get('http://localhost:8001/users/xml')

		then:
		response.statusCode() == 200

		and:
		response.contentType() == 'application/xml'

		and:
		response.header('application') == 'vertx-proxy'

		and:
		response.body().xmlPath()
	}
}
