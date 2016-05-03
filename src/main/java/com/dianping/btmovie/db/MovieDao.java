package com.dianping.btmovie.db;

import com.dianping.btmovie.entity.BtMovie;

import java.util.List;

/**
 * Created by oceanzhang on 15/11/29.
 */
public interface MovieDao {
    void insertMovie(BtMovie movie);
    int updateMovie(String movieId,String[] keys,Object values[]);
    BtMovie findMovie(String movieId,String ... keys);
    BtMovie findMovie(String movieId);
    List<BtMovie> getMovies(String []whereKeys,Object [] whereValues,int page,int pageSize, String order,String ... keys);
    List<BtMovie> getMovies(String sql,Object... vaules);

    boolean isHaveMovie(String []whereKeys,Object [] whereValues);
    List<BtMovie> searchMovies(String key);
}
