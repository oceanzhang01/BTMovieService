package com.dianping.btmovie.servlet;

import com.dianping.btmovie.ucdisk.UCDiskTaskService;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/11/29.
 */
@WebServlet(name = "UCDiskOfflineTaskServlet",urlPatterns = "/UCDiskOfflineTask")
public class UCDiskOfflineTaskServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }
    private static long lastRunAddTime;
    private static long lastRunUpdateTime;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        final String type = request.getParameter("type");
        String forceStr = request.getParameter("force");
        PrintWriter pw = response.getWriter();
        if(StringUtils.isEmpty(type)){
            jsonRerutn(pw,-1,"type不能为null.");
            return;
        }
        boolean force = false; //正在运行时强制停止之前的任务
        if(forceStr != null && forceStr.equals("true")){
            force = true;
        }
        if(!force){
            if(type.equals("add") && System.currentTimeMillis() - lastRunAddTime < 3 * 60 * 60 * 1000){
                jsonRerutn(pw,-1,"执行添加离线下载间隔大于三小时.");
                return;
            }
            if(type.equals("update") && System.currentTimeMillis() - lastRunUpdateTime < 3 * 60 * 60 * 1000){
                jsonRerutn(pw,-1,"执行添加离线下载间隔大于三小时.");
                return;
            }
        }
        synchronized (this){
            new Thread(new Runnable() {
                public void run() {
                    if(type.equals("add")){
                        lastRunAddTime = System.currentTimeMillis();
                        UCDiskTaskService.getInstance().startOfflineTask();
                    }else if (type.equals("update")){
                        lastRunUpdateTime = System.currentTimeMillis();
                        UCDiskTaskService.getInstance().startUpdateUCLinkTask();
                    }
                }
            }).start();
        }
        jsonRerutn(pw,0,"任务正在后台执行.");
    }
}
