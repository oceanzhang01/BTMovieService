package com.dianping.btmovie.mp4ba;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by oceanzhang on 15/9/20.
 */
public class HttpHelper {
//    private static  String PROXY_IP = "58.220.10.7";
//    private static int PROXY_PORT = 80;
    public static String httpGet(String u) {
        return httpGet(u,null);
    }
    public static String httpGet(String u,HashMap<String,String> headers) {
        byte[] data = httpGetData(u,headers);
        if(data != null){
            try {
                return new String(data,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static byte[] httpGetData(String u,HashMap<String,String> headers) {
        try {
            URL url = new URL(u);
//            SocketAddress address = new InetSocketAddress(PROXY_IP,PROXY_PORT);
//            Proxy proxy = new Proxy(Proxy.Type.HTTP,address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            processRequestHeaders(conn,headers);
            return readData(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void processRequestHeaders(URLConnection conn, HashMap<String,String> headers){
        if (headers != null && !headers.isEmpty()) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                conn.setRequestProperty(key, headers.get(key));
            }
        }
//        String ip = randomIp();
//        conn.setRequestProperty("client-ip", ip);
//        conn.setRequestProperty("x-forwarded-for",ip);
    }
    private static String randomIp(){
        return new Random().nextInt(255)+"."+new Random().nextInt(255)+"."+new Random().nextInt(255)+"."+new Random().nextInt(255);
    }
    private static byte[] readData(HttpURLConnection conn) {
        BufferedInputStream in = null;
        try {
            conn.connect();
            int code = conn.getResponseCode();
            in = new BufferedInputStream(conn.getInputStream());
            int len;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                out.flush();
            }
            byte[] data = out.toByteArray();
            out.close();
//            for (int i = 0;; i++) {
//                String headerName = conn.getHeaderFieldKey(i);
//                String headerValue = conn.getHeaderField(i);
//                if (headerName == null && headerValue == null) {
//                    break;
//                }
//                System.out.println(headerName + ":" + headerValue);
//            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }


    public static String httpPost(String u, String data, HashMap<String, String> headers) {
        byte[] d = httpPostData(u,data,headers);
        if(d != null){
            try {
                return new String(d,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static byte[] httpPostData(String u, String data, HashMap<String, String> headers) {
        try {
            URL url = new URL(u);
//            SocketAddress address = new InetSocketAddress(PROXY_IP,PROXY_PORT);
//            Proxy proxy = new Proxy(Proxy.Type.HTTP,address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            processRequestHeaders(conn,headers);
            if (data != null) {
                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                out.write(data.getBytes("UTF-8"));
                out.flush();
                out.close();
            }
            return readData(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
