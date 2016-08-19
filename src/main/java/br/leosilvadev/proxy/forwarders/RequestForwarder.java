package br.leosilvadev.proxy.forwarders;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface RequestForwarder {
	
	void forward(HttpMethod httpMethod, String url, HttpServerRequest cliRequest, HttpServerResponse cliResponse);

}
