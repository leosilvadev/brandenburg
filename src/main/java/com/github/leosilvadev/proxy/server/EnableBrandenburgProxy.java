package com.github.leosilvadev.proxy.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.github.leosilvadev.proxy.config.ProxyConfigurer;

@ComponentScan
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ProxyConfigurer.class })
public @interface EnableBrandenburgProxy {
  
}
