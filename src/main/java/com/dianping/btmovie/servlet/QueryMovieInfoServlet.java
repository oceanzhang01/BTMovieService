package com.dianping.btmovie.servlet;

import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by oceanzhang on 15/10/10.
 */
@WebServlet(name = "QueryMovieInfoServlet" ,urlPatterns = "/QueryMovieInfo")
public class QueryMovieInfoServlet extends BaseServlet {
    private MovieDao dao = new MovieDaoImpl();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String movieId = request.getParameter("movieId");
        response.setContentType("text/json;charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin","*");
        PrintWriter pw = response.getWriter();
        BtMovie movie = dao.findMovie(movieId,"objectId","info","name");
        jsonRerutn(pw, 0, "return movie info", movie);
    }
}
