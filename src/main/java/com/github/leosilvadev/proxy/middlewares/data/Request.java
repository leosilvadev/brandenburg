package com.github.leosilvadev.proxy.middlewares.data;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class Request {
	
	private final RoutingContext context;

	public Request(RoutingContext context) {
		this.context = context;
	}

	public JsonObject data() {
		return context.getBodyAsJson();
	}
}
