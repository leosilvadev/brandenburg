package br.leosilvadev.proxy

import io.vertx.core.Vertx
import spock.lang.Shared
import spock.lang.Specification

class IntegrationSpec extends Specification {

	@Shared Vertx vertx
	
	def setupSpec() {
		vertx = Vertx.vertx()
	}
	
	def cleanupSpec() {
		vertx.close()
	}
	
}
