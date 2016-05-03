package com.dianping.btmovie.bttiantang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.bcodec.BDecoder;
import com.dianping.btmovie.bcodec.BEValue;
import com.dianping.btmovie.bcodec.BEncoder;
import com.j256.ormlite.dao.Dao;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oceanzhang on 16/3/14.
 */
public class QueryBtTiantangMovie {
    public static void main(String ...args) {
        new Thread(new DownloadTask()).start();
        QueryTask task = new QueryTask(3629,27804);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Void> result = forkJoinPool.submit(task);
        try {
            result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

   public static class QueryTask extends RecursiveTask<Void>{
       int start;
       int end;

       public QueryTask(int start, int end) {
           this.start = start;
           this.end = end;
       }

       @Override
       protected Void compute() {
           boolean doQuery = (end - start) <= 20;
           if(doQuery){
               for(int i = start;i<=end;i++){
                   //do query
                   try {
                       query(i);
                   } catch (Exception e) {
                       System.out.println("movieId:"+i +" error:"+e.getMessage() );
                   }
               }
           }else{
               int middle = (start + end) / 2;
               QueryTask task = new QueryTask(start,middle);
               QueryTask right = new QueryTask(middle + 1,end);
               task.fork();
               right.fork();
               task.join();
               right.join();
           }
           return null;
       }
   }
    private static LinkedBlockingQueue<BtTiantangMovie> downloadLinked = new LinkedBlockingQueue<BtTiantangMovie>(1000);
    public static void query(int id) throws Exception {
        Dao<BtTiantangMovie,String> dao = DBHelper.createDao(BtTiantangMovie.class);
        if(dao.idExists(id+"")){
            System.out.println("exists");
            return;
        }
        String url = "http://www.bttiantang.com/subject/"+id+".html";
        String html = HttpUtils.queryHtml(url);
        Document document = Jsoup.parse(html);
        Element titleEle = document.getElementsByClass("title").first();
        String h2 = titleEle.getElementsByTag("h2").html();
        String movieName = h2.split("<i>")[0];
        String year = null;
        try {
             year = h2.substring(h2.lastIndexOf(".") + 1);
        }catch (Exception e){}
        Elements divs = document.getElementsByClass("tinfo");
        List<BtTiantangMovie.Torrnet> torrnets = new ArrayList<BtTiantangMovie.Torrnet>();
        BtTiantangMovie movie = new BtTiantangMovie();
        movie.setMovieName(movieName);
        movie.setYead(year);
        for(Element div:divs){
            Element a = div.getElementsByTag("a").first();
            String name = a.attr("title");
            String href = a.attr("href");
            String hash =  href.substring(href.lastIndexOf("=")+1);
            torrnets.add(new BtTiantangMovie.Torrnet(id+"",name,hash));
        }
        movie.setTorrnets(torrnets);

        String doubanUrl = "http://www.bttiantang.com/jumpto.php?aid="+id;
        String redirectHtml = HttpUtils.queryHtml(doubanUrl);
        Pattern pattern = Pattern.compile("subject/\\d+/");
        Matcher matcher = pattern.matcher(redirectHtml);
        if(!matcher.find())return;
        String doubanId = matcher.group().replaceAll("subject|/", "");
        Document doubanDocument = Jsoup.parse(HttpUtils.queryDoubanHtml("https://movie.douban.com/subject/" + doubanId + "/"));
        Element element = doubanDocument.getElementById("mainpic");
//        String doubanId = element.getElementsByClass("nbgnbg").first().attr("href").replaceAll("https://movie.douban.com/subject/|/photos?type=R", "");
        String mainImageUrl = element.getElementsByTag("img").first().attr("src");
        String info = doubanDocument.getElementById("info").html();
        double score = 0;
        try {
            score = Double.parseDouble(doubanDocument.getElementsByClass("rating_num").first().text());
        }catch (Exception e){}
        String desc = doubanDocument.getElementsByClass("related-info").first().html();
        movie.setMovieId(id + "");
        movie.setDesc(desc);
        movie.setInfo(info);
        movie.setDoubanId(doubanId);
        movie.setScore(score);
        movie.setMainImageUrl(mainImageUrl);
        downloadLinked.offer(movie);
//        DBHelper.createDao(BtTiantangMovie.class).createIfNotExists(movie);
    }

    public static class DownloadTask implements Runnable{
        public void run() {
            while (true){
                try {

                    BtTiantangMovie movie = downloadLinked.poll(100,TimeUnit.SECONDS);
                    JSONArray array = new JSONArray();
                    if(movie != null){
                        List<BtTiantangMovie.Torrnet> torrnets = movie.getTorrnets();
                        for(BtTiantangMovie.Torrnet torrnet: torrnets) {
                            byte[] data = HttpUtils.downloadData("http://www.bttiantang.com/download2.php", "action=download&id=" + torrnet.id + "&uhash=" + torrnet.hash + "&imageField.x=100&imageField.y=25");
                            try {
                                String magnetLink = torrentDataToMagnetLink(new ByteArrayInputStream(data));
                                JSONObject object = new JSONObject();
                                object.put("title", torrnet.title);
                                object.put("hash", magnetLink);
                                array.add(object);
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    movie.setMagnetLinks(JSON.toJSONString(array));
                    DBHelper.createDao(BtTiantangMovie.class).createIfNotExists(movie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static String torrentDataToMagnetLink(InputStream in) throws IOException{
        Map<String, BEValue> decoded = BDecoder.bdecode(in).getMap() ;
        Map<String, BEValue> decoded_info = decoded.get("info").getMap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BEncoder.bencode(decoded_info, baos);
        byte[] encoded_info = baos.toByteArray();
        byte[] info_hash= DigestUtils.sha(encoded_info);
        return new String(Hex.encodeHex(info_hash, false));
        //"magnet:?xt=urn:btih:"+
    }
}
