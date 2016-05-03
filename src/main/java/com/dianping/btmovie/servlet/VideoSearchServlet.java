package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.task.SearchTorrentTask;
import com.dianping.btmovie.utils.HttpUtils;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * Created by oceanzhang on 15/10/28.
 */
@WebServlet(name = "VideoSearchServlet",urlPatterns = "/VideoSearch")
public class VideoSearchServlet extends BaseServlet {
    private MovieDao dao = new MovieDaoImpl();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        String key = request.getParameter("key");
        String encoding = request.getParameter("encoding");
        PrintWriter pw = response.getWriter();
        if(StringUtils.isEmpty(key)){
        	jsonRerutn(pw, -1, "search key cannot be null");
        	return;
        }
        if(!StringUtils.isEmpty(encoding) && encoding.equals("base64")){
        	key = new String( Base64.decodeFast(key),"utf-8");
        }
        Object souhuTvs = null;
        String url = "http://api.tv.sohu.com/v4/search/all.json?all=1&sver=5.1.0&area_code=42&pay=1&sysver=5.1&type=1&pgc=1&poid=1&ds=&uid=709929d2d23d60ce64639a68e84e77d1&partner=340&api_key=9854b2afa779e1a6bff1962447a09dbd&plat=6&page=1&key="+URLEncoder.encode(key,"utf-8")+"&page_size=30&cid=2";
        String json = HttpUtils.get(url);
        JSONObject object = JSON.parseObject(json);
        int status = object.getInteger("status");
        if(status == 200) {
        	souhuTvs = object.getJSONObject("data").getJSONArray("items");
        }
         Object movies = dao.searchMovies(key);
         Object magnetLinks = SearchTorrentTask.searchTorrentLink(key);
         JSONObject obj = new JSONObject();
         obj.put("tvs", souhuTvs);
         obj.put("movies", movies);
         obj.put("links", magnetLinks);
        jsonRerutn(pw,0,"return search result for key:"+key,obj);
    }
}
