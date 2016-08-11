package br.leosilvadev.proxy.server;

import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;

@Component
public class ProxyVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		new ProxyServer(vertx, new ProxyServerConfig(8000)).run();
	}

}
