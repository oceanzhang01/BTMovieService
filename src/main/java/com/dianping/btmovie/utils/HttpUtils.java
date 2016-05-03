package com.dianping.btmovie.utils;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by oceanzhang on 15/8/8.
 */
public class HttpUtils {
    private static OkHttpClient httpClient = new OkHttpClient();
    public static String get(String url) throws IOException {
        return get(url,null);
    }
    public static String get(String url,Map<String,String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).get();
        if (headers!=null){
            Set<String>keySet = headers.keySet() ;
            for(String key:keySet){
                builder=builder.header(key,headers.get(key));
            }
        }

        Response response = httpClient.newCall(builder.build()).execute() ;
        if(response.isSuccessful())
            return response.body().string();
        throw new IOException("http get error! url:"+url+" code:"+response.code());
    }


    public static String post(String url, Map<String,String> params) throws IOException {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if(params != null && params.size() > 0){
            for(String key:params.keySet()){
                builder.add(key,params.get(key));
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    public static final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static String post(String url, String params) throws IOException {
        RequestBody body = RequestBody.create(mediaType,params);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

}
