package com.github.leosilvadev.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class ProxyApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(ProxyApplication.class, args);
  }
  
}
