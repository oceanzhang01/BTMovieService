package com.dianping.btmovie.entity;

/**
 * Created by oceanzhang on 15/11/29.
 */
public class BtMovie {
    private String objectId;
    private String type;
    private String name;
    private String mainImageUrl;
    private String info;
    private String contentUrl;
    private String torrentUrl;
    private String doubanId;
    private String doubanName;
    private double rating;
    private String year;
    private String images;
    private String genres; //["动作","科幻","冒险"],
    private String countries;
    private String summary;
    private String path;
    private String m3u8Path = "";  //path@user
    private String uclink = "";
    private String uclink720P="";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getTorrentUrl() {
        return torrentUrl;
    }

    public void setTorrentUrl(String torrentUrl) {
        this.torrentUrl = torrentUrl;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
    }

    public String getDoubanName() {
        return doubanName;
    }

    public void setDoubanName(String doubanName) {
        this.doubanName = doubanName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getM3u8Path() {
        return m3u8Path;
    }

    public void setM3u8Path(String m3u8Path) {
        this.m3u8Path = m3u8Path;
    }

    public String getUclink() {
        return uclink;
    }

    public void setUclink(String uclink) {
        this.uclink = uclink;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUclink720P() {
        return uclink720P;
    }

    public void setUclink720P(String uclink720P) {
        this.uclink720P = uclink720P;
    }
}
