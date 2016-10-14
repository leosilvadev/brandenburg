package br.leosilvadev.proxy.server.verticles;

import java.util.ArrayList;
import java.util.List;

import br.leosilvadev.proxy.config.ApplicationConfig;
import br.leosilvadev.proxy.middlewares.Middleware;
import br.leosilvadev.proxy.server.ProxyServer;
import br.leosilvadev.proxy.server.ProxyServerConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ProxyVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ProxyVerticle.class);

	private final List<Middleware> middlewares;
	
	public ProxyVerticle() {
		this.middlewares = new ArrayList<>();
	}
	
	public ProxyVerticle(List<Middleware> middlewares) {
		this.middlewares = middlewares;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		try {
			JsonObject config = config();
			Integer port = config.getInteger(ApplicationConfig.PORT_JSON, 8080);
			String routesPath = config.getString(ApplicationConfig.ROUTES_PATH_JSON, ProxyServerConfig.DEFAULT_ROUTES_FILE);
			new ProxyServer(vertx, new ProxyServerConfig(port, routesPath)).run(middlewares);
			future.complete();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage(), ex);
			future.fail(ex);
		}
	}
}
