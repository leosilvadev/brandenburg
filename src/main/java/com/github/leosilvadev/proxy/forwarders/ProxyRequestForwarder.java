package com.github.leosilvadev.proxy.forwarders;

import com.github.leosilvadev.proxy.domains.TargetEndpoint;
import com.github.leosilvadev.proxy.forwarders.resolvers.ResponseErrorResolver;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Map.Entry;

public class ProxyRequestForwarder implements RequestForwarder {

  private static final Logger logger = LoggerFactory.getLogger(ProxyRequestForwarder.class);

  private final Vertx vertx;

  public ProxyRequestForwarder(final Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void forward(final TargetEndpoint endpoint, final HttpServerRequest cliRequest, final HttpServerResponse cliResponse) {
    final String targetUrl = endpoint.getUrl() + queryParams(cliRequest);
    logger.info("Requesting {0} to {1}", endpoint.getMethod(), targetUrl);
    final HttpClient client = vertx.createHttpClient();
    HttpClientRequest request = buildRequest(endpoint, cliRequest, cliResponse, client);
    cliRequest.bodyHandler(fillRequestAndSend(request, cliRequest.headers()));
  }

  private HttpClientRequest buildRequest(final TargetEndpoint endpoint, final HttpServerRequest cliRequest,
                                         final HttpServerResponse cliResponse, final HttpClient client) {
    final HttpMethod method = endpoint.getMethod() == null ? cliRequest.method() : endpoint.getMethod();
    final String targetUrl = endpoint.getUrl() + queryParams(cliRequest);
    final HttpClientRequest request = client.requestAbs(method, targetUrl, handleResponse(cliResponse, client))
        .exceptionHandler(handleException(cliResponse));

    if (endpoint.hasTimeout())
      request.setTimeout(endpoint.getTimeout());

    return request;
  }

  private String queryParams(final HttpServerRequest cliRequest) {
    final StringBuilder builder = new StringBuilder();
    Boolean firstParameter = true;

    for (Entry<String, String> entry : cliRequest.params()) {
      if (firstParameter) {
        builder.append("?");
        firstParameter = false;
      } else {
        builder.append("&");
      }
      builder.append(entry.getKey());
      builder.append("=");
      builder.append(entry.getValue());
    }
    return builder.toString();
  }

  private Handler<HttpClientResponse> handleResponse(final HttpServerResponse cliResponse,
                                                     final HttpClient client) {
    return (response) -> {
      cliResponse.headers().setAll(response.headers());
      cliResponse.setStatusCode(response.statusCode());
      response.bodyHandler(respondTo(cliResponse, client));
    };
  }

  private Handler<Buffer> respondTo(final HttpServerResponse cliResponse, final HttpClient client) {
    return (buffer) -> {
      if (buffer != null) {
        cliResponse.putHeader("Content-Length", String.valueOf(buffer.length())).write(buffer);
      }
      cliResponse.end();
      client.close();
    };
  }

  private Handler<Throwable> handleException(final HttpServerResponse cliResponse) {
    return (ex) -> {
      final Integer status = ResponseErrorResolver.resolveStatus(ex);
      logger.error(ex.getMessage(), ex);
      cliResponse.setStatusCode(status).end(ex.getMessage());
    };
  }

  private Handler<Buffer> fillRequestAndSend(final HttpClientRequest request, final MultiMap headers) {
    return (body) -> {
      if (body != null && body.length() > 0) {
        request.putHeader("Content-Length", String.valueOf(body.length())).write(body);
      }
      request.headers().setAll(headers);
      request.headers().remove("Host");
      request.end();
    };
  }
}
