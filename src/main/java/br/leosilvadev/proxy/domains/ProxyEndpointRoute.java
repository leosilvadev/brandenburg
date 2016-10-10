package br.leosilvadev.proxy.domains;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class ProxyEndpointRoute {

	private final String url;
	private final HttpMethod fromMethod;
	private final String fromPath;
	private final HttpMethod toMethod;
	private final String toPath;
	private Long timeout;
	private String permission;

	public ProxyEndpointRoute(String url, HttpMethod fromMethod, String fromPath, HttpMethod toMethod, String toPath) {
		super();
		this.url = url;
		this.fromMethod = fromMethod;
		this.fromPath = fromPath;
		this.toMethod = toMethod;
		this.toPath = toPath;
	}
	
	public ProxyEndpointRoute(String url, HttpMethod fromMethod, String fromPath, HttpMethod toMethod, String toPath, Long timeout, String permission) {
		this(url, fromMethod, fromPath, toMethod, toPath);
		this.timeout = timeout;
		this.permission = permission;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUrlTo() {
		return getUrl() + getToPath();
	}
	
	public HttpMethod getFromMethod() {
		return fromMethod;
	}

	public String getFromPath() {
		return pathOf(fromPath);
	}

	public HttpMethod getToMethod() {
		return toMethod;
	}

	public String getToPath() {
		return pathOf(toPath);
	}
	
	public Long getTimeout() {
		return timeout;
	}
	
	public String getPermission() {
		return permission;
	}

	private String pathOf(String path) {
		return path.startsWith("/") ? path : "/" + path;
	}
	
	public static ProxyEndpointRoute from(String url, JsonObject json, Long defaultTimeout) {
		JsonObject from = json.getJsonObject("from");
		HttpMethod fromMethod = HttpMethod.valueOf(from.getString("method"));
		String fromPath = from.getString("path");
		JsonObject to = json.getJsonObject("to");
		HttpMethod toMethod = to.getString("method") != null ? HttpMethod.valueOf(to.getString("method")) : fromMethod;
		String toPath = to.getString("path");
		Long timeout = to.getLong("timeout", defaultTimeout);
		String permission = to.getString("permission");
		return new ProxyEndpointRoute(url, fromMethod, fromPath, toMethod, toPath, timeout, permission);
	}
}
