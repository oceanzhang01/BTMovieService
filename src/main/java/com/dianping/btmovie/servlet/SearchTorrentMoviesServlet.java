package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.util.Base64;
import com.dianping.btmovie.task.SearchTorrentTask;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/10/15.
 */
@WebServlet(name = "SearchTorrentMoviesServlet",urlPatterns = "/SearchTorrentMovies")
public class SearchTorrentMoviesServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        String key =  request.getParameter("key");
        String type = request.getParameter("type");
        String encoding = request.getParameter("encoding");
        PrintWriter pw = response.getWriter();
        if(!StringUtils.isEmpty(type) && type.equals("hotlist")){
            return;
        }
        if(!StringUtils.isEmpty(key)){
        	if(!StringUtils.isEmpty(encoding) && encoding.equals("base64")){
        		key = new String(Base64.decodeFast(key),"utf-8"); 
        	}
            String json = SearchTorrentTask.searchTorrentLink(key);
            jsonRerutn(pw,0,"return torrents json list.",json);
            return;
        }
        jsonRerutn(pw,-1,"unknow action.");
    }
}
