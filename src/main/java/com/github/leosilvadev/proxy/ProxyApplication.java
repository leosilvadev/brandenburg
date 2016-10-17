package com.github.leosilvadev.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.github.leosilvadev.proxy.ProxyApplication;

@ComponentScan
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
