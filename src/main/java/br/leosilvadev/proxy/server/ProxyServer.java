package br.leosilvadev.proxy.server;

import br.leosilvadev.proxy.domains.ProxyEndpointRoute;
import br.leosilvadev.proxy.forwarders.ProxyForwarder;
import br.leosilvadev.proxy.routers.ProxyRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class ProxyServer {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);

	private final Vertx vertx;
	private final HttpServer server;
	private final Router router;
	private final ProxyServerConfig config;

	public ProxyServer(Vertx vertx, ProxyServerConfig config) {
		this.vertx = vertx;
		this.config = config;
		this.server = vertx.createHttpServer();
		this.router = Router.router(vertx);
	}

	public Future<ProxyServer> run() {
		Future<ProxyServer> future = Future.future();
		logger.info(String.format("Reading Routes file from %s", config.getRoutesFilePath()));
		vertx.fileSystem().readFile(config.getRoutesFilePath(), (fileResult) -> {
			Buffer buffer = fileResult.result();
			if (buffer == null) {
				String message = String.format("Routes File %s not found!", config.getRoutesFilePath());
				logger.error(message, config.getRoutesFilePath());
				future.fail(message);

			} else {
				JsonObject routes = buffer.toJsonObject();
				ProxyRouter proxyRouter = new ProxyRouter(router);
				ProxyForwarder proxyForwarder = new ProxyForwarder(vertx);
				routes.forEach((entry) -> {
					logger.info(String.format("Mapping API %s ...", entry.getKey()));
					JsonObject apiConfig = (JsonObject) entry.getValue();
					String url = apiConfig.getString("url");
					Long timeout = apiConfig.getLong("timeout");
					JsonObject bind = apiConfig.getJsonObject("bind");
					if (bind != null && bind.getBoolean("active")) {
						String path = bind.getString("path");
						Boolean appendPath = bind.getBoolean("append_path");
						proxyRouter.route(url, path, appendPath, proxyForwarder);
					}
					JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
					endpointsConfig.forEach((conf) -> {
						JsonObject json = (JsonObject) conf;
						proxyRouter.route(ProxyEndpointRoute.from(url, json, timeout), proxyForwarder);
					});
					logger.info(String.format("API %s mapped successfully.", entry.getKey()));
				});
				server.requestHandler(router::accept).listen(config.getPort(), onListening(future));
			}
		});
		return future;
	}

	private Handler<AsyncResult<HttpServer>> onListening(Future<ProxyServer> future) {
		return (serverResult) -> {
			if (serverResult.succeeded()) {
				logger.info(String.format("Proxy Server running on port %s", config.getPort()));
				future.complete(this);
			} else {
				logger.error("Error trying to run Proxy Server", serverResult.cause());
				future.fail(serverResult.cause());
			}
		};
	}
}
