package br.leosilvadev.proxy.caching;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class Caching {
	
	private static final Logger logger = LoggerFactory.getLogger(Caching.class);

	private final Vertx vertx;
	private RedisClient client;

	public Caching(Vertx vertx) {
		this.vertx = vertx;
	}

	public Caching config(JsonObject json) {
		if (client == null) {
			String host = json.getString("host", "localhost");
			Integer port = json.getInteger("port", 6379);
			String encoding = json.getString("encoding", "UTF-8");
			String auth = json.getString("auth");

			logger.info(
				String.format("Configuring Cache, host (%s), port (%s), encoding (%s), auth (%s)", host, port, encoding, auth)
			);
			RedisOptions options = new RedisOptions().setHost(host).setPort(port).setEncoding(encoding).setAuth(auth);

			this.client = RedisClient.create(vertx, options);
		}
		return this;
	}

}
