package br.leosilvadev.proxy.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.leosilvadev.proxy.config.ApplicationConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@Component
public class ProxyVerticleDeployer {

	@Autowired
	Vertx vertx;
	@Autowired
	ApplicationConfig applicationConfig;

	@PostConstruct
	public void deploy() {
		JsonObject json = new JsonObject()
				.put("port", applicationConfig.getPort())
				.put("routesPath", applicationConfig.getRoutesPath());
		vertx.deployVerticle(new ProxyVerticle(), new DeploymentOptions().setConfig(json));
	}

}
