package br.leosilvadev.proxy.forwarders;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ProxyForwarder implements RequestForwarder {

	private static final Logger logger = LoggerFactory.getLogger(ProxyForwarder.class);

	private final Vertx vertx;

	public ProxyForwarder(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void forward(HttpMethod httpMethod, String url, HttpServerRequest cliRequest, HttpServerResponse cliResponse) {
		HttpClient client = vertx.createHttpClient();
		logger.info(String.format("Requesting %s to %s", httpMethod, url));
		HttpClientRequest request = buildRequest(httpMethod, url, cliResponse, client);
		cliRequest.bodyHandler(fillRequestAndSend(request, cliRequest.headers()));
	}

	private HttpClientRequest buildRequest(HttpMethod httpMethod, String url, HttpServerResponse cliResponse,
			HttpClient client) {
		return client.requestAbs(httpMethod, url, handleResponse(cliResponse))
				.exceptionHandler(handleException(cliResponse));
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
			logger.error(ex.getMessage(), ex);
			cliResponse.setStatusCode(500).end(ex.getMessage());
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
