package com.dianping.btmovie.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by oceanzhang on 15/9/10.
 */
@WebServlet("/TestCurlServlet")
public class TestCurlServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        CookieManager cookieManager = new CookieManager();
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        CookieHandler.setDefault(cookieManager);
//        HttpURLConnection.setFollowRedirects(true);
//        checkLogin();
//        URL url = new URL("http://wappass.baidu.com/wp/api/login");
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        conn.setRequestProperty("Referer","http://wappass.baidu.com/passport/?login&tpl=wimn&ssid=0&from=844b&uid=&pu=sz%401320_1001%2Cta%40iphone_2_5.0_3_537&bd_page_type=1&tn=&regtype=1&u=http://m.baidu.com");
//        conn.setRequestProperty("User-Agent", "//Mozilla/5.0 (Linux; U; Android 5.0.2;zh-cn; XT1085/LXE22.92-30) AppleWebKit/537.36 (KHTML, like Gecko) Version/5.0.2 Mobile Safari/537.36");
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//        byte [] data = produceFormEntity("546107362@qq.com","zm921210");
//        OutputStream out = conn.getOutputStream();
//        out.write(data);
//        out.flush();
//        out.close();
//        conn.connect();
//        InputStream in = conn.getInputStream();
//        int code = conn.getResponseCode();
//        int len;
//        byte[] buffer = new byte[1024];
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        while((len = in.read(buffer)) != -1){
//            outputStream.write(buffer,0,len);
//            outputStream.flush();
//        }
//        String html = new String(outputStream.toByteArray(),"utf-8");
//        System.out.println(html);
//        outputStream.close();
    	int code = Integer.parseInt(request.getParameter("code")) ;
    	response.setStatus(code);
        PrintWriter pw = response.getWriter();
        jsonRerutn(pw, 0, "return login html", "");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
    private void checkLogin() throws IOException{
        URL url = new URL("http://pan.baidu.com/disk/home");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        int code = urlConnection.getResponseCode();
        InputStream in = urlConnection.getInputStream();
        int len;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while((len = in.read(buffer)) != -1){
            outputStream.write(buffer,0,len);
            outputStream.flush();
        }
        String html = new String(outputStream.toByteArray(),"utf-8");
        outputStream.close();
        System.out.println(html);
    }
    private  byte[] produceFormEntity(String username,String password) {
        HashMap<String,String> parmas = new HashMap<String,String>();
        parmas.put("tt", "" + System.currentTimeMillis());
        parmas.put("username",username);
        parmas.put("password",password);
        parmas.put("verifycode","");
        parmas.put("vcodestr","");
        parmas.put("action","login");
        parmas.put("u","http%3A%2F%2Fm.baidu.com%3Fuid%3D1443356236494_399");
        parmas.put("tpl","wimn");
        parmas.put("tn","");
        parmas.put("pu","sz@1320_1001,ta@iphone_2_5.0_3_537");
        parmas.put("ssid","");
        parmas.put("from","884b");
        parmas.put("bd_page_type","1");
        parmas.put("uid","1443356236494_399");
        parmas.put("type","");
        parmas.put("regtype","");
        parmas.put("subpro","");
        parmas.put("adapter","0");
        parmas.put("skin","default_v2");
        parmas.put("regist_mode","");
        parmas.put("login_share_strategy","");
        parmas.put("client","");
        parmas.put("clientfrom","");
        parmas.put("connect","0");
        parmas.put("bindToSmsLogin","");
        parmas.put("isphone","0");
        parmas.put("loginmerge","1");
        parmas.put("countrycode","");
        parmas.put("servertime",System.currentTimeMillis()+"");
        parmas.put("gid","7C26D59-3743-463B-81D5-1BB4AA5B25D5");
        parmas.put("logLoginType","wap_loginTouch");
        Set<String> keySet = parmas.keySet();
        StringBuilder sb = new StringBuilder();
        for(String key:keySet){
            sb.append(key+"="+parmas.get(key)+"&");
        }
        return sb.toString().getBytes();
    }
}
