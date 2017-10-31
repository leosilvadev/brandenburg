package com.github.leosilvadev.proxy.domains;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.leosilvadev.proxy.utils.HttpMethodUtils;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CorsRoute {

  private final String allowedOriginPattern;
  private final Boolean allowCredentials;
  private final Set<String> allowHeaders;
  private final Set<HttpMethod> allowMethods;
  private final Set<String> exposeHeaders;

  private final HttpMethod fromMethod;
  private final String fromPath;

  public CorsRoute(final String allowedOriginPattern, final Boolean allowCredentials, final List<String> allowHeaders,
                   final List<HttpMethod> allowMethods, final List<String> exposeHeaders, final HttpMethod fromMethod,
                   final String fromPath) {
    super();
    this.allowedOriginPattern = allowedOriginPattern;
    this.allowCredentials = allowCredentials;
    this.allowHeaders = new HashSet<>(allowHeaders);
    this.allowMethods = new HashSet<>(allowMethods);
    this.exposeHeaders = new HashSet<>(exposeHeaders);
    this.fromMethod = fromMethod;
    this.fromPath = fromPath;
  }

  public String getAllowedOriginPattern() {
    return allowedOriginPattern;
  }

  public Boolean getAllowCredentials() {
    return allowCredentials;
  }

  public Set<String> getAllowHeaders() {
    return allowHeaders;
  }

  public Set<HttpMethod> getAllowMethods() {
    return allowMethods;
  }

  public Set<String> getExposeHeaders() {
    return exposeHeaders;
  }

  public HttpMethod getFromMethod() {
    return fromMethod;
  }

  public String getFromPath() {
    return fromPath;
  }

  @SuppressWarnings("unchecked")
  public static CorsRoute from(final JsonObject json) {
    if (json == null)
      return null;

    final HttpMethod fromMethod = HttpMethodUtils.from(json);
    final String fromPath = json.getString("path");
    Boolean allowCredentials;
    String allowedOriginPattern;
    JsonArray allowHeaders;
    JsonArray allowMethods;
    JsonArray exposeHeaders;
    final JsonObject allowConfig = json.getJsonObject("allow");

    if (allowConfig == null)
      return null;

    allowCredentials = allowConfig.getBoolean("credentials", false);
    allowedOriginPattern = allowConfig.getString("origin_pattern", "*");
    allowHeaders = allowConfig.getJsonArray("headers", new JsonArray());
    allowMethods = allowConfig.getJsonArray("methods", new JsonArray());

    final JsonObject exposeConfig = json.getJsonObject("expose", new JsonObject().put("headers", new JsonArray()));
    exposeHeaders = exposeConfig.getJsonArray("headers");

    return new CorsRoute(
        allowedOriginPattern,
        allowCredentials,
        allowHeaders.getList(),
        allowMethods.getList(),
        exposeHeaders.getList(),
        fromMethod,
        fromPath
    );
  }
}
