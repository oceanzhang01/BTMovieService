package com.dianping.btmovie.servlet;

import com.dianping.btmovie.baidu.BaiduService;
import com.dianping.btmovie.bcodec.BDecoder;
import com.dianping.btmovie.bcodec.BEValue;
import com.dianping.btmovie.bcodec.BEncoder;
import com.dianping.btmovie.utils.StringUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by oceanzhang on 15/10/14.
 */
@WebServlet(name = "PlayMagnetLinkServlet",urlPatterns = "/PlayMagnetLink.m3u8")
public class PlayMagnetLinkServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String type = request.getParameter("type");
        String client = request.getParameter("client");
        String magnetLint = "";
        if(type != null && type.equalsIgnoreCase("torrent")){
            InputStream in = request.getInputStream();
            try {
                magnetLint = torrentDataToMagnetLink(in);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            magnetLint = request.getParameter("magnetLink");
        }
        if(StringUtils.isEmpty(magnetLint)){
            PrintWriter out = response.getWriter();
            response.setStatus(203);
            jsonRerutn(out,-1,"magnetLink cannot be null!!");
            return;
        }
        System.out.print("magnetLint:"+magnetLint);
        //如果是磁力链 转化为hash
        if(magnetLint.toLowerCase().startsWith("magnet:?xt=urn")){
            magnetLint = magnetLint.replaceAll("magnet:\\?xt=urn:btih:|&.+","");
        }
        String path = null ,user = null;
        BaiduService baiduService = null;
        Collections.shuffle(baiduManager.getBaiduServices());
    	int retrys = 0;
    	for(BaiduService service : baiduManager.getBaiduServices()){
    		if(++retrys > 3)
    			break;
    		if(!service.isLogin())
    			continue;
    		baiduService = service;
            path = baiduService.searchRemoteFile("/movies", magnetLint);
            if(path == null){
            	path = baiduService.addOfflineTask(magnetLint);
            }
        	if(path == null){
                continue;
            }
        	user = baiduService.getBaiduAccount().getUser();
        	break;
    	}
        if(path == null){
            PrintWriter out = response.getWriter();
            response.setStatus(203);
            jsonRerutn(out,-1,"add offline task failed!!");
            return;
        }
        String m3u8Content = baiduService.readM3u8Content(path);
        if(StringUtils.isEmpty(m3u8Content)){
            response.setStatus(203);
            PrintWriter out = response.getWriter();
            jsonRerutn(out,-1,"cannot read m3u8 content.");
            return;
        }
        if(client != null && client.equals("web")){
            String url = "http://"+request.getServerName()+":"+request.getServerPort()+"/PlayM3u8.ts";
            m3u8Content = parseStringFromUrl(m3u8Content,url);
        }
        response.setContentType("application/vnd.apple.mpegurl");
        PrintWriter out = response.getWriter();
        out.write(m3u8Content);
        out.flush();
        out.close();
    }
    private String torrentDataToMagnetLink(InputStream in) throws IOException{
        Map<String, BEValue> decoded = BDecoder.bdecode(in).getMap() ;
        Map<String, BEValue> decoded_info = decoded.get("info").getMap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BEncoder.bencode(decoded_info, baos);
        byte[] encoded_info = baos.toByteArray();
        byte[] info_hash= DigestUtils.sha(encoded_info);
        return "magnet:?xt=urn:btih:"+new String(Hex.encodeHex(info_hash, false));
    }
    public String parseStringFromUrl(String m3u8Content,String url) throws IOException {
        String[] lines = m3u8Content.split("\\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.length() > 0 && line.startsWith("http://")) {
                sb.append(url+"?url="+ Base64.encodeBase64String(line.getBytes("UTF-8")) + "\r\n");
            } else {
                sb.append(line + "\r\n");
            }

        }
        return sb.toString();
    }
}
