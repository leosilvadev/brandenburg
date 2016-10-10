package br.leosilvadev.proxy.server.verticles;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.leosilvadev.proxy.config.ApplicationConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@Component
public class ProxyVerticleDeployer {

	@Autowired Vertx vertx;
	@Autowired ApplicationConfig applicationConfig;

	@PostConstruct
	public void deploy() {
		JsonObject json = new JsonObject()
				.put(ApplicationConfig.PORT_JSON, applicationConfig.getPort())
				.put(ApplicationConfig.ROUTES_PATH_JSON, applicationConfig.getRoutesPath());
		vertx.deployVerticle(new ProxyVerticle(), new DeploymentOptions().setConfig(json));
	}

}
