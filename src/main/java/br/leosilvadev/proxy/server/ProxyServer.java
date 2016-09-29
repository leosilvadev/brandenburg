package br.leosilvadev.proxy.server;

import br.leosilvadev.proxy.readers.RoutesReader;
import br.leosilvadev.proxy.routers.ProxyRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
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
		new RoutesReader(vertx).read(config.getRoutesFilePath(), routes -> {
			new ProxyRouter(vertx, router).route(routes);
			return server.requestHandler(router::accept).listen(config.getPort(), onListening(future));
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
