package com.dianping.btmovie.servlet;

import com.dianping.btmovie.baidu.BaiduService;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/9/23.
 */
@WebServlet(name = "ShowImageServlet",urlPatterns="/ShowImage")
public class ShowImageServlet extends BaseServlet{
    private MovieDao dao = new MovieDaoImpl();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String movieid = request.getParameter("movieid");
        if(movieid == null){
            PrintWriter out = response.getWriter();
            response.setStatus(400);
            jsonRerutn(out,-1,"movieid cannot be null");
            return;
        }
        response.setCharacterEncoding("UTF-8");
        BtMovie movie = dao.findMovie(movieid,"m3u8Path","objectId");
        if(movie == null){
            PrintWriter out = response.getWriter();
            response.setStatus(400);
            jsonRerutn(out,-1,"movieid cannot be null");
            return;
        }
        String m3u8paths = movie.getM3u8Path();
        if(StringUtils.isEmpty(m3u8paths)){
            PrintWriter out = response.getWriter();
            response.setStatus(400);
            jsonRerutn(out,-1,"movieid cannot be null");
            return;
        }
        String []ps = m3u8paths.split("#");
        BaiduService baiduService = baiduManager.getBaiduService(ps[1]);
        if(baiduService == null){
            PrintWriter out = response.getWriter();
            jsonRerutn(out,403,"need login","");
            return;
        }
        byte[] imagedata = baiduService.getImage(ps[0]);
        if(imagedata == null){
            PrintWriter out = response.getWriter();
            response.setStatus(400);
            jsonRerutn(out,-1,"movieid cannot be null");
            return;
        }
        response.setContentType("image/jpeg");
        OutputStream outputStream = response.getOutputStream();

        outputStream.write(imagedata);
        outputStream.close();
    }
}
