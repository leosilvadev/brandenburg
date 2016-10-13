package br.leosilvadev.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.leosilvadev.proxy.server.verticles.ProxyVerticleDeployer;
import io.vertx.core.Vertx;

@Configuration
public class ProxyConfigurer {

	@Value("${server.port}")
	private Integer port;
	
	@Value("${proxy.routes.path}")
	private String path;
	
	@Bean
	public ApplicationConfig applicationConfig() {
		return new ApplicationConfig(port, path);
	}
	
	@Bean
	public Vertx vertx() {
		return Vertx.vertx();
	}

	@Bean
	public ProxyVerticleDeployer proxyVerticleDeployer() {
		ProxyVerticleDeployer deployer = new ProxyVerticleDeployer();
		deployer.deploy(vertx(), applicationConfig());
		return deployer;
	}
}
