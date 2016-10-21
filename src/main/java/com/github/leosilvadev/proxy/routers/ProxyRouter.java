package com.github.leosilvadev.proxy.routers;

import java.util.List;

import com.github.leosilvadev.proxy.domains.ProxyApiRoute;
import com.github.leosilvadev.proxy.domains.ProxyEndpointRoute;
import com.github.leosilvadev.proxy.domains.TargetEndpoint;
import com.github.leosilvadev.proxy.domains.TargetEndpoint.TargetEndpointBuilder;
import com.github.leosilvadev.proxy.forwarders.ProxyRequestForwarder;
import com.github.leosilvadev.proxy.forwarders.RequestForwarder;
import com.github.leosilvadev.proxy.middlewares.Middleware;
import com.github.leosilvadev.proxy.middlewares.MiddlewareMapping;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProxyRouter {

	private static final Logger logger = LoggerFactory.getLogger(ProxyRouter.class);

	private final Vertx vertx;
	private final Router router;

	public ProxyRouter(Vertx vertx, Router router) {
		this.vertx = vertx;
		this.router = router;
	}

	public void route(JsonObject routes, List<Middleware> middlewares) {
		if (middlewares.isEmpty())
			logger.warn("No middleware to register!");
		middlewares.forEach(this::route);

		ProxyRequestForwarder proxyForwarder = new ProxyRequestForwarder(vertx);
		routes.forEach(entry -> {
			logger.info("Mapping API {0} ...", entry.getKey());
			JsonObject apiConfig = (JsonObject) entry.getValue();
			String url = apiConfig.getString("url");
			Long timeout = apiConfig.getLong("timeout");
			JsonObject bind = apiConfig.getJsonObject("bind");
			if (mustBindApi(bind)) {
				route(ProxyApiRoute.from(url, bind, timeout), proxyForwarder);
			}
			JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
			endpointsConfig.forEach(conf -> {
				JsonObject json = (JsonObject) conf;
				route(ProxyEndpointRoute.from(url, json, timeout), proxyForwarder);
			});
			logger.info("API {0} mapped successfully.", entry.getKey());
		});
	}

	private Boolean mustBindApi(JsonObject json) {
		return json != null && json.getBoolean("active");
	}

	private Route route(Middleware middleware) {
		MiddlewareMapping mapping = middleware.getClass().getAnnotation(MiddlewareMapping.class);
		String path = mapping.value();
		if (path == null || path.isEmpty()) {
			logger.info("Registering middleware for all the endpoints");
			return router.route().handler(middleware);
		}
		logger.info("Registering middleware for {0}", path);
		return router.route(path).handler(middleware);
	}

	private Route route(ProxyApiRoute route, RequestForwarder forwarder) {
		String endpointPath = String.format("%s/*", route.getTargetPath());
		logger.info("Routing all endpoints for {0} to api {1}", endpointPath, route.getUrl());
		return router.route(endpointPath).handler(context -> {
			TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrl(), route.getTargetPath())
					.appendPath(route.getAppendPath()).setTimeout(route.getTimeout()).build();
			forwarder.forward(targetEndpoint, context.request(), context.response());
		});
	}

	private Route route(ProxyEndpointRoute route, RequestForwarder forwarder) {
		String pathFrom = route.getFromPath();
		String urlTo = route.getUrlTo();
		logger.info("Routing endpoint with method {0} and path {1} to api {2} method {3}", route.getFromMethod(), pathFrom, urlTo, route.getToMethod());
		Handler<RoutingContext> handler = (context) -> {
			TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrlTo(), route.getToPath())
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
