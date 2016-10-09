package br.leosilvadev.proxy.routers;

import br.leosilvadev.proxy.domains.ProxyEndpointRoute;
import br.leosilvadev.proxy.domains.TargetEndpoint;
import br.leosilvadev.proxy.domains.TargetEndpoint.TargetEndpointBuilder;
import br.leosilvadev.proxy.forwarders.ProxyRequestForwarder;
import br.leosilvadev.proxy.forwarders.RequestForwarder;
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

	public Route route(String url, String targetPath, Long timeout, String permission, Boolean appendPath, RequestForwarder forwarder) {
		String endpointPath = String.format("%s/*", targetPath);
		logger.info(String.format("Routing all endpoints for %s to api %s", endpointPath, url));
		return router.route(endpointPath).handler((context) -> {
			TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, url, targetPath)
					.appendPath(appendPath)
					.setTimeout(timeout)
					.setPermission(permission)
					.build();
			forwarder.forward(targetEndpoint, context.request(), context.response());
		});
	}

	public Route route(ProxyEndpointRoute route, RequestForwarder forwarder) {
		String pathFrom = route.getFromPath();
		String urlTo = route.getUrlTo();
		logger.info(String.format("Routing endpoint with method %s and path %s to api %s method %s", route.getFromMethod(), pathFrom, urlTo, route.getToMethod()));
		return router.route(route.getFromMethod(), pathFrom).handler((context) -> {
			TargetEndpoint targetEndpoint = new TargetEndpointBuilder(context, route.getUrlTo(), route.getToPath())
					.setTimeout(route.getTimeout())
					.setPermission(route.getPermission())
					.setMethod(route.getToMethod())
					.build();
			 forwarder.forward(targetEndpoint, context.request(), context.response());
		});
	}

	public void route(JsonObject routes) {
		ProxyRequestForwarder proxyForwarder = new ProxyRequestForwarder(vertx);
		routes.forEach((entry) -> {
			logger.info(String.format("Mapping API %s ...", entry.getKey()));
			JsonObject apiConfig = (JsonObject) entry.getValue();
			String url = apiConfig.getString("url");
			Long timeout = apiConfig.getLong("timeout");
			String permission = apiConfig.getString("permission");
			JsonObject bind = apiConfig.getJsonObject("bind");
			if (bind != null && bind.getBoolean("active")) {
				String path = bind.getString("path");
				Boolean appendPath = bind.getBoolean("append_path");
				route(url, path, timeout, permission, appendPath, proxyForwarder);
			}
			JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
			endpointsConfig.forEach((conf) -> {
				JsonObject json = (JsonObject) conf;
				route(ProxyEndpointRoute.from(url, json, timeout), proxyForwarder);
			});
			logger.info(String.format("API %s mapped successfully.", entry.getKey()));
		});
	}

	public Route route(String url, String targetPath, Boolean appendPath, RequestForwarder forwarder) {
		return route(url, targetPath, null, null, appendPath, forwarder);
	}

	public Route route(String url, String targetPath, RequestForwarder forwarder) {
		return route(url, targetPath, null, null, Boolean.FALSE, forwarder);
	}
	
}
