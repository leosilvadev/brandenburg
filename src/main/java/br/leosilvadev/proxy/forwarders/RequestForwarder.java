package br.leosilvadev.proxy.forwarders;

import br.leosilvadev.proxy.domains.TargetEndpoint;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public interface RequestForwarder {
	
	void forward(TargetEndpoint endpoint, HttpServerRequest cliRequest, HttpServerResponse cliResponse);

}
