package com.github.leosilvadev.proxy.middlewares;

import java.util.HashSet;
import java.util.Set;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

public abstract class CORSMiddleware extends AbstractMiddleware {
  
  protected static final Logger logger = LoggerFactory.getLogger(CORSMiddleware.class);
  
  private final CorsHandler corsHandler;
  
  public CORSMiddleware() {
    this.corsHandler = CorsHandler.create(allowedOriginPattern())
        .allowedHeaders(allowedHeaders())
        .allowedMethods(allowedMethods())
        .allowCredentials(allowCredentials())
        .exposedHeaders(exposedHeaders());
  }
  
  @Override
  public final void handleRequest(RoutingContext context) {
    corsHandler.handle(context);
  }
  
  public String allowedOriginPattern() {
    return "*";
  }
  
  public Set<String> allowedHeaders() {
    return new HashSet<>();
  }
  
  public Set<HttpMethod> allowedMethods() {
    return new HashSet<>();
  }
  
  public Boolean allowCredentials() {
    return false;
  }
  
  public Set<String> exposedHeaders() {
    return new HashSet<>();
  }
  
}
