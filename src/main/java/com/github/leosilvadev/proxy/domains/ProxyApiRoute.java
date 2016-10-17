package com.github.leosilvadev.proxy.domains;

import io.vertx.core.json.JsonObject;

public class ProxyApiRoute {

	private final String url;
	private final String targetPath;
	private final Long timeout;
	private final String permission;
	private final Boolean appendPath;

	public ProxyApiRoute(String url, String targetPath, Long timeout, String permission, Boolean appendPath) {
		super();
		this.url = url;
		this.targetPath = targetPath;
		this.timeout = timeout;
		this.permission = permission;
		this.appendPath = appendPath;
	}

	public String getUrl() {
		return url;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public Long getTimeout() {
		return timeout;
	}

	public String getPermission() {
		return permission;
	}

	public Boolean getAppendPath() {
		return appendPath;
	}

	public static ProxyApiRoute from(String url, JsonObject json, Long timeout, String permission) {
		String path = json.getString("path");
		Boolean appendPath = json.getBoolean("append_path");
		return new ProxyApiRoute(url, path, timeout, permission, appendPath);
	}
}
