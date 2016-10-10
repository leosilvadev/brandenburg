package br.leosilvadev.proxy.caching;

import br.leosilvadev.proxy.config.ApplicationConfig;
import io.vertx.core.Vertx;
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

	public Caching config(ApplicationConfig config) {
		if (client == null) {
			String host = config.getCachingHost();
			Integer port = config.getCachingPort();
			String encoding = config.getCachingEncoding();
			String auth = config.getCachingAuth();

			logger.info(
				String.format("Configuring Cache, host (%s), port (%s), encoding (%s), auth (%s)", host, port, encoding, auth)
			);
			
			RedisOptions options = new RedisOptions().setHost(host).setPort(port).setEncoding(encoding).setAuth(auth);
			this.client = RedisClient.create(vertx, options);
		}
		return this;
	}

}
