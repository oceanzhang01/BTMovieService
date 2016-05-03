package com.dianping.btmovie.bmob;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.baidu.BaiduManager.BaiduAccount;
import com.dianping.btmovie.entity.BmobMovie;
import com.dianping.btmovie.entity.BtMovie;
import org.apache.http.util.TextUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class BmobHelper {
	private BmobHelper(){

	}
	private static BmobHelper instance = null ;
	public static BmobHelper getInstance(){
		if(instance ==null){
			instance = new BmobHelper();
			Bmob.initBmob("58cc145874045de280e54abd58612cc9","c8c2f8580357853aad40591c4b45647f");
		}
		return instance;
	}
	
	public void addData(String jsonData){
		Bmob.insert("Movie",jsonData);
	}
	public BmobMovie findMovie(String movieId){
		String result = Bmob.findOne("Movie",movieId);
        try {
            return JSON.parseObject(result, BmobMovie.class);
        }catch (Exception e){
            return null;
        }

	}
    public List<BmobMovie> getMovies(String type,int page,int pageSize){
        JSONObject json = new JSONObject();
        json.put("type", type);
        String result = Bmob.findColumns("Movie", "objectId,name,mainImageUrl,torrentUrl,doubanId,rating,images", json.toJSONString(), (page-1) * pageSize, pageSize, "-pubTime");
        String  array = JSONObject.parseObject(result).getString("results");
        return JSON.parseArray(array,BmobMovie.class);
    }
    public List<BmobMovie> getAllMovies(){
    	String result = Bmob.findColumns("Movie", "torrentUrl", "", 1500);
    	String  array = JSONObject.parseObject(result).getString("results");
        return JSON.parseArray(array,BmobMovie.class);
    }
    public List<BtMovie> getAllMoviesJson(int page,int pageSize){
    	JSONObject json = new JSONObject();
    	String result = Bmob.find("Movie", json.toJSONString(), (page - 1) * pageSize, pageSize, "pubTime");
    	String  array = JSONObject.parseObject(result).getString("results");
        return JSON.parseArray(array,BtMovie.class);
    }
    public BmobMovie getMovieTorrent(String movieId){
        JSONObject json = new JSONObject();
        json.put("objectId", movieId);
        String result = Bmob.findColumns("Movie","objectId,m3u8Path,torrentUrl,uclink",json.toJSONString(),1);
        String  array = JSONObject.parseObject(result).getString("results");
        List<BmobMovie> movies = JSON.parseArray(array, BmobMovie.class);
        return movies != null && movies.size() >0 ? movies.get(0) : null;
    }
    public BmobMovie getMovieInfo(String movieId){
        JSONObject json = new JSONObject();
        json.put("objectId", movieId);
        String result = Bmob.findColumns("Movie","objectId,info,name",json.toJSONString(),1);
        String  array = JSONObject.parseObject(result).getString("results");
        List<BmobMovie> movies = JSON.parseArray(array, BmobMovie.class);
        return movies != null && movies.size() >0 ? movies.get(0) : null;
    }
	public boolean isHaveMovie(String name) {
		JSONObject json = new JSONObject();
		json.put("name", name);
		String result = Bmob.findColumns("Movie", "name,type", json.toJSONString(), 1);
        JSONArray array = JSONObject.parseObject(result).getJSONArray("results");
		return !(array == null || array.isEmpty());
	}
	public List<BmobMovie> searchMovies(String key){
		JSONObject json = new JSONObject();
		JSONObject obj = new JSONObject();
		//{"$regex":"smile.*"}
		obj.put("$regex", ".*"+key+".*");
        json.put("name", obj);
        String result = Bmob.findColumns("Movie", "objectId,name,mainImageUrl,torrentUrl,doubanId,rating,images", json.toJSONString(), "-pubTime");
        String  array = JSONObject.parseObject(result).getString("results");
        return JSON.parseArray(array,BmobMovie.class);
	}
    public void updateMovie(BmobMovie movie){
        try {
            updateTasks.put(movie);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void updateMoviePath(BmobMovie movie){
        JSONObject json = new JSONObject();
        json.put("m3u8Path",movie.getM3u8Path());
        Bmob.update("Movie", movie.getObjectId(),json.toJSONString());

    }
    public List<BaiduAccount> getBaiduAccounts(){
    	String json = Bmob.findAll("BaiduAccount");
    	String  array = JSONObject.parseObject(json).getString("results");
    	return JSON.parseArray(array, BaiduAccount.class);
    }
    public String getUCCookie(){
        String json = Bmob.findAll("UCCookie");
        String  array = JSONObject.parseObject(json).getString("results");
        JSONArray arr = JSON.parseArray(array);
        if(arr != null && arr.size() > 0){
            return arr.getJSONObject(0).getString("cookie");
        }
        return null;
    }
    public static JSONObject getPatch(){
        String json = Bmob.find("Patch", 1, "-code");
        String  array = JSONObject.parseObject(json).getString("results");
        if(!TextUtils.isEmpty(array)){
            JSONArray arr = JSON.parseArray(array);
            if(arr != null && arr.size() > 0){
                return arr.getJSONObject(0);
            }
        }
        return  null;
    }
    private static LinkedBlockingQueue<BmobMovie> updateTasks = new LinkedBlockingQueue<BmobMovie>();

}
