package com.dianping.btmovie.servlet;

import com.alibaba.fastjson.util.Base64;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by oceanzhang on 15/10/10.
 */
@WebServlet(name = "QueryMovieServlet",urlPatterns = "/QueryMovie")
public class QueryMovieServlet extends BaseServlet {
    private MovieDao dao = new MovieDaoImpl();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int page = Integer.parseInt(request.getParameter("page")) ;
        int pageSize = Integer.parseInt(request.getParameter("pageSize")) ;
        String type = request.getParameter("type");
        String encoding = request.getParameter("encoding");
        if(!StringUtils.isEmpty(encoding) && encoding.equals("base64")){
        	type = new String( Base64.decodeFast(type),"utf-8");
        }
//        String sql = "SELECT objectId,name,mainImageUrl,torrentUrl,doubanId,rating,images FROM btmovie WHERE type=? AND (uclink <> ? or m3u8Path <> ?)  ORDER BY pubTime DESC LIMIT ?,?";
//        Object[] values = new Object[]{type,"","",(page-1)*pageSize,pageSize};
//        List<BtMovie> movies = dao.getMovies(sql, values);
        List<BtMovie> movies = dao.getMovies(new String[]{"type"},new String[]{type},page,pageSize,null,"objectId","name","mainImageUrl","torrentUrl","doubanId","rating","images");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin","*");
        PrintWriter pw = response.getWriter();
        jsonRerutn(pw,0,type,movies);
    }
}
