package br.leosilvadev.proxy.server;

import br.leosilvadev.proxy.handlers.RequestForwarder;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class ProxyRouter {

	private final Router router;

	public ProxyRouter(Router router) {
		this.router = router;
	}

	public Route route(String url, String path, Boolean appendPath, RequestForwarder forwarder) {
		return router.route(String.format("%s/*", path)).handler((context) -> {
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
		return router.route(method, finalPathFrom).handler((context) -> {
			forwarder.forward(method, targetUrl, context.request(), context.response());
		});
	}

}
