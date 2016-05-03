package com.dianping.btmovie.db;

import com.dianping.btmovie.entity.BtMovie;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by oceanzhang on 15/11/29.
 */
public class MovieDaoImpl implements MovieDao {
    public void insertMovie(BtMovie movie) {
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        String sql = "INSERT INTO btmovie(objectId,type,name,mainImageUrl,info,torrentUrl,doubanId,doubanName,rating,year,images,genres,countries,summary,uclink,m3u8Path,path) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] params = new Object[]{
                movie.getObjectId(),
                movie.getType(),
                movie.getName(),
                movie.getMainImageUrl(),
                movie.getInfo(),
                movie.getTorrentUrl(),
                movie.getDoubanId(),
                movie.getDoubanName(),
                movie.getRating(),
                movie.getYear(),
                movie.getImages(),
                movie.getGenres(),
                movie.getCountries(),
                movie.getSummary(),
                movie.getUclink(),
                movie.getM3u8Path(),
                movie.getPath()
        };
        try {
            qr.update(sql,params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int updateMovie(String movieId, String[] keys, Object[] values) {
        if(keys == null || values == null || keys.length != values.length || keys.length == 0) return  -1;
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        String sql = "UPDATE btmovie set " + generateUpdateEquale(keys) + " WHERE objectId='"+movieId+"'";
        try {
            return qr.update(sql,values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public BtMovie findMovie(String movieId, String... keys) {
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        String sql = "SELECT " + generateKeys(keys) +" FROM btmovie WHERE objectId=?";
        try {
            return  qr.query(sql, new BeanHandler<BtMovie>(BtMovie.class), movieId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BtMovie findMovie(String movieId) {
        return findMovie(movieId,null);
    }

    public List<BtMovie> getMovies(String[] whereKeys, Object[] whereValues, int page, int pageSize, String order, String... keys) {
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        String sql = "SELECT " + generateKeys(keys) + " FROM btmovie ";
        if(whereKeys != null && whereValues != null && whereKeys.length == whereValues.length && whereKeys.length > 0){
            sql+="WHERE ";
            sql += generateSelectEquale(whereKeys);
        }
        String orderSql;
        if(order != null){
            if(order.startsWith("-")){
                orderSql = " ORDER BY " + order.substring(1) +" DESC";
            }else{
                orderSql = " ORDER BY "+ order;
            }
        }else{
            orderSql = " ORDER BY pubTime DESC";
        }
        sql+=orderSql;
        sql+=" LIMIT "+(page-1)*pageSize + "," + pageSize;
        try {
            return qr.query(sql,new BeanListHandler<BtMovie>(BtMovie.class),whereValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<BtMovie> getMovies(String sql,Object... vaules) {
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        try {
            return qr.query(sql,new BeanListHandler<BtMovie>(BtMovie.class),vaules);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isHaveMovie(String[] whereKeys, Object[] whereValues) {
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        String sql = "SELECT COUNT(*) FROM btmovie ";
        if(whereKeys != null && whereValues != null && whereKeys.length == whereValues.length && whereKeys.length > 0){
            sql+="WHERE ";
            sql += generateSelectEquale(whereKeys);
        }
        try {
            return (Long) qr.query(sql, new ScalarHandler(1),whereValues) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<BtMovie> searchMovies(String key) {
        QueryRunner qr = new QueryRunner(DataSourceUtil.getDataSource());
        String sql = "SELECT objectId,name,mainImageUrl,torrentUrl,doubanId,rating,images FROM btmovie where name LIKE ? ORDER BY pubTime DESC";
        try {
            return qr.query(sql,new BeanListHandler<BtMovie>(BtMovie.class),"%"+key+"%");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateKeys(String ...keys){
        String key;
        if(keys != null && keys.length > 0){
            StringBuilder sb = new StringBuilder();
            for(String k : keys){
                sb.append(k).append(",");
            }
            key = sb.substring(0,sb.length() - 1);
        }else {
            key = "*";
        }
        return key;
    }
    private String generateUpdateEquale(String [] keys){
        StringBuilder sb = new StringBuilder();
        for(int i =0;i<keys.length ; i++){
            sb.append(keys[i]).append("=?,");
        }
        return sb.substring(0,sb.length() - 1);
    }

    /**
     * where key1=? and key2<>?
     * ! not equals  % like
     * @param keys
     * @return
     */
    private String generateSelectEquale(String [] keys){
        StringBuilder sb = new StringBuilder();
        for(int i =0;i<keys.length ; i++){
            String key = keys[i];
            if(key.startsWith("!")) {
                sb.append(key.substring(1)).append("<>?");
            }else if(key.startsWith("%")){
                sb.append(key.substring(1)).append("like %?%");
            }else{
                sb.append(key).append("=?");
            }
            if(i != keys.length - 1)
                sb.append(" and ");
        }
        return sb.toString();
    }
}
