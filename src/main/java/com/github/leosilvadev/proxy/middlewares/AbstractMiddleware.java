package com.github.leosilvadev.proxy.middlewares;

import com.github.leosilvadev.proxy.utils.Response;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractMiddleware implements Handler<RoutingContext> {
  
  protected static final Logger logger = LoggerFactory.getLogger(AbstractMiddleware.class);
  
  public abstract void handleRequest(RoutingContext context);
  
  @Override
  public final void handle(final RoutingContext context) {
    try {
      handleRequest(context);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      Response.internalServerError(context);
    }
  }
  
  public String path() {
    return null;
  }
  
}
