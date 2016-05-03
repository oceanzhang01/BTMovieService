package com.dianping.btmovie.servlet;

import com.dianping.btmovie.baidu.BaiduManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/9/28.
 */
@WebServlet(name = "CheckLoginServlet",urlPatterns = "/CheckLogin")
public class CheckLoginServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String loginUser = baiduManager.getLoginUser();
        PrintWriter pw = response.getWriter();
        jsonRerutn(pw,0,"alerady login","oceanzhang");
//        if(loginUser == null){ //没有可用的登陆账号
//            BaiduManager.BaiduAccount account = baiduManager.getUserfulBaiduAccount();
//            if(account == null){
//                jsonRerutn(pw,-1,"cannot get a account unlogin");
//                return;
//            }else{
//                jsonRerutn(pw, 403, "need login", account);
//            }
//        }else{
//            jsonRerutn(pw,0,"alerady login",loginUser);
//        }
    }
}
