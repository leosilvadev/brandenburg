package br.leosilvadev.proxy.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ProxyVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ProxyVerticle.class);

	@Override
	public void start(Future<Void> future) throws Exception {
		try {
			JsonObject config = config();
			Integer port = config.getInteger("port", 8000);
			String routesPath = config.getString("routesPath", ProxyServerConfig.DEFAULT_ROUTES_FILE);
			new ProxyServer(vertx, new ProxyServerConfig(port, routesPath)).run();
			future.complete();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage(), ex);
			future.fail(ex);
		}
	}
}
