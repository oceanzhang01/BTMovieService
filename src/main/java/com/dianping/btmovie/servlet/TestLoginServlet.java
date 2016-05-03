//package com.dianping.btmovie.servlet;
//
//import com.dianping.btmovie.baidu.BaiduException;
//import com.dianping.btmovie.baidu.Util;
//import org.apache.http.HttpEntity;
//import org.apache.http.Response;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.CookieStore;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.client.protocol.ClientContext;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Created by oceanzhang on 15/10/8.
// */
//@WebServlet(name = "TestLoginServlet",urlPatterns = "/TestLogin")
//public class TestLoginServlet extends BaseServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        this.doGet(request, response);
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/json;charset=UTF-8");
//        String body =httpRequest("http://www.baidu.com/");
//        System.out.println(body);
//        String html = login("nieiqmhj997038@sina.com","sunny968");
//        PrintWriter pw = response.getWriter();
//        jsonRerutn(pw,0,"return login html",html);
//    }
//	public synchronized String login(String username,String password) throws IOException {
//		String html = httpRequest("http://wappass.baidu.com/wp/api/login?v=1439520441484", produceFormEntity(username, password),null);
//        System.out.println(html);
//		String bdToken;
//		if ((bdToken = checkLogin()) == null) {
//			return "登录结果:登陆失败";
//		} else {
//			System.out.println("登录结果:" + " 登录成功.");
//			return bdToken;
//		}
//	}
//
//	private  List<NameValuePair> produceFormEntity(String username,String password)
//			throws UnsupportedEncodingException {
//		List<NameValuePair> list = new ArrayList<NameValuePair>();
//		list.add(new BasicNameValuePair("tt", "" + System.currentTimeMillis()));
//		list.add(new BasicNameValuePair("tpl", "neidisk"));
//		list.add(new BasicNameValuePair("login_share_strategy", "disabled"));
//		list.add(new BasicNameValuePair("isPhone", ""));
//		list.add(new BasicNameValuePair("username", username));
//		list.add(new BasicNameValuePair("password", password));
//		list.add(new BasicNameValuePair("verifycode", ""));
//		list.add(new BasicNameValuePair("clientfrom", "native"));
//		list.add(new BasicNameValuePair("client", "android"));
//		list.add(new BasicNameValuePair("adapter", "3"));
//		list.add(new BasicNameValuePair("act", "implicit"));
//
//		list.add(new BasicNameValuePair("loginLink", "0"));
//		list.add(new BasicNameValuePair("smsLoginLink", "0"));
//		list.add(new BasicNameValuePair("lPFastRegLink", "0"));
//        list.add(new BasicNameValuePair("subpro", "netdiskandroid"));
//		list.add(new BasicNameValuePair("action", "login"));
//		list.add(new BasicNameValuePair("loginmerge", "1"));
//		list.add(new BasicNameValuePair("isphone", "0"));
//		list.add(new BasicNameValuePair("logLoginType", "sdk_login"));
//
//		return list;
//	}
//	public String checkLogin() throws IOException {
//        String body = httpRequest("http://pan.baidu.com/disk/home");
//        System.out.println(body);
//		Pattern pattern = Pattern.compile("yunData.MYBDSTOKEN = \"\\w+\";");
//		Matcher matcher = pattern.matcher(body);
//		if (matcher.find()) {
//			String group = matcher.group();
//			return Util.substring(group, "\"", "\"");
//		}
//		return null;
//	}
//
//    static HttpClient httpClient = new DefaultHttpClient();
//    private CookieStore cookieStore = new BasicCookieStore();
//    public String httpRequest(String url) throws IOException{
//        return httpRequest(url,null,null);
//    }
//    public String httpRequest(String url, List<NameValuePair> params,
//                          HashMap<String, String> headers) throws IOException {
//        httpClient.getParams().setParameter(
//                CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
//                60000);
////        HttpHost proxy = new HttpHost("222.88.236.234",80);
////        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
//        Response response = null;
//        HttpUriRequest request = null;
//        if (params != null) {
//            HttpPost httpPost = new HttpPost(url);
//            HttpEntity postBodyEnt = new UrlEncodedFormEntity(params,"UTF-8");
//            httpPost.setEntity(postBodyEnt);
//            request = httpPost;
//        } else {
//            HttpGet httpGet = new HttpGet(url);
//            request = httpGet;
//
//        }
//        if (headers != null) {
//            Set<String> keySet = headers.keySet();
//            for (String key : keySet) {
//                request.addHeader(key, headers.get(key));
//            }
//        }
////        String ip = randomIp();
////        request.addHeader("client-ip",ip);
////        request.addHeader("x-forwarded-for",ip);
//        HttpContext localContext = new BasicHttpContext();
//		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//        response = httpClient.execute(request,localContext);
//        int code = response.getStatusLine().getStatusCode();
//        String respnseStr = EntityUtils.toString(response.getEntity());
//        System.out.println("[HTTP状态码:" + code + "]" + "-->Request URL:" + url);
//        if (code == 200) {
//            return respnseStr;
//        }
//        throw new BaiduException(-1, "error to get response from " + url
//                + " return:" + respnseStr);
//    }
//
//    private static String randomIp(){
//        return new Random().nextInt(255)+"."+new Random().nextInt(255)+"."+new Random().nextInt(255)+"."+new Random().nextInt(255);
//    }
//}
