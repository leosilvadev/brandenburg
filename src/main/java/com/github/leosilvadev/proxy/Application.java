package com.github.leosilvadev.proxy;

import com.github.leosilvadev.proxy.config.ApplicationConfig;
import com.github.leosilvadev.proxy.middlewares.AbstractMiddleware;
import com.github.leosilvadev.proxy.server.verticles.ProxyVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Application {
  
  public static void main(final String[] args) {
    final Vertx vertx = Vertx.vertx();
    final ApplicationConfig config = applicationConfig();
    final List<AbstractMiddleware> middlewares = new ArrayList<>();

    final JsonObject json = new JsonObject().put(ApplicationConfig.PORT_JSON, config.getPort()).put(ApplicationConfig.ROUTES_PATH_JSON, config.getRoutesPath());
    vertx.deployVerticle(new ProxyVerticle(middlewares), new DeploymentOptions().setConfig(json));
  }

  private static ApplicationConfig applicationConfig() {
    final Integer port = Integer.parseInt(System.getenv().getOrDefault("BRANDENBURG_PORT", "9000"));
    final String routesPath = System.getenv().getOrDefault("BRANDENBURG_ROUTES", "routes.json");
    return new ApplicationConfig(port, routesPath);
  }

}
