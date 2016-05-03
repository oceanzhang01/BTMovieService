package com.dianping.btmovie.mp4ba;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by oceanzhang on 15/9/20.
 */
public class DownloadManager {
    private static MovieDao movieDao = new MovieDaoImpl();

    public static void main(String... args) {
        startDownloadMovie();
    }

    private static final String HOST = "http://www.mp4ba.com/";

    public static void startDownloadMovie() {
        int page = 1;
        while (page <= 2) {
            String html = HttpHelper.httpGet("http://www.mp4ba.com/index.php?page=" + page++);
            if (html == null) {
                page--;
                continue;
            }
            List<BtMovie> movies = MovieParseUtil.parseTitle(html);
            if (movies.isEmpty()) {
                return;
            }
            for (BtMovie movie : movies) {
                if (!movieDao.isHaveMovie(new String[]{"name"}, new String[]{movie.getName()})) {
                    //
                    String contentUrl = movie.getContentUrl();
                    if (isEmpty(contentUrl)) continue;
                    String contentHtml = HttpHelper.httpGet(HOST + contentUrl);
                    if (isEmpty(contentHtml)) continue;
                    MovieParseUtil.parseMovice(contentHtml, movie);
                    String torrentUrl = movie.getTorrentUrl();
                    if (isEmpty(torrentUrl)) continue;
                    //http://movie.douban.com/j/subject_suggest?q=%E5%88%BA%E5%AE%A2%E8%81%82%E9%9A%90%E5%A8%98
                    html = HttpHelper.httpGet("https://api.douban.com/v2/movie/search?q=" + URLEncoder.encode(subMovieName(movie.getName())));
                    if (html == null) continue;
                    JSONObject object = JSON.parseObject(html);
                    if (object == null || object.getIntValue("total") <= 0) {
                        continue;
                    }
                    JSONArray arr = object.getJSONArray("subjects");
                    if (arr != null && arr.size() > 0) {
                        for (Object o : arr) {
                            JSONObject obj = (JSONObject) o;
                            if (obj.getString("subtype").equals("movie")) {
//                                String url = obj.getString("url"); //http://movie.douban.com/subject/2303845/
//                                String img = obj.getString("img");
                                String title = obj.getString("title");
//                                Pattern pattern = Pattern.compile("\\d+");
//                                Matcher matcher = pattern.matcher(url);
                                String doubanId = obj.getString("id");
                                double rating = obj.getJSONObject("rating") != null ? obj.getJSONObject("rating").getDouble("average") : movie.getRating();
                                movie.setDoubanName(title);
                                movie.setDoubanId(doubanId);
                                movie.setRating(rating);
                                getDoubanInfo(movie);
                                break;
                            }
                        }
                    } else {
                        System.out.println("没有找到豆瓣信息." + movie.getName());
                    }
                    movie.setObjectId(generateRandomStr(10));
                    movieDao.insertMovie(movie);
                }
            }

        }
    }

    private static void getDoubanInfo(BtMovie movie) {
        try {
            String doubanId = movie.getDoubanId();
            if (doubanId == null) return;//
            String url = "https://api.douban.com/v2/movie/subject/" + doubanId + "?apikey=000822f8e6aaa492001868eab3ffedb6";
//        HashMap<String,String> headers = new HashMap<String, String>();
//        headers.put("Authorization","Bearer cb30fc71edd620686192f555c9826138");
            String json = HttpHelper.httpGet(url);
            if (json == null) return;
            JSONObject object = JSON.parseObject(json);
            double rating = object.getJSONObject("rating") != null ? object.getJSONObject("rating").getDouble("average") : 0;
            String year = object.getString("year");
            String images = object.getString("images");
            String genres = object.getString("genres");
            String countries = object.getString("countries");
            String summary = object.getString("summary");
            String title = object.getString("title");
            movie.setDoubanName(title);
            movie.setCountries(countries);
            movie.setGenres(genres);
            movie.setYear(year);
            movie.setSummary(summary);
            movie.setImages(images);
            movie.setRating(rating);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String generateRandomStr(int len) {
        //字符源，可以根据需要删减
        String generateSource = "0123456789abcdefghigklmnopqrstuvwxyz";
        String rtnStr = "";
        for (int i = 0; i < len; i++) {
            //循环随机获得当次字符，并移走选出的字符
            String nowStr = String.valueOf(generateSource.charAt((int) Math.floor(Math.random() * generateSource.length())));
            rtnStr += nowStr;
            generateSource = generateSource.replaceAll(nowStr, "");
        }
        return rtnStr;
    }
    private static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    public static String subMovieName(String name) {
        return name.substring(0, name.indexOf("."));
    }

}
