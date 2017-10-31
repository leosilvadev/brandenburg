package com.github.leosilvadev.proxy.domains;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class TargetEndpoint {
  
  private final HttpMethod method;
  private final String url;
  private final String path;
  private final Long timeout;
  
  public TargetEndpoint(final HttpMethod method, final String url, final String path, final Long timeout) {
    this.method = method;
    this.url = url;
    this.path = path;
    this.timeout = timeout;
  }
  
  public HttpMethod getMethod() {
    return method;
  }
  
  public String getPath() {
    return path;
  }
  
  public String getUrl() {
    return url;
  }
  
  public Long getTimeout() {
    return timeout;
  }
  
  public Boolean hasTimeout() {
    return timeout != null;
  }
  
  public static class TargetEndpointBuilder {
    
    private final RoutingContext context;
    private final String url;
    private final String path;
    private HttpMethod method;
    private Long timeout;
    private Boolean appendPath;
    
    public TargetEndpointBuilder(final RoutingContext context, final String url, final String path) {
      this.context = context;
      this.url = url;
      this.path = path;
      this.appendPath = Boolean.FALSE;
    }
    
    public TargetEndpointBuilder setTimeout(final Long timeout) {
      this.timeout = timeout;
      return this;
    }
    
    public TargetEndpointBuilder appendPath(final Boolean appendPath) {
      this.appendPath = appendPath;
      return this;
    }
    
    public TargetEndpointBuilder setMethod(final HttpMethod method) {
      this.method = method;
      return this;
    }
    
    public TargetEndpoint build() {
      final HttpServerRequest request = context.request();
      final String targetPath = appendPath ? request.path() : request.path().replace(path, "");
      final String targetUrl = url + targetPath;
      method = method == null ? request.method() : method;
      return new TargetEndpoint(method, targetUrl, targetPath, timeout);
    }
    
  }
  
}
