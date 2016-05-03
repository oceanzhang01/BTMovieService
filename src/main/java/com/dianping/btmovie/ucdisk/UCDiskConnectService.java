package com.dianping.btmovie.ucdisk;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
public class UCDiskConnectService {

	private CookieStore cookieStore = new BasicCookieStore();
	private UCDiskConnectService(){

	}
	private volatile static UCDiskConnectService service = null;
	public static UCDiskConnectService getService(){
		if(service == null){
			synchronized (UCDiskConnectService.class){
				if(service == null){
					service = new UCDiskConnectService();
				}
			}
		}
		return service;
	}
	public Response execute(String url) throws IOException {
		return this.execute(url, null, null);
	}

	public Response execute(String url, HashMap<String, String> headers)
			throws IOException {
		return this.execute(url, null, headers);
	}

	public Response execute(String url, List<NameValuePair> params)
			throws IOException {
		return this.execute(url, params, null);
	}
	private Response executeStream(String url, InputStream data,
							HashMap<String, String> headers) throws IOException{
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				60000).setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpResponse response = null;
		HttpUriRequest request = null;
		if (data != null) {
			HttpPost httpPost = new HttpPost(url);
			HttpEntity postBodyEnt = new InputStreamEntity(data);
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
		request.addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 5.1.1; zh-CN; N1 Build/A5CN701) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.6.0.620 U3/0.8.0 Mobile Safari/534.30");
		request.addHeader("X-UCBrowser-UA","dv(N1);pr(UCBrowser/10.6.0.620);ov(Android 5.1.1);ss(768*976);pi(1536*1952);bt(UC);pm(1);bv(1);nm(0);im(0);sr(0);nt(2);");
		HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		response = httpClient.execute(request, localContext);
		int code = response.getStatusLine().getStatusCode();
		String respnseStr = EntityUtils.toString(response.getEntity());
		Header[] hs = response.getAllHeaders();
		Map<String,String> headersMap = new HashMap<String, String>();
		if(hs != null){
			for(Header h: hs){
				headersMap.put(h.getName(),h.getValue());
			}
		}
		if (code == 200) {
			return new Response(code,respnseStr,headersMap,null);
		}else if(code / 100 == 3){
			String locationUrl = response.getFirstHeader("Location").getValue();
			locationUrl+="&fr=android&pf=145&ve=10.5.2.598&ss=360x592&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNQUGqmrS7c86ay74Pj84d9hMIVAKXkSpNJX6XZcAl4%3D&si=&ei=bTkwBBK4gkfrF92fNnTDRnogkh7ge0Ye&nt=2";
			return execute(locationUrl);
		}
		throw new UCDiskException(code, "error to get response from " + url
				+ " return:" + respnseStr);
	}
	private String encode(List<NameValuePair> form) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (NameValuePair e : form) {
			if (sb.length() > 0)
				sb.append('&');
			sb.append(e.getName());
			sb.append('=');
			if (e.getValue() != null)
				sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
		}
		return sb.toString();
	}
	public Response execute(String url, List<NameValuePair> params,
			HashMap<String, String> headers) throws IOException {
		if(params != null && params.size() > 0) {
			return executeStream(url, new ByteArrayInputStream(encode(params).getBytes("UTF-8")), headers);
		}else{
			return executeStream(url, null, headers);
		}
	}
}