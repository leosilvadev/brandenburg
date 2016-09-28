package br.leosilvadev.proxy.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;

public class ResponseErrorResolver {

	private static final Map<Class<? extends Throwable>, Integer> errors;

	static {
		errors = new HashMap<>();
		errors.put(TimeoutException.class, HttpStatus.SC_GATEWAY_TIMEOUT);
	}

	public static Integer resolveStatus(Throwable ex) {
		for (Class<? extends Throwable> clazz : errors.keySet()) {
			if (clazz.isInstance(ex)) return errors.get(clazz);
		}
		return HttpStatus.SC_INTERNAL_SERVER_ERROR;
	}
}
