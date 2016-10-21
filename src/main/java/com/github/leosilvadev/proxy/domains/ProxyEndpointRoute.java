package com.github.leosilvadev.proxy.domains;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class ProxyEndpointRoute {

	private final String url;
	private final HttpMethod fromMethod;
	private final String fromPath;
	private final HttpMethod toMethod;
	private final String toPath;
	private Long timeout;

	public ProxyEndpointRoute(String url, HttpMethod fromMethod, String fromPath, HttpMethod toMethod, String toPath) {
		super();
		this.url = url;
		this.fromMethod = fromMethod;
		this.fromPath = fromPath;
		this.toMethod = toMethod;
		this.toPath = toPath;
	}

	public ProxyEndpointRoute(String url, HttpMethod fromMethod, String fromPath, HttpMethod toMethod, String toPath,
			Long timeout) {
		this(url, fromMethod, fromPath, toMethod, toPath);
		this.timeout = timeout;
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

	public Boolean isThereFromMethod() {
		return fromMethod!=null;
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

	private String pathOf(String path) {
		return path.startsWith("/") ? path : "/" + path;
	}

	public static ProxyEndpointRoute from(String url, JsonObject json, Long defaultTimeout) {
		Long timeout = json.getLong("timeout", defaultTimeout);

		JsonObject from = json.getJsonObject("from");
		
		if (from==null)
			throw new IllegalArgumentException("Api Endpoint required a 'from' mapping");
		
		String methodStr = from.getString("method");
		HttpMethod fromMethod = methodStr == null ? null : HttpMethod.valueOf(methodStr);
		String fromPath = from.getString("path");

		if (fromPath==null || fromPath.isEmpty())
			throw new IllegalArgumentException("Api Endpoint required a path mapping");
		
		JsonObject to = json.getJsonObject("to") == null ? from : json.getJsonObject("to");		
		HttpMethod toMethod = to.getString("method") == null ? fromMethod : HttpMethod.valueOf(to.getString("method"));
		String toPath = to.getString("path") == null ? fromPath : to.getString("path");

		return new ProxyEndpointRoute(url, fromMethod, fromPath, toMethod, toPath, timeout);
	}
}
