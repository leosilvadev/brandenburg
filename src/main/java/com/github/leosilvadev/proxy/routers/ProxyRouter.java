package com.github.leosilvadev.proxy.routers;

import java.util.List;

import com.github.leosilvadev.proxy.domains.CorsRoute;
import com.github.leosilvadev.proxy.domains.ProxyApiRoute;
import com.github.leosilvadev.proxy.domains.ProxyEndpointRoute;
import com.github.leosilvadev.proxy.domains.TargetEndpoint;
import com.github.leosilvadev.proxy.domains.TargetEndpoint.TargetEndpointBuilder;
import com.github.leosilvadev.proxy.forwarders.ProxyRequestForwarder;
import com.github.leosilvadev.proxy.forwarders.RequestForwarder;
import com.github.leosilvadev.proxy.middlewares.AbstractMiddleware;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

public class ProxyRouter {

  private static final Logger logger = LoggerFactory.getLogger(ProxyRouter.class);

  private final Vertx vertx;
  private final Router router;

  public ProxyRouter(Vertx vertx, Router router) {
    this.vertx = vertx;
    this.router = router;
  }

  public void route(final JsonObject routes, final List<AbstractMiddleware> middlewares) {
    if (middlewares.isEmpty())
      logger.warn("No middleware to register!");

    middlewares.forEach(this::route);

    final ProxyRequestForwarder proxyForwarder = new ProxyRequestForwarder(vertx);
    routes.forEach(entry -> {
      logger.info("Mapping API {0} ...", entry.getKey());
      final JsonObject apiConfig = (JsonObject) entry.getValue();
      final String url = apiConfig.getString("url");

      if (url == null || url.isEmpty()) {
        final IllegalArgumentException ex = new IllegalArgumentException("API requires an URL");
        logger.fatal(ex.getMessage(), ex);
        throw ex;
      }

      final JsonObject corsConfig = apiConfig.getJsonObject("cors");
      if (corsConfig != null) {
        route(CorsRoute.from(corsConfig));
      }

      final Long timeout = apiConfig.getLong("timeout");
      final JsonObject bind = apiConfig.getJsonObject("bind");
      final JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
      if (mustBindApi(bind)) {
        route(ProxyApiRoute.from(url, bind, timeout), proxyForwarder);

      } else if (hasEndpoints(endpointsConfig)) {
        endpointsConfig.forEach(conf -> {
          final JsonObject json = (JsonObject) conf;
          route(ProxyEndpointRoute.from(url, json, timeout), proxyForwarder);
        });

      } else {
        final IllegalArgumentException ex = new IllegalArgumentException(
            "You must configure the API with either bind or specific endpoints");
        logger.fatal(ex.getMessage(), ex);
        throw ex;
      }
      logger.info("API {0} mapped successfully.", entry.getKey());
    });
  }

  private Boolean hasEndpoints(final JsonArray endpointsConfig) {
    return endpointsConfig != null && endpointsConfig.size() > 0;
  }

  private Boolean mustBindApi(final JsonObject json) {
    return json != null && json.getBoolean("active");
  }

  private Route route(final AbstractMiddleware middleware) {
    final String path = middleware.path();
    if (path == null || path.isEmpty()) {
      logger.info("Registering middleware for all the endpoints");
      return router.route().handler(middleware);
    }
    logger.info("Registering middleware for {0}", path);
    return router.route(path).handler(middleware);
  }

  private Route route(final ProxyApiRoute route, final RequestForwarder forwarder) {
    final String endpointPath = String.format("%s/*", route.getTargetPath());
    logger.info("Routing all endpoints for {0} to api {1}", endpointPath, route.getUrl());
    return router.route(endpointPath).handler(context -> {
      final TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrl(), route.getTargetPath())
          .appendPath(route.getAppendPath()).setTimeout(route.getTimeout()).build();
      forwarder.forward(targetEndpoint, context.request(), context.response());
    });
  }

  private Route route(final CorsRoute corsRoute) {
    final CorsHandler corsHandler = CorsHandler.create(corsRoute.getAllowedOriginPattern());

    if (corsRoute.getAllowCredentials() != null)
      corsHandler.allowCredentials(corsRoute.getAllowCredentials());

    if (corsRoute.getAllowHeaders() != null)
      corsHandler.allowedHeaders(corsRoute.getAllowHeaders());

    if (corsRoute.getAllowMethods() != null)
      corsHandler.allowedMethods(corsRoute.getAllowMethods());

    if (corsRoute.getExposeHeaders() != null)
      corsHandler.exposedHeaders(corsRoute.getExposeHeaders());

    if (corsRoute.getFromPath() == null) {
      if (corsRoute.getFromMethod() == null) {
        return router.route().handler(corsHandler);
      } else {
        throw new IllegalArgumentException("Cannot configure CORS for a specific Method without a Path");
      }
    } else if (corsRoute.getFromMethod() == null) {
      return router.route(corsRoute.getFromPath()).handler(corsHandler);
    } else {
      return router.route(corsRoute.getFromMethod(), corsRoute.getFromPath()).handler(corsHandler);
    }
  }

  private Route route(final ProxyEndpointRoute route, final RequestForwarder forwarder) {
    final String pathFrom = route.getFromPath();
    final String urlTo = route.getUrlTo();
    logger.info("Routing endpoint with method {0} and path {1} to api {2} method {3}", route.getFromMethod(), pathFrom,
        urlTo, route.getToMethod());
    final Handler<RoutingContext> handler = (context) -> {
      final TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrlTo(), route.getToPath())
          .setTimeout(route.getTimeout()).setMethod(route.getToMethod()).build();
      forwarder.forward(targetEndpoint, context.request(), context.response());
    };

    if (route.isThereFromMethod()) {
      return router.route(route.getFromMethod(), pathFrom).handler(handler);
    } else {
      return router.route(pathFrom).handler(handler);
    }
  }
}
