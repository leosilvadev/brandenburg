package com.github.leosilvadev.proxy.utils;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class HttpMethodUtils {
  
  public static HttpMethod from(final String method) {
    return method == null ? null : HttpMethod.valueOf(method);
  }
  
  public static HttpMethod from(final JsonObject json) {
    final String method = json.getString("method");
    return method == null ? null : HttpMethod.valueOf(method);
  }
  
}
