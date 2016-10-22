package com.github.leosilvadev.proxy.server

import groovy.json.JsonOutput
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class ProxyServerFixture {

	static HttpServer buildServer(Vertx vertx) {
		def router = Router.router(vertx)
		router.get('/users/timeout').handler { RoutingContext context -> }
		router.get('/users/badRequest').handler { RoutingContext context -> 
			context.response().setStatusCode(400)
					.putHeader('application', 'vertx-proxy')
					.end()
		}
		router.get('/users').handler { RoutingContext context ->
			def users = JsonOutput.toJson([
				[name:'User 1', alias:'U1'],
				[name:'User 2', alias:'U2']
			])
			context.response()
					.setChunked(true)
					.setStatusCode(200)
					.putHeader('application', 'vertx-proxy')
					.putHeader('content-type', 'application/json')
					.end(users)
		}
		router.get('/users/params').handler { RoutingContext context ->
			def params = context.request().params()
			def result = JsonOutput.toJson([param1: params.param1, param2: params.param2])
			context.response()
					.setChunked(true)
					.setStatusCode(200)
					.putHeader('content-type', 'application/json')
					.end(result)
		}
		router.get('/users/xml').handler { RoutingContext context ->
			def users = '''
				<users>
					<user id="1">
						<name>User 1</name>
						<age>20</age>
					</user>
				</users>
			'''
			context.response()
					.setChunked(true)
					.setStatusCode(200)
					.putHeader('application', 'vertx-proxy')
					.putHeader('content-type', 'application/xml')
					.end(users)
		}
		router.post('/users').handler BodyHandler.create()
		router.post('/users').handler { RoutingContext context ->
			def user = context.getBodyAsJson()
			if (user.getString("name")=='leonardo') {
				def links = JsonOutput.toJson([links: [
						[href: '/users/1', rel: 'self'],
						[href: '/users/1', rel: 'edit'],
						[href: '/users/1/permissions', rel: 'permissions']
					]])
				context.response()
						.setChunked(true)
						.setStatusCode(201)
						.putHeader('application', 'vertx-proxy')
						.putHeader('content-type', 'application/json')
						.end(links)
			} else {
				context.response()
						.setStatusCode(500)
						.end()
			}
		}
		def server = vertx.createHttpServer()
		server.requestHandler(router.&accept)
	}
}
