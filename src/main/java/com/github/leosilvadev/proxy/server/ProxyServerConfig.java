package com.github.leosilvadev.proxy.server;

public class ProxyServerConfig {

	public static String DEFAULT_ROUTES_FILE = "routes.json";
	private final Integer port;
	private final String routesFilePath;

	public ProxyServerConfig(Integer port) {
		super();
		this.port = port;
		this.routesFilePath = DEFAULT_ROUTES_FILE;
	}
	
	public ProxyServerConfig(Integer port, String routesFilePath) {
		super();
		this.port = port;
		this.routesFilePath = routesFilePath;
	}

	public Integer getPort() {
		return port;
	}
	
	public String getRoutesFilePath() {
		return routesFilePath;
	}
}
