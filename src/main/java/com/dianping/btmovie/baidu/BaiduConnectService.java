package com.dianping.btmovie.baidu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class BaiduConnectService {

	private CookieStore cookieStore;

	public BaiduConnectService(String baiduUser) {
		this.cookieStore = new BasicCookieStore();
	}
	public BaiduConnectService() {
		this.cookieStore = new BasicCookieStore();
	}
	public byte[] executeGetData(String url,HashMap<String,String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);
		HttpResponse response = null;
		HttpUriRequest request = null;
		HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36");
		request = httpGet;
        if (headers != null) {
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                request.addHeader(key, headers.get(key));
            }
        }
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		response = httpClient.execute(request, localContext);
		int code = response.getStatusLine().getStatusCode();
		//String respnseStr = EntityUtils.toString(response.getEntity());
		System.out.println("[HTTP状态码:" + code + "]" + "-->Request URL:" + url);
		if (code == 200) {
			return EntityUtils.toByteArray(response.getEntity());
		}
		throw new BaiduException(code,"error:statuscode="+code);
	}
	
	public String execute(String url) throws IOException {
		return this.execute(url, null, null);
	}

	public String execute(String url, HashMap<String, String> headers)
			throws IOException {
		return this.execute(url, null, headers);
	}

	public String execute(String url, List<NameValuePair> params)
			throws IOException {
		return this.execute(url, params, null);
	}

	public String execute(String url, List<NameValuePair> params,
			HashMap<String, String> headers) throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000);
		HttpResponse response = null;
		HttpUriRequest request = null;
		if (params != null) {
			HttpPost httpPost = new HttpPost(url);
			HttpEntity postBodyEnt = new UrlEncodedFormEntity(params,"UTF-8");
			httpPost.setEntity(postBodyEnt);
			request = httpPost;
		} else {
			HttpGet httpGet = new HttpGet(url);
			request = httpGet;

		}
		if (headers != null) {
			Set<String> keySet = headers.keySet();
			for (String key : keySet) {
				request.addHeader(key, headers.get(key));
			}
		}
        HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		response = httpClient.execute(request, localContext);
		int code = response.getStatusLine().getStatusCode();
		String respnseStr = EntityUtils.toString(response.getEntity());
		System.out.println("[HTTP状态码:" + code + "]" + "-->Request URL:" + url);
		if (code == 200) {
			return respnseStr;
		}  else if (code == 403) {
            System.out.println(respnseStr);
            JSONObject object = JSON.parseObject(respnseStr);
            if(object != null){
                if(object.getIntValue("error_code") == 31045){
                    throw new BaiduException(403, "need login");
                }
            }
		}
        throw new BaiduException(-1, "error to get response from " + url
                + " return:" + respnseStr);
	}

//	public CookieStore getCookieStore() {
//		return cookieStore;
//	}

}