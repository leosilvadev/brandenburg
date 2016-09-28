package br.leosilvadev.proxy.routers;

import br.leosilvadev.proxy.domains.ProxyEndpointRoute;
import br.leosilvadev.proxy.domains.TargetEndpoint;
import br.leosilvadev.proxy.domains.TargetEndpoint.TargetEndpointBuilder;
import br.leosilvadev.proxy.forwarders.RequestForwarder;
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

	public Route route(String url, String targetPath, Boolean appendPath, RequestForwarder forwarder) {
		return route(url, targetPath, null, null, appendPath, forwarder);
	}

	public Route route(String url, String targetPath, RequestForwarder forwarder) {
		return route(url, targetPath, null, null, Boolean.FALSE, forwarder);
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

}
