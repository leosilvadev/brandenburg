package br.leosilvadev.proxy.server

import static io.restassured.RestAssured.*
import static io.restassured.matcher.RestAssuredMatchers.*
import static org.hamcrest.Matchers.*
import groovy.json.JsonOutput
import io.restassured.http.ContentType
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import spock.util.concurrent.AsyncConditions
import br.leosilvadev.proxy.IntegrationSpec

class ProxyServerSpec extends IntegrationSpec {

	def setupSpec() {
		def conds = new AsyncConditions()
		def server = ProxyServerFixture.buildServer vertx
		server.listen(9000) { res ->
			conds.evaluate { assert res.succeeded() }
		}
		conds.await 5
	}

	def 'Should forward a GET request to /users'() {
		given:
		def request = given().accept(ContentType.JSON)

		when:
		def response = request.get('http://localhost:8000/users')

		then:
		response.statusCode() == 200

		and:
		response.contentType() == 'application/json'

		and:
		response.header('application') == 'vertx-proxy'
	}

	def 'Should forward a POST request to /users'() {
		given:
		def user = [name: 'leonardo']
		def request = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(JsonOutput.toJson(user))

		when:
		def response = request.post('http://localhost:8000/users')

		then:
		response.statusCode() == 201

		and:
		response.contentType() == 'application/json'

		and:
		response.header('application') == 'vertx-proxy'
	}
}
