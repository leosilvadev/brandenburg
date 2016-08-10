package br.leosilvadev.proxy;

import io.vertx.core.Vertx;

public class ProxyApplication {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(ProxyVerticle.class.getName());
	}

}
