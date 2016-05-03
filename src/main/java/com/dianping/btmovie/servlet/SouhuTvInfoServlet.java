package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.utils.HttpUtils;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/10/29.
 */
@WebServlet(name = "SouhuTvInfoServlet",urlPatterns = "/SouhuTvInfo")
public class SouhuTvInfoServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        String aid = request.getParameter("aid");
        String vid = request.getParameter("vid");
        String pageSizeStr = request.getParameter("page_size");
        String pageStr = request.getParameter("page");
        PrintWriter pw = response.getWriter();
        if(StringUtils.isEmpty(aid) || StringUtils.isEmpty(vid)){
            jsonRerutn(pw,-1,"aid or vid cannot be null!");
            return;
        }
        int pageSize = 20;
        int page = 1;
        try{
            pageSize = Integer.parseInt(pageSizeStr);
        }catch (NumberFormatException e){

        }
        try{
            page = Integer.parseInt(pageStr);
        }catch (NumberFormatException e){

        }
        String infoUrl = "http://api.tv.sohu.com/v4/album/videos/"+aid+".json?sver=5.1.0&sysver=5.1&poid=1&site=1&with_trailer=1&with_fee_video=1&partner=340&api_key=9854b2afa779e1a6bff1962447a09dbd&plat=6&page="+page+"&page_size="+pageSize+"&order=0";
        JSONObject  object = JSON.parseObject(HttpUtils.get(infoUrl));
        if(object.getInteger("status") == 200){
            jsonRerutn(pw,0,object.getString("statusText"),object.getJSONObject("data").getJSONArray("videos"));
            return;
        }
        jsonRerutn(pw,-1,object.getString("statusText"));
    }
}
