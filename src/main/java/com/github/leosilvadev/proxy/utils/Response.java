package com.github.leosilvadev.proxy.utils;

import io.vertx.ext.web.RoutingContext;

public class Response {
  
  public static void ok(final RoutingContext context, String body) {
    respond(context, body, 200);
  }
  
  public static void ok(final RoutingContext context) {
    respond(context, 200);
  }
  
  public static void created(final RoutingContext context, String body) {
    respond(context, body, 201);
  }
  
  public static void created(final RoutingContext context) {
    respond(context, 201);
  }
  
  public static void accepted(final RoutingContext context, String body) {
    respond(context, body, 202);
  }
  
  public static void accepted(final RoutingContext context) {
    respond(context, 202);
  }
  
  public static void badRequest(final RoutingContext context, String body) {
    respond(context, body, 400);
  }
  
  public static void badRequest(final RoutingContext context) {
    respond(context, 400);
  }
  
  public static void unauthorized(final RoutingContext context, String body) {
    respond(context, body, 401);
  }
  
  public static void unauthorized(final RoutingContext context) {
    respond(context, 401);
  }
  
  public static void forbidden(final RoutingContext context, String body) {
    respond(context, body, 403);
  }
  
  public static void forbidden(final RoutingContext context) {
    respond(context, 403);
  }
  
  public static void notFound(final RoutingContext context, String body) {
    respond(context, body, 404);
  }
  
  public static void notFound(final RoutingContext context) {
    respond(context, 404);
  }
  
  public static void internalServerError(final RoutingContext context, String body) {
    respond(context, body, 500);
  }
  
  public static void internalServerError(final RoutingContext context) {
    respond(context, 500);
  }
  
  public static void respond(final RoutingContext context, final String body, final Integer status) {
    context.response().setStatusCode(status).setChunked(true).end(body);
  }
  
  public static void respond(final RoutingContext context, final Integer status) {
    context.response().setStatusCode(status).end();
  }
}
