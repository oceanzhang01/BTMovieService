package com.dianping.btmovie.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dianping.btmovie.baidu.BaiduManager;
import com.dianping.btmovie.baidu.BaiduService;
import com.dianping.btmovie.utils.StringUtils;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by oceanzhang on 15/9/10.
 */
@WebServlet(name = "QueryMagnetLinkInfoServlet" ,urlPatterns = "/QueryMagnetLinkInfo")
public class QueryMagnetLinkInfoServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
    	String magnetLink = request.getParameter("magnetLink");
        PrintWriter pw = response.getWriter();
        if(StringUtils.isEmpty(magnetLink)){
        	jsonRerutn(pw, -1, "please input magnetLink.");
        	return;
        }
      BaiduService service = BaiduManager.getInstance().getBaiduService(); 
      String name = service.searchMagnetinfo(magnetLink);
      jsonRerutn(pw, 0, "return file name",name);
    }
}
