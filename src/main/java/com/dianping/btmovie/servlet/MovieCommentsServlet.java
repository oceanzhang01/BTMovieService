package com.dianping.btmovie.servlet;

import com.dianping.btmovie.entity.MovieComment;
import com.dianping.btmovie.task.QueryMovieCommentsTask;
import com.dianping.btmovie.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by oceanzhang on 15/8/10.
 */
@WebServlet(name = "MovieCommentsServlet",urlPatterns="/MovieComments")
public class MovieCommentsServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        PrintWriter out = response.getWriter() ;
        String movieIdStr = request.getParameter("movieId") ;
        if(StringUtils.isEmpty(movieIdStr)){
            jsonRerutn(out,-1,"movieId cannot be null!");
            return;
        }
        long movieId = Long.parseLong(movieIdStr);
        String pageStr = request.getParameter("page") ;
        int page = pageStr==null ? 1:Integer.parseInt(pageStr);
        List<MovieComment> comments = QueryMovieCommentsTask.queryMovieComments(movieId, page);

        jsonRerutn(out,0,"return movie comments", comments);
    }
}
