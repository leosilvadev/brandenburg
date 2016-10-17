package com.github.leosilvadev.proxy

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.json.JsonObject
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

import com.github.leosilvadev.proxy.config.ApplicationConfig
import com.github.leosilvadev.proxy.server.verticles.ProxyVerticle

class IntegrationSpec extends Specification {

	@Shared Vertx vertx
	@Shared HttpClient client
	
	def setupSpec() {
		vertx = Vertx.vertx()
	}
	
	def deployProxyVerticle(Integer port, String routesPath) {
		def conds = new AsyncConditions()
		def json = new JsonObject()
			.put(ApplicationConfig.ROUTES_PATH_JSON, routesPath)
			.put(ApplicationConfig.PORT_JSON, port)
		def options = new DeploymentOptions().setConfig(json)
		
		client = vertx.createHttpClient()
		vertx.deployVerticle(ProxyVerticle.name, options, {res ->
			if(res.failed()) res.cause().printStackTrace()
			conds.evaluate { assert res.succeeded() }
		})
		conds.await 3
	}
	
	def cleanupSpec() {
		vertx.close()
	}
	
}
