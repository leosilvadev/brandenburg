package br.leosilvadev.proxy.server;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class ProxyServer {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);

	private final Vertx vertx;
	private final HttpServer server;
	private final Router router;
	private final ProxyServerConfig config;

	public ProxyServer(Vertx vertx, ProxyServerConfig config) {
		this.vertx = vertx;
		this.config = config;
		this.server = vertx.createHttpServer();
		this.router = Router.router(vertx);
	}

	public Future<ProxyServer> run() {
		Future<ProxyServer> future = Future.future();
		vertx.fileSystem().readFile(config.getRoutesFilePath(), (fileResult) -> {
			Buffer buffer = fileResult.result();
			if (buffer == null) {
				String message = String.format("Routes File %s not found!", config.getRoutesFilePath());
				logger.error(message, config.getRoutesFilePath());
				future.fail(message);

			} else {
				JsonObject routes = buffer.toJsonObject();
				routes.forEach((entry) -> {
					logger.info(String.format("Mapping API %s ...", entry.getKey()));
					JsonObject apiConfig = (JsonObject) entry.getValue();
					String url = apiConfig.getString("url");
					JsonObject bind = apiConfig.getJsonObject("bind");
					if (bind.getBoolean("active")) {
						String path = bind.getString("path");
						Boolean appendPath = bind.getBoolean("append_path");
						buildRelativeRoute(router, url, path, appendPath);
					}
					JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
					endpointsConfig.forEach((conf) -> {
						JsonObject endpointConfig = (JsonObject) conf;
						String method = endpointConfig.getString("method");
						String pathFrom = endpointConfig.getString("from");
						String pathTo = endpointConfig.getString("to");
						buildRoute(router, method, url, pathFrom, pathTo);
					});
					logger.info(String.format("API %s mapped successfully.", entry.getKey()));
				});
				server.requestHandler(router::accept);
				server.listen(config.getPort(), (serverResult) -> {
					if (serverResult.succeeded()) {
						logger.info(String.format("Proxy Server running on port %s", config.getPort()));
						future.complete(this);
					} else {
						logger.error("Error trying to run Proxy Server", serverResult.cause());
						future.fail(serverResult.cause());
					}
				});
			}
		});
		return future;
	}

	private void buildRelativeRoute(Router router, String url, String path, Boolean appendPath) {
		router.route(String.format("%s/*", path)).handler((context) -> {
			HttpMethod method = context.request().method();
			String targetPath = appendPath ? context.request().path() : context.request().path().replace(path, "");
			String targetUrl = url + targetPath;
			request(method, targetUrl, context.request(), context.response());
		});
	}

	private void buildRoute(Router router, String method, String url, String pathFrom, String pathTo) {
		String finalPathFrom = pathFrom.startsWith("/") ? pathFrom : "/" + pathFrom;
		String finalPathTo = pathTo.startsWith("/") ? pathTo : "/" + pathTo;
		String targetUrl = url + finalPathTo;
		HttpMethod httpMethod = HttpMethod.valueOf(method.toString());
		router.route(httpMethod, finalPathFrom).handler((context) -> {
			request(httpMethod, targetUrl, context.request(), context.response());
		});
	}

	private void request(HttpMethod httpMethod, String url, HttpServerRequest cliRequest, HttpServerResponse cliResponse) {
		HttpClient client = vertx.createHttpClient();
		logger.info(String.format("Requesting %s to %s", httpMethod, url));
		HttpClientRequest request = buildRequest(httpMethod, url, cliResponse, client);
		cliRequest.bodyHandler(fillRequestAndSend(request, cliRequest.headers()));
	}

	private HttpClientRequest buildRequest(HttpMethod httpMethod, String url, HttpServerResponse cliResponse, HttpClient client) {
		return client.requestAbs(httpMethod, url, handleResponse(cliResponse))
				.exceptionHandler(handleException(cliResponse));
	}

	private Handler<HttpClientResponse> handleResponse(HttpServerResponse cliResponse) {
		return (response) -> {
			MultiMap headers = response.headers();
			headers.forEach((entry) -> cliResponse.putHeader(entry.getKey(), entry.getValue()));
			response.bodyHandler(handleResponseBody(cliResponse));
		};
	}

	private Handler<Buffer> handleResponseBody(HttpServerResponse cliResponse) {
		return (buffer) -> {
			if (buffer != null) {
				cliResponse.setChunked(true).write(buffer);
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
