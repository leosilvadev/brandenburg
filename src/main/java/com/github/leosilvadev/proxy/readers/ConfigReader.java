package com.github.leosilvadev.proxy.readers;

import java.util.function.Function;

import com.github.leosilvadev.proxy.server.ProxyServer;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ConfigReader {
  
  private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
  
  private final Vertx vertx;
  
  public ConfigReader(final Vertx vertx) {
    this.vertx = vertx;
  }
  
  public void read(final String path, final Function<JsonObject, Object> callback) {
    vertx.fileSystem().readFile(path, (fileResult) -> {
      final Buffer buffer = fileResult.result();
      if (buffer == null) {
        logger.error("Routes File {} not found!", path);
        
      } else {
        callback.apply(buffer.toJsonObject());
      }
    });
  }
  
}
