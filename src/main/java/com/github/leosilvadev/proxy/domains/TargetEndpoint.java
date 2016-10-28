package com.github.leosilvadev.proxy.domains;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class TargetEndpoint {
  
  private HttpMethod method;
  private String url;
  private String path;
  private Long timeout;
  
  public TargetEndpoint(HttpMethod method, String url, String path, Long timeout) {
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
    
    private RoutingContext context;
    private HttpMethod method;
    private String url;
    private String path;
    private Long timeout;
    private Boolean appendPath;
    
    public TargetEndpointBuilder(RoutingContext context, String url, String path) {
      this.context = context;
      this.url = url;
      this.path = path;
      this.appendPath = Boolean.FALSE;
    }
    
    public TargetEndpointBuilder setTimeout(Long timeout) {
      this.timeout = timeout;
      return this;
    }
    
    public TargetEndpointBuilder appendPath(Boolean appendPath) {
      this.appendPath = appendPath;
      return this;
    }
    
    public TargetEndpointBuilder setMethod(HttpMethod method) {
      this.method = method;
      return this;
    }
    
    public TargetEndpoint build() {
      HttpServerRequest request = context.request();
      String targetPath = appendPath ? request.path() : request.path().replace(path, "");
      String targetUrl = url + targetPath;
      method = method == null ? request.method() : method;
      return new TargetEndpoint(method, targetUrl, targetPath, timeout);
    }
    
  }
  
}
