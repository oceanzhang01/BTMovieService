package com.dianping.btmovie.mp4ba;

import com.dianping.btmovie.entity.BtMovie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class MovieParseUtil {
	private static final String types = "国产电影 港台电影 欧美电影 日韩电影 海外电影 动画电影";
	public static List<BtMovie> parseTitle(String html){
		List<BtMovie> titles=new ArrayList<BtMovie>();
		Document doc= Jsoup.parse(html);
		Element elementList=doc.getElementById("data_list");
		if(elementList != null){
		Elements ele_trs=elementList.getElementsByTag("tr");
		if(ele_trs!=null&&ele_trs.size()>0) {
			for (Element tr : ele_trs) {
				Elements tds = tr.getElementsByTag("td");
				if (tds.size() >= 5) {
					BtMovie title = new BtMovie();
					Element e = tds.get(2).getElementsByTag("a").get(0);
					String name = e.text();
					if (name.indexOf("1080P") != -1) {
						continue;
					}
					title.setName(name);
					String type = tds.get(1).text();
					if(types.contains(type)) {
						title.setType(type);
						title.setContentUrl(e.attr("href"));
						titles.add(title);
					}
				}
			}
		}
		}
		
		return titles;
	}
	
	public static void parseMovice(String html,BtMovie movie){
		Document doc= Jsoup.parse(html);
		Element down=doc.getElementById("magnet");
		if(down==null||down.attr("href")==null){
			return;
		}
		movie.setTorrentUrl(down.attr("href"));
		Element ele=doc.getElementsByClass("intro").get(0);
		Elements scripts=ele.getElementsByAttributeValue("type", "text/javascript");
		Elements imgs=ele.getElementsByTag("img");
		int len=imgs.size();
		if(len>1){
			movie.setMainImageUrl(imgs.get(0).attr("src"));
		}
		imgs.remove();
		scripts.remove();
		ele.getElementsByTag("a").remove();
		ele.getElementsByTag("strong").remove();
		ele.getElementsByTag("span").remove();
		String text=ele.html();
		movie.setInfo(text);
	}
}
