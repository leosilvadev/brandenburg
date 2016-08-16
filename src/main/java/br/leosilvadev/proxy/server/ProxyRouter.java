package br.leosilvadev.proxy.server;

import br.leosilvadev.proxy.handlers.RequestForwarder;
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

	public Route route(HttpMethod method, String url, String pathFrom, String pathTo, RequestForwarder forwarder) {
		String finalPathFrom = pathFrom.startsWith("/") ? pathFrom : "/" + pathFrom;
		String finalPathTo = pathTo.startsWith("/") ? pathTo : "/" + pathTo;
		String targetUrl = url + finalPathTo;
		logger.info(String.format("Routing endpoint with method %s and path %s to api %s", method, finalPathFrom, targetUrl));
		return router.route(method, finalPathFrom).handler((context) -> {
			forwarder.forward(method, targetUrl, context.request(), context.response());
		});
	}

}
