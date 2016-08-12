package br.leosilvadev.proxy.server

import spock.util.concurrent.AsyncConditions
import br.leosilvadev.proxy.IntegrationSpec
import groovy.json.JsonOutput
import io.vertx.core.Future;
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext;;

class ProxyServerSpec extends IntegrationSpec {

	def setupSpec() {
		def conds = new AsyncConditions()
		
		def router = Router.router(vertx)
		router.get('/users').handler { RoutingContext context ->
			def users = JsonOutput.toJson([[name:'User 1', alias:'U1'], [name:'User 2', alias:'U2']])
			context.response().setChunked(true).end(users)
		}
		router.post('/users').handler { RoutingContext context ->
			def users = JsonOutput.toJson([[name:'User 1', alias:'U1'], [name:'User 2', alias:'U2']])
			context.response().setChunked(true).end(users)
		}
		def server = vertx.createHttpServer()
		server.requestHandler(router.&accept)
		server.listen(9000, { res ->
			conds.evaluate {
				assert res.succeeded()
			}
		})
		
		conds.await 5
	}
	
	def 'Should forward a GET request to /users'() {
		
	}
	
}
