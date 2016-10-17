package com.github.leosilvadev.proxy.middlewares.exceptions;

public class Violation extends RuntimeException {

	private static final long serialVersionUID = 7101922930570393589L;

	private final Integer status;

	public Violation(String message) {
		super(message);
		this.status = 500;
	}

	public Violation(String message, Integer status) {
		super(message);
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

}
