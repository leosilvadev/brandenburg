package com.github.leosilvadev.proxy.server.verticles;

import java.util.ArrayList;
import java.util.List;

import com.github.leosilvadev.proxy.config.ApplicationConfig;
import com.github.leosilvadev.proxy.middlewares.AbstractMiddleware;
import com.github.leosilvadev.proxy.server.ProxyServer;
import com.github.leosilvadev.proxy.server.ProxyServerConfig;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ProxyVerticle extends AbstractVerticle {
  
  private static final Logger logger = LoggerFactory.getLogger(ProxyVerticle.class);
  
  private final List<AbstractMiddleware> middlewares;
  
  public ProxyVerticle() {
    this.middlewares = new ArrayList<>();
  }
  
  public ProxyVerticle(List<AbstractMiddleware> middlewares) {
    this.middlewares = middlewares;
  }
  
  @Override
  public void start(final Future<Void> future) throws Exception {
    try {
      final JsonObject config = config();
      final Integer port = config.getInteger(ApplicationConfig.PORT_JSON, 8080);
      final String routesPath = config.getString(ApplicationConfig.ROUTES_PATH_JSON, ProxyServerConfig.DEFAULT_ROUTES_FILE);
      new ProxyServer(vertx, new ProxyServerConfig(port, routesPath)).run(middlewares);
      future.complete();
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      future.fail(ex);
    }
  }
}
