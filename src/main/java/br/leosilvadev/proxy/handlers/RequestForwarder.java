package br.leosilvadev.proxy.handlers;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface RequestForwarder {
	
	void forward(HttpMethod httpMethod, String url, HttpServerRequest cliRequest, HttpServerResponse cliResponse);

}
