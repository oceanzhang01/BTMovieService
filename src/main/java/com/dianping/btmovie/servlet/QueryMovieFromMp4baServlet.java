package com.dianping.btmovie.servlet;

import com.dianping.btmovie.mp4ba.DownloadManager;
import com.dianping.btmovie.ucdisk.UCDiskTaskService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/11/29.
 */
@WebServlet(name = "QueryMovieFromMp4baServlet",urlPatterns = "/QueryMovieFromMp4ba")
public class QueryMovieFromMp4baServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }
    private static boolean isRunning = false;
    private static long lastRunTime;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        if(System.currentTimeMillis() - lastRunTime < 3 * 60 * 60 * 1000){
            jsonRerutn(pw,-1,"执行间隔大于三小时.");
            return;
        }
        if(isRunning){
            jsonRerutn(pw,-1,"任务正在执行中.");
            return;
        }
        synchronized (this){
            new Thread(new Runnable() {
                public void run() {
                    lastRunTime = System.currentTimeMillis();
                    isRunning = true;
                    DownloadManager.startDownloadMovie();
                    UCDiskTaskService.getInstance().startOfflineTask();
                    isRunning = false;
                }
            }).start();
        }
        jsonRerutn(pw,0,"任务正在后台执行.");
    }
}
