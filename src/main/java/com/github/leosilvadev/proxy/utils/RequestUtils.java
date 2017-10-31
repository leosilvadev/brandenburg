package com.github.leosilvadev.proxy.utils;

import java.util.UUID;

/**
 * Created by leonardo on 10/31/17.
 */
public class RequestUtils {

  public static String generateId() {
    final String uuid = UUID.randomUUID().toString();
    return System.currentTimeMillis() + "-" + uuid;
  }

}
