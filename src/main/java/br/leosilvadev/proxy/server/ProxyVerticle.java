package br.leosilvadev.proxy.server;

import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

@Component
public class ProxyVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		JsonObject config = config();
		String envPort = System.getenv("PORT");
		Integer port = config.getInteger("port", envPort==null ? 8080 : Integer.parseInt(envPort));
		String routesPath = config.getString("routesPath", ProxyServerConfig.DEFAULT_ROUTES_FILE);
		new ProxyServer(vertx, new ProxyServerConfig(port, routesPath)).run();
	}

}
