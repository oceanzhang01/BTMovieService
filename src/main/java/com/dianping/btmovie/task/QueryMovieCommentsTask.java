package com.dianping.btmovie.task;

import com.dianping.btmovie.entity.MovieComment;
import com.dianping.btmovie.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oceanzhang on 15/8/10.
 */
public class QueryMovieCommentsTask {
    static Map<String,String> headers = new HashMap<String, String>();
    static {
        headers.put("Cookie","bid=\"CHQkcitHjEs\"; ct=y; ps=y; ue=\"546107362@qq.com\"; dbcl2=\"131594696:JeedZ96PQWY\"; ck=\"UnTy\"; ll=\"108296\"; _ga=GA1.2.1219823030.1437112339; ap=1; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1439216515%2C%22http%3A%2F%2Fwww.douban.com%2F%22%5D; __utmt_douban=1; __utmt=1; push_noty_num=0; push_doumail_num=0; _pk_id.100001.4cf6=03905762b1022984.1437112343.26.1439216518.1439198166.; _pk_ses.100001.4cf6=*; __utma=30149280.1219823030.1437112339.1439193100.1439216515.26; __utmb=30149280.2.10.1439216515; __utmc=30149280; __utmz=30149280.1439171429.24.10.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=30149280.13159; __utma=223695111.1988430535.1437112340.1439193100.1439216515.25; __utmb=223695111.2.10.1439216515; __utmc=223695111; __utmz=223695111.1439125194.23.15.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/tag/2015/movie");
        headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36");
    }
    //http://movie.douban.com/subject/25723907/comments?start=21&limit=20&sort=new_score
    public static List<MovieComment> queryMovieComments(long movieId,int page){


        List<MovieComment> movieComments = new ArrayList<MovieComment>();
        try {
            String html = HttpUtils.get("http://movie.douban.com/subject/"+movieId+"/comments?start="+((page-1)*20+1)+"&limit=20&sort=new_score",headers);
            Document document = Jsoup.parse(html);
            Elements comments = document.getElementsByClass("comment-item");
            if(comments!=null&&comments.size()>0){

                for(Element element:comments){
                    try {
                        String userName = element.getElementsByClass("avatar").get(0).getElementsByTag("a").get(0).attr("title");
                        String imageUrl = element.getElementsByClass("avatar").get(0).getElementsByTag("img").get(0).attr("src");
                        String time = element.getElementsByClass("comment-info").get(0).getElementsByTag("span").get(2).text();
                        String info = element.getElementsByTag("p").get(0).text();
//                        String ratingStr = element.getElementsByClass("comment-info").get(0).getElementsByTag("span").get(1).attr("class");
//                        //allstar40 rating
//                        int rating = Integer.parseInt(ratingStr.replaceAll("allstar|rating", "").trim(),30);
                        MovieComment comment = new MovieComment();
                        comment.setMovieDoubanId(movieId);
                        comment.setUserName(userName);
                        comment.setUserImageUrl(imageUrl);
                        comment.setTime(time);
                        comment.setCommentInfo(info);
//                        comment.setRating(rating);
                        movieComments.add(comment);
                    }catch (Exception e){

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieComments;
    }
}
