package com.github.leosilvadev.proxy.server;

import java.util.List;

import com.github.leosilvadev.proxy.middlewares.AbstractMiddleware;
import com.github.leosilvadev.proxy.readers.ConfigReader;
import com.github.leosilvadev.proxy.routers.ProxyRouter;

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
  
  public ProxyServer(final Vertx vertx, final ProxyServerConfig config) {
    this.vertx = vertx;
    this.config = config;
    this.server = vertx.createHttpServer();
    this.router = Router.router(vertx);
  }
  
  public Future<ProxyServer> run(final List<AbstractMiddleware> middlewares) {
    final Future<ProxyServer> future = Future.future();
    logger.info("Reading Routes file from {}", config.getRoutesFilePath());
    new ConfigReader(vertx).read(config.getRoutesFilePath(), routes -> {
      new ProxyRouter(vertx, router).route(routes, middlewares);
      return server.requestHandler(router::accept).listen(config.getPort(), onListening(future));
    });
    return future;
  }
  
  private Handler<AsyncResult<HttpServer>> onListening(final Future<ProxyServer> future) {
    return (serverResult) -> {
      if (serverResult.succeeded()) {
        logger.info("Proxy Server running on port {}", config.getPort());
        future.complete(this);
      } else {
        logger.error("Error trying to run Proxy Server", serverResult.cause());
        future.fail(serverResult.cause());
      }
    };
  }
}
