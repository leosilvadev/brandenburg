package com.github.leosilvadev.proxy.utils;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class HttpMethodUtils {
  
  public static HttpMethod from(String method) {
    return method == null ? null : HttpMethod.valueOf(method);
  }
  
  public static HttpMethod from(JsonObject json) {
    String method = json.getString("method");
    return method == null ? null : HttpMethod.valueOf(method);
  }
  
}
