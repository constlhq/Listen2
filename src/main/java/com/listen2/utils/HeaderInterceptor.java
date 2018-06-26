package com.listen2.utils;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

import java.io.IOException;

public class HeaderInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {

    Request originalRequest = chain.request();
    String originalUrl = originalRequest.url().toString();
    Headers originalHeaders = originalRequest.headers();
    Builder newBuilder = originalRequest.newBuilder();
    String referer_value="";

    if (originalUrl.contains("://music.163.com/")) {
      referer_value = "http://music.163.com/";
    }
    else if (originalUrl.contains("xiami.com/")) {
      referer_value = "http://m.xiami.com/";
    }

    else if(( originalUrl.contains("y.qq.com/")) ||
            (originalUrl.contains("qqmusic.qq.com/")) ||
            (originalUrl.contains("music.qq.com/")) ||
            (originalUrl.contains("imgcache.qq.com/"))) {

      referer_value = "http://y.qq.com/";
    }

    boolean modified_referer = false;
    for (int i = 0; i < originalHeaders.size(); i++) {

      if ((originalHeaders.name(i).equalsIgnoreCase("Referer")) && (!referer_value.isEmpty())) {
        newBuilder.addHeader(originalHeaders.name(i),referer_value);
        modified_referer = true;
      }
      if ((originalHeaders.name(i).equalsIgnoreCase("Origin")) && (!referer_value.isEmpty())) {
        newBuilder.addHeader(originalHeaders.name(i),referer_value);
      }
    }

    if (!modified_referer){
      newBuilder.addHeader("Referer",referer_value);
    }

    return chain.proceed(newBuilder.build());

  }

}