package br.leosilvadev.proxy.config;

public class ApplicationConfig {
	
	private Integer port;
	private String routesPath;

	private String cachingHost;
	private Integer cachingPort;
	private String cachingEncoding;
	private String cachingAuth;

	public ApplicationConfig(Integer port, String routesPath, String cachingHost, Integer cachingPort,
			String cachingEncoding, String cachingAuth) {
		super();
		this.port = port;
		this.routesPath = routesPath;
		this.cachingHost = cachingHost;
		this.cachingPort = cachingPort;
		this.cachingEncoding = cachingEncoding;
		this.cachingAuth = cachingAuth;
	}
	
	public Integer getPort() {
		return port;
	}
	public String getRoutesPath() {
		return routesPath;
	}
	public String getCachingHost() {
		return cachingHost;
	}
	public Integer getCachingPort() {
		return cachingPort;
	}
	public String getCachingEncoding() {
		return cachingEncoding;
	}
	public String getCachingAuth() {
		return cachingAuth;
	}
}
