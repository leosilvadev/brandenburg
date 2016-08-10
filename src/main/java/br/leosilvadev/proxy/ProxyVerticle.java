package br.leosilvadev.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class ProxyVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		
		vertx.fileSystem().readFile("applications.json", (result) -> {
			Buffer buffer = result.result();
			JsonObject config = buffer.toJsonObject();
			config.forEach((entry) -> {
				System.out.println(String.format("Mapping API %s ...", entry.getKey()));
				JsonObject apiConfig = (JsonObject) entry.getValue();
				String url = apiConfig.getString("url");
				JsonArray endpointsConfig = apiConfig.getJsonArray("endpoints");
				endpointsConfig.forEach((conf) -> {
					JsonObject endpointConfig = (JsonObject) conf;
					String method = endpointConfig.getString("method");
					String pathFrom = endpointConfig.getString("pathFrom");
					String pathTo = endpointConfig.getString("pathTo");
					buildRoute(router, method, url, pathFrom, pathTo);
				});
				System.out.println(String.format("API %s mapped successfully.", entry.getKey()));
			});
		});
		
		server.requestHandler(router::accept);
		server.listen(8000);
	}
	
	private void buildRoute(Router router, String method, String url, String pathFrom, String pathTo) {
		String finalPathFrom = pathFrom.startsWith("/") ? pathFrom : "/" + pathFrom;
		String finalPathTo = pathTo.startsWith("/") ? pathTo : "/" + pathTo;
		
		HttpMethod httpMethod = HttpMethod.valueOf(method.toString());
		router.route(httpMethod, finalPathFrom).handler((context) -> {
			HttpClient client = vertx.createHttpClient();
			HttpClientRequest request = client.requestAbs(httpMethod, url + finalPathTo, (response) -> {
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
	}

}
