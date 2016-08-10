package br.leosilvadev.proxy;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class ProxyApplication {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		router.get("/v1/books").handler((context) -> {
			HttpClient client = vertx.createHttpClient();
			HttpClientRequest request = client.getAbs("http://localhost:8090/available", (response) -> {
				MultiMap headers = response.headers();
				HttpServerResponse cliResponse = context.response();
				cliResponse.headers().clear();
				headers.forEach((entry) -> {
					cliResponse.putHeader(entry.getKey(), entry.getValue());
				});
				response.bodyHandler((buffer) -> {
					if (buffer != null) {
						cliResponse.write(buffer);
					}
					cliResponse.end();
				});
			});
			request.exceptionHandler((ex) -> {
				ex.printStackTrace();
			}).end();
		});
		server.requestHandler(router::accept);
		server.listen(8000);
	}

}
