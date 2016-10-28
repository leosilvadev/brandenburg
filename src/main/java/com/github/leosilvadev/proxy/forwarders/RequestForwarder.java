package com.github.leosilvadev.proxy.forwarders;

import com.github.leosilvadev.proxy.domains.TargetEndpoint;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface RequestForwarder {
  
  void forward(TargetEndpoint endpoint, HttpServerRequest cliRequest, HttpServerResponse cliResponse);
  
}
