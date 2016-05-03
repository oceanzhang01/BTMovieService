package com.dianping.btmovie.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by oceanzhang on 15/8/8.
 */
public class SearchTorrentTask {

//    public static String getHotTorrents(){
//        String url = "http://www.cili8.org/Top?t=w";
//        JSONArray array = new JSONArray();
//        try {
//            String str = HttpUtils.get(url);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static String searchTorrentLink(String movieName) {
        JSONArray array = new JSONArray();
        try {
            String str = HttpUtils.get("http://www.cili8.org/s/" + URLEncoder.encode(movieName, "UTF-8") + "_hits_1.html");
            Document document = Jsoup.parse(str);
            Elements tables= document.getElementById("content").getElementsByClass("search-item");
            if(tables != null && tables.size() > 0){
                for(Element element : tables){
                    Element itemBar = element.getElementsByClass("item-bar").first();
                    Elements spans = itemBar.getElementsByTag("span");
                    String type = spans.get(0).text().trim();
                    if(type != null && type.equals("视频")){
                        Element a = element.getElementsByTag("a").get(0);
                        String hash = a.attr("href").trim().replaceAll("http://www\\.cili8\\.org/detail/|\\.html", "");
                        String title =a.text().trim();
                        String createTime = spans.get(1).text().trim().replace("创建时间：","");
                        String fileSize = spans.get(2).text().trim();
                        String hot = spans.get(3).text().trim();
                        JSONObject object = new JSONObject();
                        object.put("hash",hash);
                        object.put("title",title);
                        object.put("createTime",createTime);
                        object.put("fileSize",fileSize);
                        object.put("hot",hot);
                        array.add(object);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array.toJSONString();
    }
}
