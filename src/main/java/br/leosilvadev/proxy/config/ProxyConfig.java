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
	public ApplicationConfig applicationConfig() {
		Buffer buffer = vertx().fileSystem().readFileBlocking("application.json");
		JsonObject json = new JsonObject(buffer.toString());
		JsonObject jsonCaching = json.getJsonObject("caching", new JsonObject());
		return new ApplicationConfig(json.getInteger("port", 8000), json.getString("routesPath", "routes.json"),
				jsonCaching.getString("host", "localhost"), jsonCaching.getInteger("port", 6379),
				jsonCaching.getString("encoding", "UTF-8"), jsonCaching.getString("auth"));
	}

	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}

	@Bean
	public Caching caching() {
		return new Caching(vertx()).config(applicationConfig());
	}
}
