package br.leosilvadev.proxy.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ResponseErrorResolver {

	private static final Map<Class<? extends Throwable>, Integer> errors;

	static {
		errors = new HashMap<>();
		errors.put(TimeoutException.class, 504);
	}

	public static Integer resolveStatus(Throwable ex) {
		for (Class<? extends Throwable> clazz : errors.keySet()) {
			if (clazz.isInstance(ex)) return errors.get(clazz);
		}
		return 500;
	}
}
