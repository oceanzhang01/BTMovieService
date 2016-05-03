package com.dianping.btmovie.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dianping.btmovie.bmob.BmobHelper;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;
import com.dianping.btmovie.ucdisk.UCDiskTaskService;
import com.dianping.btmovie.utils.StringUtils;

import java.util.List;

/**
 * Created by oceanzhang on 15/11/29.
 */
public class TestDB {
    static MovieDao dao = new MovieDaoImpl();
    public static void main(String ... args){
//        BtMovie movie = dao.findMovie("a8d38e420f","name");
//        List<BtMovie> movies = dao.getMovies(new String[]{"uclink"}, new String[]{""}, 1, 20, null);
//        boolean isHaveMovie = dao.isHaveMovie(new String[]{"name"}, new String[]{"超凡蜘蛛侠2.The.Amazing.Spider.Man.2.2014.HD720P.X264.AAC.English.CHS-ENG.Mp4Ba"});
//        System.out.println(isHaveMovie);
//        toDb();
        UCDiskTaskService.getInstance().startUpdateUCLinkTask();
    }

    private static void toDb(){
        int page = 1;
        while(true){
            List<BtMovie> movies = BmobHelper.getInstance().getAllMoviesJson(page++,50);
            if(movies != null && movies.size() > 0){
                for(BtMovie movie : movies){
                    String m3u8 = movie.getM3u8Path() ;
                    movie.setM3u8Path("");
                    if(!StringUtils.isEmpty(m3u8)) {
                        JSONArray arr = JSON.parseArray(m3u8);
                        if(arr != null && arr.size() > 0){
                            movie.setM3u8Path(arr.getString(0));
                        }
                    }
                    dao.insertMovie(movie);
                }
            }
            assert movies != null;
            if(movies.size() < 50) break;
        }
    }
}
