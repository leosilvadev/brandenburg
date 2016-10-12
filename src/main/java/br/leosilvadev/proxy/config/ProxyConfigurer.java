package br.leosilvadev.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.leosilvadev.proxy.caching.Caching;
import br.leosilvadev.proxy.server.verticles.ProxyVerticleDeployer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

@Configuration
public class ProxyConfigurer {

	@Bean
	public ApplicationConfig applicationConfig() {
		Buffer buffer = vertx().fileSystem().readFileBlocking("application.json");
		JsonObject json = new JsonObject(buffer.toString());
		JsonObject jsonCaching = json.getJsonObject("caching", new JsonObject());
		return new ApplicationConfig(
			json.getInteger(ApplicationConfig.PORT_JSON, 8000), 
			json.getString(ApplicationConfig.ROUTES_PATH_JSON, "routes.json"),
			jsonCaching.getString(ApplicationConfig.CACHING_HOST_JSON, "localhost"), 
			jsonCaching.getInteger(ApplicationConfig.CACHING_PORT_JSON, 6379),
			jsonCaching.getString(ApplicationConfig.CACHING_ENCODING_JSON, "UTF-8"), 
			jsonCaching.getString(ApplicationConfig.CACHING_AUTH_JSON)
		);
	}
	
	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}

	@Bean
	public Caching caching() {
		return new Caching(vertx()).config(applicationConfig());
	}
	
	@Bean
	public ProxyVerticleDeployer proxyVerticleDeployer() {
		ProxyVerticleDeployer deployer = new ProxyVerticleDeployer();
		deployer.deploy(vertx(), applicationConfig());
		return deployer;
	}
}
