package br.leosilvadev.proxy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.leosilvadev.proxy.server.ProxyVerticle;
import io.vertx.core.Vertx;

@Configuration
public class ProxyConfig {
	
	@Autowired ProxyVerticle proxyVerticle;

	@Bean
	public Vertx vertx() {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(proxyVerticle);
		return vertx;
	}
}
