package com.github.leosilvadev.proxy.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.leosilvadev.proxy.middlewares.Middleware;
import com.github.leosilvadev.proxy.server.verticles.ProxyVerticleDeployer;

import io.vertx.core.Vertx;

@Configuration
public class ProxyConfigurer {

	@Autowired(required=false)
	private List<Middleware> middlewares;
	
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
		deployer.deploy(vertx(), applicationConfig(), middlewares == null ? new ArrayList<>() : middlewares);
		return deployer;
	}
}
