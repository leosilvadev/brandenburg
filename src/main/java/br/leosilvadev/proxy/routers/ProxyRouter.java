package br.leosilvadev.proxy.routers;

import br.leosilvadev.proxy.domains.ProxyEndpointRoute;
import br.leosilvadev.proxy.forwarders.RequestForwarder;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class ProxyRouter {

	private static final Logger logger = LoggerFactory.getLogger(ProxyRouter.class);
	
	private final Router router;

	public ProxyRouter(Router router) {
		this.router = router;
	}

	public Route route(String url, String path, Boolean appendPath, RequestForwarder forwarder) {
		String endpointPath = String.format("%s/*", path);
		logger.info(String.format("Routing all endpoints for %s to api %s", endpointPath, url));
		return router.route(endpointPath).handler((context) -> {
			HttpMethod method = context.request().method();
			String targetPath = appendPath ? context.request().path() : context.request().path().replace(path, "");
			String targetUrl = url + targetPath;
			forwarder.forward(method, targetUrl, context.request(), context.response());
		});
	}

	public Route route(ProxyEndpointRoute route, RequestForwarder forwarder) {
		String pathFrom = route.getFromPath();
		String urlTo = route.getUrlTo();
		logger.info(String.format("Routing endpoint with method %s and path %s to api %s", route.getFromMethod(), pathFrom, urlTo));
		return router.route(route.getFromMethod(), pathFrom).handler((context) -> {
			forwarder.forward(route.getToMethod(), urlTo, context.request(), context.response());
		});
	}

}
