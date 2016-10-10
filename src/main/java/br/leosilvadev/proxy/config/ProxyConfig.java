package br.leosilvadev.proxy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.leosilvadev.proxy.caching.Caching;
import br.leosilvadev.proxy.server.ProxyVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

@Configuration
public class ProxyConfig {

	@Autowired
	ProxyVerticle proxyVerticle;

	@Bean
	public Vertx vertx() {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(proxyVerticle);
		return vertx;
	}

	@Bean
	public Caching caching() {
		Buffer buffer = vertx().fileSystem().readFileBlocking("caching.json");
		JsonObject json = new JsonObject(buffer.toString());
		return new Caching(vertx()).config(json);
	}
}
