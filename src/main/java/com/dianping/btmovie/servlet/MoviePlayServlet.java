package com.dianping.btmovie.servlet;

import com.dianping.btmovie.baidu.BaiduManager;
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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;

/**
 * Created by oceanzhang on 15/9/20.
 */
@WebServlet(name = "MoviePlayServlet",urlPatterns="/MoviePlay")
public class MoviePlayServlet extends BaseServlet {
	private MovieDao dao = new MovieDaoImpl();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String objectId = request.getParameter("movieid");
        response.setCharacterEncoding("UTF-8");
        if(StringUtils.isEmpty(objectId)){
            response.setStatus(400);
            PrintWriter out = response.getWriter();
            jsonRerutn(out, -1, "please input movieid or user");
            return;
        }
        BtMovie movie = dao.findMovie(objectId, "torrentUrl" ,"m3u8Path" ,"uclink");
        if(movie == null || movie.getTorrentUrl() == null){
            response.setStatus(400);
            PrintWriter out = response.getWriter();
            jsonRerutn(out,-1,"cannot find movie by this id");
            return;
        }
       String m3u8path = movie.getM3u8Path();
        String path = null ,user = null;
        BaiduService baiduService = null;
        if(StringUtils.isEmpty(m3u8path)){
        	if(!StringUtils.isEmpty(movie.getUclink())){
        		redirectToUcLink(response, movie.getUclink());
        		return;
        	}
        	Collections.shuffle(baiduManager.getBaiduServices());
        	int retrys = 0;
        	for(BaiduService service : baiduManager.getBaiduServices()){
        		if(++retrys > 3)
        			break;
        		if(!service.isLogin())
        			continue;
        		baiduService = service;
	        	String magent = movie.getTorrentUrl();
	        	magent = magent.replaceAll("magnet:\\?xt=urn:btih:|&.+", "");
	//            String path = baiduService.searchRemoteFile("/movies", magent);
	        	path = baiduService.addOfflineTask(magent);
	        	if(path == null){
	                continue;
	            }
	        	user = baiduService.getBaiduAccount().getUser();
	        	//update bmobmovie
	        	String m3u8p = path + "#" + user;
	        	movie.setM3u8Path(m3u8p);
				dao.updateMovie(objectId,new String[]{"m3u8Path"},new String[]{m3u8p});
	        	break;
        	}
        }else{
        	String ps[] = m3u8path.split("#");
        	if(ps != null && ps.length == 2){
        		path = ps[0];
        		user = ps[1];
        	}
        }
        PrintWriter out = response.getWriter();
        if(path == null || user == null){
        	if(!StringUtils.isEmpty(movie.getUclink())){
        		redirectToUcLink(response, movie.getUclink());
        		return;
        	}
        	response.setStatus(400);
        	jsonRerutn(out,-1,"add offline task failed");
        }
        if(baiduService == null){
        	baiduService = BaiduManager.getInstance().getBaiduService(user);
        }
        if(baiduService == null){
        	if(!StringUtils.isEmpty(movie.getUclink())){
        		redirectToUcLink(response, movie.getUclink());
        		return;
        	}
        	response.setStatus(400);
        	jsonRerutn(out,-1,"cannot find a useful baidu account");
        }
        String m3u8Content = baiduService.readM3u8Content(path);     
        if(StringUtils.isEmpty(m3u8Content)){
        	if(!StringUtils.isEmpty(movie.getUclink())){
        		redirectToUcLink(response, movie.getUclink());
        		return;
        	}
            response.setStatus(400);
            jsonRerutn(out,-1,"cannot read m3u8 content.");
            return;
        }
        if(m3u8Content.length() < 1000){ //百度8s视频
			dao.updateMovie(objectId,new String[]{"m3u8Path"},new String[]{""});
        	if(!StringUtils.isEmpty(movie.getUclink())){
        		redirectToUcLink(response, movie.getUclink());
        		return;
        	}
            response.setStatus(400);
            jsonRerutn(out,-1,"cannot read m3u8 content.");
            return;
        }
		response.setContentLength(m3u8Content.getBytes().length);
        response.setContentType("application/vnd.apple.mpegurl");
        out.write(m3u8Content);
        out.flush();
        out.close();
    }
    private void redirectToUcLink(HttpServletResponse response,String ucLink) throws IOException{
    	if(!StringUtils.isEmpty(ucLink)){
    		URL url = new URL(ucLink);
    		InputStream in = url.openStream();
    		response.setContentType("video/mp4");
    		response.setStatus(302);
    		response.sendRedirect(ucLink);  		
    	}
    }
}
