package com.dianping.btmovie.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/10/15.
 */
@WebServlet(name = "SearchTorrentInfoServlet",urlPatterns = "/SearchTorrentInfo")
public class SearchTorrentInfoServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hash = request.getParameter("hash");
        String user = request.getParameter("user");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        if(hash == null || hash.equals("") || user == null || user.equals("")){
            jsonRerutn(pw,-1,"please input hash value");
            return;
        }
        String name = baiduManager.getBaiduService(user).searchMagnetinfo(hash);
                if(name == null){
            jsonRerutn(pw,-1,"cannot find a video file");
            return;
        }
        jsonRerutn(pw,0,"return video name",name.substring(name.lastIndexOf("/")+1));
    }
}
