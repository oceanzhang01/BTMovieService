package com.dianping.btmovie.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.utils.HttpUtils;
import com.dianping.btmovie.utils.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oceanzhang on 15/10/23.
 */
public class VideoLiveTask {
    private static final  String liveListUrl = "http://i.cmvideo.cn/iworld//publish/clt/resource/isj2/home/homeData.jsp?flevel=2&sdkVersion=32.00.02.16&playerType=4&res=HDPI&clientId=3518949845152&filterType=1&OSType=0&netType=&nodeId=70000014&tag=%E4%B8%AD%E5%A4%AE%E5%8F%B0&nt=4&isH265=true&isDobby=false&isFullHD=true";
    private static final String liveDataUrl = "http://i.cmvideo.cn/iworld//publish/clt/resource/isj2/home/homeLiveData.jsp?sdkVersion=32.00.02.16&playerType=4&res=HDPI&clientId=3518949845152&filterType=1&OSType=0&netType=&token=0750cf548338d7dcb32c6f3f19adb55a&cid=10089&imei=dbbfa8014d7cce9a5ac0293c630152264e88846ef6075b6d8053a524fefad933";
    static SimpleDateFormat format = new SimpleDateFormat("MM-dd");

    private static String liveInfoUrl(String nodeId){
        String date = format.format(new Date());
        return "http://i.cmvideo.cn/iworld//publish/clt/resource/isj2/livelist/livelistData.jsp?nodeId="+nodeId+"&timeparam="+date+"&sdkVersion=32.00.02.16&playerType=4&res=HDPI&clientId=3518949845152&filterType=1&OSType=0&netType=";
    }
    private static String livePlayUrl(String nodeId,String contentId,boolean isLive){
        return "http://i.cmvideo.cn/iworld//publish/clt/resource/isj2/player/playerData.jsp?contentId="+contentId+"&nodeId="+nodeId+"&objType=live&nt=4"+ (isLive? "&live=true":"")+"&isDobby=false&isH265=true&isFullHD=true&sdkVersion=32.00.02.16&playerType=4&res=HDPI&clientId=3518949845152&filterType=1&OSType=0&netType=&token=0750cf548338d7dcb32c6f3f19adb55a&cid=10089&imei=dbbfa8014d7cce9a5ac0293c630152264e88846ef6075b6d8053a524fefad933";
    //http://i.cmvideo.cn/iworld//publish/clt/resource/isj2/player/playerData.jsp?contentId=60820785820151024001&nodeId=608207858&objType=review&nt=4&isDobby=false&isH265=true&isFullHD=true&sdkVersion=32.00.02.16&playerType=4&res=HDPI&clientId=3518949845152&filterType=1&OSType=0&netType=&token=0750cf548338d7dcb32c6f3f19adb55a&cid=10089&imei=dbbfa8014d7cce9a5ac0293c630152264e88846ef6075b6d8053a524fefad933
    }
    public static String getLiveList() throws IOException {
        String html = HttpUtils.get(liveListUrl);
        JSONObject object = JSON.parseObject(html);
        JSONArray array = object.getJSONArray("contList");
        List<String> nodeIds = new ArrayList<String>();
        for(Object o : array){
            JSONObject obj = (JSONObject) o;
            String param = obj.getString("param");
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(param);
            if (matcher.find()){
                nodeIds.add(matcher.group());
            }
        }
        String liveData = HttpUtils.post(liveDataUrl,"nodeId="+ org.apache.commons.lang.StringUtils.join(nodeIds, ","));
        int len = array.size();
        JSONObject liveDataObj = JSON.parseObject(liveData);
        for (int i=0;i< len; i++){
            JSONObject obj = array.getJSONObject(i);
            String data = liveDataObj.getString(nodeIds.get(i));
            obj.put("liveData",data);
            obj.put("nodeId",nodeIds.get(i));
        }
        return array.toJSONString();
    }

    public static String getLiveInfo(String nodeId) throws IOException {
        String jsonStr = HttpUtils.get(liveInfoUrl(nodeId));
        JSONObject object = JSON.parseObject(jsonStr);
        JSONObject obj = new JSONObject();
        obj.put("List1",object.getJSONArray("List1"));
        obj.put("List2",object.getJSONArray("List2"));
        obj.put("List3",object.getJSONArray("List3"));
        obj.put("List4",object.getJSONArray("List4"));
        return obj.toJSONString();
    }

    public static String getLivePlayUrl(String nodeId,String contentId,boolean isLive) throws IOException {
        String playUrl = HttpUtils.get(livePlayUrl(nodeId,contentId,isLive));
        JSONObject object = JSON.parseObject(playUrl);
        playUrl = object.getString("playUrl") ;
        if(StringUtils.isEmpty(playUrl)){
            throw new IOException("cannot find play source");
        }
        return playUrl.replaceAll("amp;","");
    }
}
