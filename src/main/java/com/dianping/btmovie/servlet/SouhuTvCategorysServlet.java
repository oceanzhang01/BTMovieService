package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/10/29.
 */
@WebServlet(name = "SouhuTvCategorysServlet",urlPatterns = "/SouhuTvCategorys")
public class SouhuTvCategorysServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        JSONObject object = JSON.parseObject(HttpUtils.get("http://api.tv.sohu.com/v4/category/teleplay.json?&partner=340&api_key=9854b2afa779e1a6bff1962447a09dbd&sver=5.1.0&sysver=5.1&plat=6&poid=1"));
        PrintWriter pw = response.getWriter();
        if(object.getInteger("status") == 200){
            jsonRerutn(pw,0,object.getString("statusText"),object.getJSONObject("data").getJSONArray("categorys"));
        }else{
            jsonRerutn(pw,-1,object.getString("statusText"));
        }
    }
}
