package br.leosilvadev.proxy.routers

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import spock.lang.Specification
import br.leosilvadev.proxy.domains.ProxyEndpointRoute

class ProxyRouterSpec extends Specification {

	def vertx

	def setup() {
		vertx = Vertx.vertx()
	}

	def 'Should build a route'() {
		given:
		Router router = Router.router(vertx)

		and:
		def proxyRouter = new ProxyRouter(router)

		and:
		def method = HttpMethod.POST

		and:
		def url = 'http://myapi.io'

		and:
		def pathFrom = '/v1/users'

		and:
		def pathTo = '/users'

		when:
		def route = proxyRouter.route(new ProxyEndpointRoute(url, method, pathFrom, method, pathTo)) {}

		then: 'there must be only one registered route'
		router.routes.size() == 1

		and: 'the route must be built based on pathFrom'
		router.routes.first().path == pathFrom
	}

	def cleanup() {
		vertx.close()
	}
}
