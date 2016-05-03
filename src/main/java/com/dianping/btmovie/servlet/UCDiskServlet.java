package com.dianping.btmovie.servlet;

import com.dianping.btmovie.baidu.BaiduManager;
import com.dianping.btmovie.baidu.BaiduService;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;
import com.dianping.btmovie.utils.StringUtils;
import org.apache.http.util.TextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by oceanzhang on 15/12/6.
 */
@WebServlet(name = "UCDiskServlet", urlPatterns = "/UCDisk")
public class UCDiskServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    private MovieDao dao = new MovieDaoImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        String type = request.getParameter("type");
        PrintWriter pw = response.getWriter();
        if (TextUtils.isEmpty(type)) {
            jsonRerutn(pw, -1, "please input type!");
            return;
        }
        //获取待离线下载的movies
        if (type.equals("getmovies")) {
            List<BtMovie> movies = dao.getMovies(new String[]{"uclink", "m3u8Path"}, new String[]{"", ""}, 1, 20, null, "objectId", "name", "torrentUrl","path");
            if (movies != null && movies.size() > 0) {
                for (BtMovie movie : movies) {
                    String path = movie.getPath();
                    if (TextUtils.isEmpty(path)) {
                        BaiduService service = BaiduManager.getInstance().getBaiduService();
                        if(service != null) {
                            String name = service.searchMagnetinfo(movie.getTorrentUrl());
                            if (!TextUtils.isEmpty(name)) {
                                path = name.substring(0, name.indexOf("/"));
                                movie.setPath(path);
                                dao.updateMovie(movie.getObjectId(), new String[]{"path"}, new String[]{path});
                            }
                        }
                    }
                }
            }
            jsonRerutn(pw,0,"return movies",movies);
        }else if(type.equals("updateLink")){
            String link = request.getParameter("link");
            String movieId = request.getParameter("movieId");
            String link720 = request.getParameter("link720");
            if(StringUtils.isEmpty(link) || StringUtils.isEmpty(movieId)){
                jsonRerutn(pw, -1, "please input link and movieid!");
                return;
            }
            link = link.replace("ext:uc_dw:","");
            link720 = link720.replace("ext:uc_dw:","");
            System.out.println(link);
            System.out.println(link720);
            int update = dao.updateMovie(movieId,new String[]{"uclink","uclink720P"},new String[]{link,link720});
            jsonRerutn(pw,0,"success update uclink:"+update);
        }else if(type.equals("getUpdateMovies")){
            String sql = "SELECT path,objectId,torrentUrl,name FROM btmovie WHERE uclink <> ? ORDER BY pubTime DESC";
            List<BtMovie> movies = dao.getMovies(sql,"");
            if (movies != null && movies.size() > 0) {
                for (BtMovie movie : movies) {
                    String path = movie.getPath();
                    if (TextUtils.isEmpty(path)) {
                        BaiduService service = BaiduManager.getInstance().getBaiduService();
                        String name = service.searchMagnetinfo(movie.getTorrentUrl());
                        if (!TextUtils.isEmpty(name)) {
                            path = name.substring(0, name.indexOf("/"));
                            movie.setPath(path);
                            dao.updateMovie(movie.getObjectId(),new String[]{"path"},new String[]{path});
                        }
                    }
                }
            }
            jsonRerutn(pw,0,"return movies",movies);
        }else{
            jsonRerutn(pw,-1,"unrecognized type:"+type);
        }
    }
}
