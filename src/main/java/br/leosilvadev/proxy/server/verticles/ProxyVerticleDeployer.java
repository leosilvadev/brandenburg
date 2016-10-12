package br.leosilvadev.proxy.server.verticles;

import br.leosilvadev.proxy.config.ApplicationConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ProxyVerticleDeployer {

	public void deploy(Vertx vertx, ApplicationConfig applicationConfig) {
		JsonObject json = new JsonObject()
				.put(ApplicationConfig.PORT_JSON, applicationConfig.getPort())
				.put(ApplicationConfig.ROUTES_PATH_JSON, applicationConfig.getRoutesPath());
		vertx.deployVerticle(new ProxyVerticle(), new DeploymentOptions().setConfig(json));
	}

}
