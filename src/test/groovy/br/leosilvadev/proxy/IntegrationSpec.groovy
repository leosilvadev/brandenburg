package br.leosilvadev.proxy

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.json.JsonObject
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions
import br.leosilvadev.proxy.server.ProxyVerticle

class IntegrationSpec extends Specification {

	@Shared Vertx vertx
	@Shared HttpClient client
	
	def setupSpec() {
		vertx = Vertx.vertx()
	}
	
	def deployProxyVerticle(String routesPath) {
		def conds = new AsyncConditions()
		def options = new DeploymentOptions().setConfig(new JsonObject().put('routesPath', routesPath))
		
		client = vertx.createHttpClient()
		vertx.deployVerticle(ProxyVerticle.name, options, {res ->
			conds.evaluate { assert res.succeeded() }
		})
		conds.await 5
	}
	
	def cleanupSpec() {
		vertx.close()
	}
	
}
