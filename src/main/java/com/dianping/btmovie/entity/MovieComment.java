package com.dianping.btmovie.entity;

/**
 * Created by oceanzhang on 15/8/10.
 */
public class MovieComment {
    private long movieDoubanId ;
    private String userName ;
    private String userImageUrl ;
    private String time;
    private String commentInfo ;
    private int rating ;
    public long getMovieDoubanId() {
        return movieDoubanId;
    }

    public void setMovieDoubanId(long movieDoubanId) {
        this.movieDoubanId = movieDoubanId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(String commentInfo) {
        this.commentInfo = commentInfo;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
