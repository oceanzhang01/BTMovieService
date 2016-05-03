package com.dianping.btmovie.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/9/28.
 */
@WebServlet(name = "BaiduLoginServlet",urlPatterns = "/BaiduLogin")
public class BaiduLoginServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        jsonRerutn(pw,0,"login success","oceanzhang");
    }
}
