package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
 * Created by oceanzhang on 15/10/28.
 */
@WebServlet(name = "SouhuTvListServlet",urlPatterns = "/SouhuTvList")
public class SouhuTvListServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        String o = request.getParameter("o");
        String cat = request.getParameter("cat");
        String year = request.getParameter("year");
        String area = request.getParameter("area");
        String pageSizeStr = request.getParameter("page_size");
        String pageStr = request.getParameter("page");
        int pageSize = StringUtils.isEmpty(pageSizeStr) ? 24:Integer.parseInt(pageSizeStr);
        int page =  StringUtils.isEmpty(pageStr) ? 1: Integer.parseInt(pageStr);
        o = o == null ? "1" : o ;
        cat = cat == null ? "" : cat ;
        year = year == null ? "" : year ;
        area = area == null ? "" : area ;
        //http://api.tv.sohu.com/v4/search/channel.json?cid=2&cat=&o=1&area=&year=&fee=&company_id=&cate_id=&partner=340&offset=54&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=5.1.0&sysver=5.1&plat=6&poid=1&page_size=30
        //http://api.tv.sohu.com/v6/mobile/classificationScreening/list.json?sub_channel_id=1010000&cid=2&area=&cursor=0&year=&sver=5.1.0&sysver=5.1&poid=1&o=1&partner=340&api_key=9854b2afa779e1a6bff1962447a09dbd&cat=&plat=6&page_size=30
        String url = "http://api.tv.sohu.com/v4/search/channel.json?cid=2&cat="+cat+"&o="+o+"&area="+area+"&year="+year+"&fee=&company_id=&cate_id=&partner=340&offset="+((page-1)*pageSize)+"&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=5.1.0&sysver=5.1&plat=6&poid=1&page_size="+pageSize;
        String json = HttpUtils.get(url);
        PrintWriter pw = response.getWriter();
        JSONObject object = JSON.parseObject(json);
        int status = object.getInteger("status");
        if(status == 200) {
            JSONArray columns = object.getJSONObject("data").getJSONArray("videos");
            if (columns != null && columns.size() > 0) {
                jsonRerutn(pw,0,object.getString("statusText"),columns);
                return;
            }
        }
        jsonRerutn(pw,-1,object.getString("statusText"));
    }
}
