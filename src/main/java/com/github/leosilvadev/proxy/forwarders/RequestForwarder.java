package com.github.leosilvadev.proxy.forwarders;

import com.github.leosilvadev.proxy.domains.TargetEndpoint;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface RequestForwarder {
  
  void forward(final TargetEndpoint endpoint, final HttpServerRequest cliRequest, final HttpServerResponse cliResponse);
  
}
