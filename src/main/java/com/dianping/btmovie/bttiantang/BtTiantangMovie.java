package com.dianping.btmovie.bttiantang;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by oceanzhang on 16/3/14.
 */
@DatabaseTable(tableName = "BtTiantangMovie")
public class BtTiantangMovie {
    @DatabaseField(id = true,columnName = "movie_id")
    private String movieId;
    @DatabaseField(columnName = "movie_name")
    private String movieName;
    @DatabaseField(columnName = "movie_year")
    private String yead;

    @DatabaseField(columnName = "douban_id")
    private String doubanId;
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String desc;
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String info; //内容简介
    @DatabaseField
    private double score;
    @DatabaseField
    private String mainImageUrl;
    @DatabaseField(columnName = "magnet_links",dataType = DataType.LONG_STRING)
    private String magnetLinks; // [{name:link}]

    private List<Torrnet> torrnets;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getMagnetLinks() {
        return magnetLinks;
    }

    public void setMagnetLinks(String magnetLinks) {
        this.magnetLinks = magnetLinks;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getYead() {
        return yead;
    }

    public void setYead(String yead) {
        this.yead = yead;
    }

    public List<Torrnet> getTorrnets() {
        return torrnets;
    }

    public void setTorrnets(List<Torrnet> torrnets) {
        this.torrnets = torrnets;
    }

    public static class Torrnet{
        String title;
        String hash;
        String id;

        public Torrnet( String id,String title, String hash) {
            this.title = title;
            this.hash = hash;
            this.id = id;
        }
    }
}
