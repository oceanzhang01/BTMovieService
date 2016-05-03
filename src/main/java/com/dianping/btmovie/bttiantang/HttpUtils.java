package com.dianping.btmovie.bttiantang;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by oceanzhang on 16/3/4.
 */
public class HttpUtils {
    public static String queryHtml(String url)throws IOException{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(true);
        int code = connection.getResponseCode();
        if(code == 200){
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            return out.toString();
        }
        System.out.println("url:"+url+"   code:"+code);
        throw new IOException("error");
    }
    public static String queryDoubanHtml(String url)throws IOException{
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Cookie", "bid=\"CHQkcitHjEs\"; ct=y; ps=y; ue=\"546107362@qq.com\"; dbcl2=\"131594696:JeedZ96PQWY\"; ck=\"UnTy\"; ll=\"108296\"; _ga=GA1.2.1219823030.1437112339; ap=1; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1439216515%2C%22http%3A%2F%2Fwww.douban.com%2F%22%5D; __utmt_douban=1; __utmt=1; push_noty_num=0; push_doumail_num=0; _pk_id.100001.4cf6=03905762b1022984.1437112343.26.1439216518.1439198166.; _pk_ses.100001.4cf6=*; __utma=30149280.1219823030.1437112339.1439193100.1439216515.26; __utmb=30149280.2.10.1439216515; __utmc=30149280; __utmz=30149280.1439171429.24.10.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=30149280.13159; __utma=223695111.1988430535.1437112340.1439193100.1439216515.25; __utmb=223695111.2.10.1439216515; __utmc=223695111; __utmz=223695111.1439125194.23.15.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/tag/2015/movie");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
        connection.setInstanceFollowRedirects(true);
        int code = connection.getResponseCode();
        if(code == 200){
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            return out.toString();
        }
        System.out.println("url:"+url+"   code:"+code);
        throw new IOException("error");
    }
    public static byte[] downloadData(String url,String params)throws IOException{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        OutputStream output = connection.getOutputStream();
        output.write(params.getBytes());
        output.close();
        int code = connection.getResponseCode();
        if(code == 200){
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            return out.toByteArray();
        }
        throw new IOException("error");
    }
}
