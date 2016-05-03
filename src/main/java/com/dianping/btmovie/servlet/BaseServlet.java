package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.JSON;
import com.dianping.btmovie.baidu.BaiduManager;
import com.dianping.btmovie.entity.BaseReturn;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/8/8.
 */
public abstract class BaseServlet extends HttpServlet {
    protected BaiduManager baiduManager = BaiduManager.getInstance();
    protected void jsonRerutn(PrintWriter out, int code, String message)
    {
        this.jsonRerutn(out, code, message, "");
    }

    protected void jsonRerutn(PrintWriter out, int code, String message, Object body)
    {
        BaseReturn base = new BaseReturn();
        base.setCode(code);
        base.setBody(body);
        base.setMessage(message);
        out.write(JSON.toJSONString(base));
        out.flush();
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
