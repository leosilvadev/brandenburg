package com.github.leosilvadev.proxy.domains;

import com.github.leosilvadev.proxy.utils.HttpMethodUtils;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class ProxyEndpointRoute {
  
  private final String url;
  private final HttpMethod fromMethod;
  private final String fromPath;
  private final HttpMethod toMethod;
  private final String toPath;
  private Long timeout;
  
  public ProxyEndpointRoute(final String url, final HttpMethod fromMethod, final String fromPath,
                            final HttpMethod toMethod, final String toPath) {
    super();
    this.url = url;
    this.fromMethod = fromMethod;
    this.fromPath = fromPath;
    this.toMethod = toMethod;
    this.toPath = toPath;
  }
  
  public ProxyEndpointRoute(final String url, final HttpMethod fromMethod, final String fromPath,
                            final HttpMethod toMethod, final String toPath,
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
    return fromMethod != null;
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
  
  private String pathOf(final String path) {
    return path.startsWith("/") ? path : "/" + path;
  }
  
  public static ProxyEndpointRoute from(final String url, final JsonObject json, final Long defaultTimeout) {
    final Long timeout = json.getLong("timeout", defaultTimeout);
    final JsonObject from = json.getJsonObject("from");
    
    if (from == null)
      throw new IllegalArgumentException("Api Endpoint required a 'from' mapping");

    final HttpMethod fromMethod = HttpMethodUtils.from(from);
    final String fromPath = from.getString("path");
    
    if (fromPath == null || fromPath.isEmpty())
      throw new IllegalArgumentException("Api Endpoint required a path mapping");

    final JsonObject to = json.getJsonObject("to") == null ? from : json.getJsonObject("to");
    final HttpMethod toMethod = HttpMethodUtils.from(to);
    final String toPath = to.getString("path") == null ? fromPath : to.getString("path");
    
    return new ProxyEndpointRoute(url, fromMethod, fromPath, toMethod, toPath, timeout);
  }
}
