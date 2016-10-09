package br.leosilvadev.proxy.security;

public interface Authorizer {
	
	public boolean authorize(String token);

}
