package com.github.leosilvadev.proxy.middlewares;

import com.github.leosilvadev.proxy.middlewares.data.Request;
import com.github.leosilvadev.proxy.middlewares.exceptions.Violation;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class Middleware implements Handler<RoutingContext> {

	public abstract void handleRequest(Request request);
	
	@Override
	public void handle(RoutingContext context) {
		try {
			handleRequest(new Request(context));
			context.next();
			
		} catch(Violation ex) {
			respondError(context, ex.getMessage(), ex.getStatus());
			
		} catch(Exception ex) {
			respondError(context, ex.getMessage(), 500);
		}
	}
	
	private void respondError(RoutingContext context, String message, Integer status) {
		JsonObject json = new JsonObject().put("message", message);
		context.response()
			.setStatusCode(status)
			.setChunked(true)
			.end(json.encode());
	}

	protected void fail(String message) {
		throw new RuntimeException(message);
	}

	protected void fail(String message, Throwable ex) {
		throw new RuntimeException(message, ex);
	}
	
	protected void violate(String message, Integer status) {
		throw new Violation(message);
	}
	
	public String path() {
		return null;
	}
	
	public HttpMethod httpMethod() {
		return null;
	}
	
}
