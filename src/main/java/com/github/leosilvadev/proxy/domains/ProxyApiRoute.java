package com.github.leosilvadev.proxy.domains;

import io.vertx.core.json.JsonObject;

public class ProxyApiRoute {

  private final String url;
  private final String targetPath;
  private final Long timeout;
  private final Boolean appendPath;

  public ProxyApiRoute(final String url, final String targetPath, final Long timeout, final Boolean appendPath) {
    super();
    this.url = url;
    this.targetPath = targetPath;
    this.timeout = timeout;
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

  public Boolean getAppendPath() {
    return appendPath;
  }

  public static ProxyApiRoute from(final String url, final JsonObject json, final Long timeout) {
    final String path = json.getString("path");

    if (path == null || path.isEmpty())
      throw new IllegalArgumentException("Api Routing must have a path to map");

    final Boolean appendPath = json.getBoolean("append_path");
    return new ProxyApiRoute(url, path, timeout, appendPath == null ? Boolean.TRUE : appendPath);
  }
}
