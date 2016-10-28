package com.github.leosilvadev.proxy.utils;

import io.vertx.ext.web.RoutingContext;

public class Response {
  
  public static void ok(RoutingContext context, String body) {
    respond(context, body, 200);
  }
  
  public static void ok(RoutingContext context) {
    respond(context, 200);
  }
  
  public static void created(RoutingContext context, String body) {
    respond(context, body, 201);
  }
  
  public static void created(RoutingContext context) {
    respond(context, 201);
  }
  
  public static void accepted(RoutingContext context, String body) {
    respond(context, body, 202);
  }
  
  public static void accepted(RoutingContext context) {
    respond(context, 202);
  }
  
  public static void badRequest(RoutingContext context, String body) {
    respond(context, body, 400);
  }
  
  public static void badRequest(RoutingContext context) {
    respond(context, 400);
  }
  
  public static void unauthorized(RoutingContext context, String body) {
    respond(context, body, 401);
  }
  
  public static void unauthorized(RoutingContext context) {
    respond(context, 401);
  }
  
  public static void forbidden(RoutingContext context, String body) {
    respond(context, body, 403);
  }
  
  public static void forbidden(RoutingContext context) {
    respond(context, 403);
  }
  
  public static void notFound(RoutingContext context, String body) {
    respond(context, body, 404);
  }
  
  public static void notFound(RoutingContext context) {
    respond(context, 404);
  }
  
  public static void internalServerError(RoutingContext context, String body) {
    respond(context, body, 500);
  }
  
  public static void internalServerError(RoutingContext context) {
    respond(context, 500);
  }
  
  public static void respond(RoutingContext context, String body, Integer status) {
    context.response().setStatusCode(status).setChunked(true).end(body);
  }
  
  public static void respond(RoutingContext context, Integer status) {
    context.response().setStatusCode(status).end();
  }
}
