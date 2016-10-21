package com.github.leosilvadev.proxy.middlewares;

import com.github.leosilvadev.proxy.utils.Response;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public abstract class Middleware implements Handler<RoutingContext> {

	protected static final Logger logger = LoggerFactory.getLogger(Middleware.class);
	
	public abstract void handleRequest(RoutingContext context);
	
	@Override
	public void handle(RoutingContext context) {
		try {
			handleRequest(context);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
			Response.internalServerError(context);
		}
	}
}
