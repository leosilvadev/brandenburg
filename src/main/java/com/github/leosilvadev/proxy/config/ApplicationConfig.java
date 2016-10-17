package com.github.leosilvadev.proxy.config;

public class ApplicationConfig {

	public static final String PORT_JSON = "port";
	public static final String ROUTES_PATH_JSON = "routes_path";

	private Integer port;
	private String routesPath;

	public ApplicationConfig(Integer port, String routesPath) {
		super();
		this.port = port;
		this.routesPath = routesPath;
	}
	
	public Integer getPort() {
		return port;
	}
	public String getRoutesPath() {
		return routesPath;
	}
}
