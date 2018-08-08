package com.listen2.utils;


import okhttp3.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UtilTools {
  OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HeaderInterceptor()).build();

  static String getParameterByName(String param,String url){
    String param_value="";

    if(url.startsWith("/")){
      url = "http:/"+url;
    }
    try {
      URI rawExtras = new URI(url);
      List<NameValuePair> extraList = URLEncodedUtils.parse(rawExtras, Charset.forName("UTF-8"));
      for (NameValuePair item : extraList) {
        String name = item.getName();
        if(name.equals(param)){
          param_value = item.getValue();
          break;
        }
      }
      return param_value;
    } catch (URISyntaxException e) {
      return "";
    }
  }

  static String get(String url){
    Request request = new Request.Builder()
            .url(url)
            .build();
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
      return response.body().string();
    }catch (IOException e){
      return null;
    }
  }

  static String post(String url , Map<String,String> formData) throws Exception {
    Set<Map.Entry<String,String>> entry = formData.entrySet();
    FormBody.Builder formBodyBuilder = new FormBody.Builder();
    entry.forEach(ent-> formBodyBuilder.add(ent.getKey(),ent.getValue()));
    RequestBody formBody = formBodyBuilder.build();
    Request request = new Request.Builder()
            .url(url)
            .header("Content-Type","application/x-www-form-urlencoded")
            .header("Cookie","_iuqxldmzr_=a; _ntes_nnid=a; _ntes_nuid=a; usertrack=a; _ga=a; mail_psc_fingerprint=a;")
            .post(formBody)
            .build();
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
      return response.body().string();
    }catch (IOException e){
      e.printStackTrace();
      return null;
    }
  }

}


