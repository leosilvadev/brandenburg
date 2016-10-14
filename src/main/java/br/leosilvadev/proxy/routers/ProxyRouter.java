package br.leosilvadev.proxy.routers;

import java.util.List;

import br.leosilvadev.proxy.domains.ProxyApiRoute;
import br.leosilvadev.proxy.domains.ProxyEndpointRoute;
import br.leosilvadev.proxy.domains.TargetEndpoint;
import br.leosilvadev.proxy.domains.TargetEndpoint.TargetEndpointBuilder;
import br.leosilvadev.proxy.forwarders.ProxyRequestForwarder;
import br.leosilvadev.proxy.forwarders.RequestForwarder;
import br.leosilvadev.proxy.middlewares.Middleware;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

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
			logger.info(String.format("Mapping API %s ...", entry.getKey()));
			JsonObject apiConfig = (JsonObject) entry.getValue();
			String url = apiConfig.getString("url");
			Long timeout = apiConfig.getLong("timeout");
			String permission = apiConfig.getString("permission");
			JsonObject bind = apiConfig.getJsonObject("bind");
			if (mustBindApi(bind)) {
				route(ProxyApiRoute.from(url, bind, timeout, permission), proxyForwarder);
			}
			JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
			endpointsConfig.forEach(conf -> {
				JsonObject json = (JsonObject) conf;
				route(ProxyEndpointRoute.from(url, json, timeout), proxyForwarder);
			});
			logger.info(String.format("API %s mapped successfully.", entry.getKey()));
		});
	}

	private Boolean mustBindApi(JsonObject json) {
		return json != null && json.getBoolean("active");
	}

	private Route route(Middleware middleware) {
		if (middleware.httpMethod() == null) {
			if (middleware.path() == null) {
				logger.info("Registering middleware for all the endpoints");
				return router.route().handler(middleware);
			}
			logger.info(String.format("Registering middleware for %s", middleware.path()));
			return router.route(middleware.path()).handler(middleware);
		}
		logger.info(String.format("Registering middleware for %s %s", middleware.httpMethod(), middleware.path()));
		return router.route(middleware.httpMethod(), middleware.path()).handler(middleware);
	}

	private Route route(ProxyApiRoute route, RequestForwarder forwarder) {
		String endpointPath = String.format("%s/*", route.getTargetPath());
		logger.info(String.format("Routing all endpoints for %s to api %s", endpointPath, route.getUrl()));
		return router.route(endpointPath).handler(context -> {
			TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrl(), route.getTargetPath())
					.appendPath(route.getAppendPath()).setTimeout(route.getTimeout())
					.setPermission(route.getPermission()).build();
			forwarder.forward(targetEndpoint, context.request(), context.response());
		});
	}

	private Route route(ProxyEndpointRoute route, RequestForwarder forwarder) {
		String pathFrom = route.getFromPath();
		String urlTo = route.getUrlTo();
		logger.info(String.format("Routing endpoint with method %s and path %s to api %s method %s",
				route.getFromMethod(), pathFrom, urlTo, route.getToMethod()));
		return router.route(route.getFromMethod(), pathFrom).handler(context -> {
			TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrlTo(), route.getToPath())
					.setTimeout(route.getTimeout()).setPermission(route.getPermission()).setMethod(route.getToMethod())
					.build();
			forwarder.forward(targetEndpoint, context.request(), context.response());
		});
	}
}
