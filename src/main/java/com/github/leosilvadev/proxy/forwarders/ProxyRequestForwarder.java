package com.github.leosilvadev.proxy.forwarders;

import com.github.leosilvadev.proxy.domains.TargetEndpoint;
import com.github.leosilvadev.proxy.forwarders.resolvers.ResponseErrorResolver;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ProxyRequestForwarder implements RequestForwarder {

	private static final Logger logger = LoggerFactory.getLogger(ProxyRequestForwarder.class);

	private final Vertx vertx;

	public ProxyRequestForwarder(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void forward(TargetEndpoint endpoint, HttpServerRequest cliRequest, HttpServerResponse cliResponse) {
		HttpClient client = vertx.createHttpClient();
		logger.info(String.format("Requesting %s to %s", endpoint.getMethod(), endpoint.getUrl()));
		HttpClientRequest request = buildRequest(endpoint, cliResponse, client);
		cliRequest.bodyHandler(fillRequestAndSend(request, cliRequest.headers()));
	}

	private HttpClientRequest buildRequest(TargetEndpoint endpoint, HttpServerResponse cliResponse, HttpClient client) {
		HttpClientRequest request = client.requestAbs(endpoint.getMethod(), endpoint.getUrl(), handleResponse(cliResponse))
				.exceptionHandler(handleException(cliResponse));
		
		if (endpoint.hasTimeout()) request.setTimeout(endpoint.getTimeout());
		
		return request;
	}

	private Handler<HttpClientResponse> handleResponse(HttpServerResponse cliResponse) {
		return (response) -> {
			cliResponse.headers().setAll(response.headers());
			cliResponse.setStatusCode(response.statusCode());
			response.bodyHandler(respondTo(cliResponse));
		};
	}

	private Handler<Buffer> respondTo(HttpServerResponse cliResponse) {
		return (buffer) -> {
			if (buffer != null) {
				cliResponse.putHeader("Content-Length", String.valueOf(buffer.length())).write(buffer);
			}
			cliResponse.end();
		};
	}

	private Handler<Throwable> handleException(HttpServerResponse cliResponse) {
		return (ex) -> {
			Integer status = ResponseErrorResolver.resolveStatus(ex);
			logger.error(ex.getMessage(), ex);
			cliResponse.setStatusCode(status).end(ex.getMessage());
		};
	}

	private Handler<Buffer> fillRequestAndSend(HttpClientRequest request, MultiMap headers) {
		return (body) -> {
			if (body != null) {
				request.putHeader("Content-Length", String.valueOf(body.length())).write(body);
			}
			request.headers().setAll(headers);
			request.end();
		};
	}
}
