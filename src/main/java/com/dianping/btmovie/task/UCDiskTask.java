package com.dianping.btmovie.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;
import com.squareup.okhttp.*;
import org.apache.http.util.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by oceanzhang on 15/11/29.
 */
public class UCDiskTask {
//    private static final String COOKIE = "s_uid=130A1A1781; _UP_F7E_8D_=\"XZ3K9CWS5kHmEO+4tckB10sem8D+GJzLUdRO7YsGUI9l3qB6vjN3R6lxR72Nkj7GCFN2GLRLX17Uv2Nidmph+rA8yl3T7sFlquXHkjRaXmcOwbTkJ/ylFniho6SkJfcKZ6Vv++WIQKHhco54/iQzeA==\"; _UP_D_=mobile; _UP_BT_=html5; _UP_L_=zh; yunsess=uc%409EA0E7D6AB6A22A220AF990E282B2F54; netdisk_vip=LTE%3D";
    private static final String COOKIE = "_UP_F7E_8D_=\"XZ3K9CWS5kHmEO+4tckB10sem8D+GJzLUdRO7YsGUI9l3qB6vjN3R6lxR72Nkj7GCFN2GLRLX17Uv2Nidmph+rA8yl3T7sFlquXHkjRaXmcOwbTkJ/ylFniho6SkJfcKZ6Vv++WIQKHhco54/iQzeA==\"; _UP_D_=mobile; _UP_BT_=html5; _UP_L_=zh; yunsess=uc%40031A94921E0957C1B89982A52E868D3F; s_uid=130A1A1781; netdisk_vip=LTE%3D";
    private String cookie;
    private boolean stop = false;
    private MovieDao dao = new MovieDaoImpl();
    public UCDiskTask(String cookie) {
        this.cookie = cookie;
    }
    private ConcurrentHashMap<String, BtMovie> tasks = new ConcurrentHashMap<String, BtMovie>();
    public static void main(String ...args){
        new UCDiskTask(COOKIE).startTask();
    }
    private void startTask(){
        try {
            List<BtMovie> movies = dao.getMovies(new String[]{"uclink","m3u8Path"}, new String[]{"",""}, 1, 2, null, "objectId", "name", "torrentUrl");
            if (movies != null && movies.size() > 0) {
                for (BtMovie movie : movies) {
                    String path = movie.getPath();
                    if (TextUtils.isEmpty(path)) {
                        JSONObject object = JSON.parseObject(httpGet("http://baidulogin.wx.jaeapp.com/QueryMagnetLinkInfo?magnetLink=" + URLEncoder.encode(movie.getTorrentUrl(), "utf-8")));
                        if (object.getInteger("code") == 0) {
                            path = object.getString("body");
                        }
                    }
                    if (path != null) {
                        path = path.substring(0, path.indexOf("/"));
                        movie.setPath(path);
                        tasks.put(path, movie);
                        addOfflineTask(movie.getTorrentUrl());
                    }

                }
                queryOfflineTask();
                transeTask();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void addOfflineTask(String magnetLink) throws IOException {
        //添加离线下载
        String url = "http://mydiskm.uc.cn/uclxmgr/newtask?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&fromdirid=&newlx_url=" + URLEncoder.encode(magnetLink, "UTF-8");
        httpRequest(url);
        url = "http://mydiskm.uc.cn/deposit/doSave?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2";
        String data = "typeoffile=link_url&link_url=" + URLEncoder.encode(magnetLink, "UTF-8") + "&link_file_size=0&link_file_cookie=link_file_ref=&link_file_name=磁力链文件";
        httpRequest(url, data);
    }

//    class QueryOfflineTask implements Runnable{
//
//        public void run() {
//            int offset = 0;
//            while(!stop) {
//                while (!stop) {
//                    try {
//                        //获取离线下载列表
//                        String url = "http://mydiskm.uc.cn/uclxmgr/ajaxGetData?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447320945886" + (offset == 0 ? "" : "&&offset=" + offset);
//                        String result = httpRequest(url).result;
//                        JSONObject object = JSON.parseObject(result);
//                        JSONArray array = object.getJSONArray("taskList");
//                        if (array.size() == 0) break;
//                        for (Object o : array) {
//                            JSONObject obj = (JSONObject) o;
//                            Boolean show = obj.getBoolean("show_save_to_nd");
//                            if (obj.getInteger("status_code") == 0 && show != null ? show : false) {
//                                String taskId = obj.getString("task_id");
//                                //存至网盘
//                                url = "http://mydiskm.uc.cn/ajax/saveToNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447330257922&taskId=" + taskId;
//                                Response response = httpRequest(url);
//                                System.out.println(response.result);
//                            }
//                        }
//                        if (array.size() < 20) {
//                            break;
//                        }
//                        offset += 20;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            try {
//                Thread.sleep(1000 * 60 * 5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    /**
     *
     * 将离线下载完成的文件存到我的UC网盘
     * @throws IOException
     */
    public void queryOfflineTask() throws IOException {
        new Thread(new Runnable() {
            public void run() {
                int offset = 0;
                while(!stop) {
                    while (!stop) {
                        try {
                            //获取离线下载列表
                            String url = "http://mydiskm.uc.cn/uclxmgr/ajaxGetData?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447320945886" + (offset == 0 ? "" : "&&offset=" + offset);
                            String result = httpRequest(url).result;
                            JSONObject object = JSON.parseObject(result);
                            JSONArray array = object.getJSONArray("taskList");
                            if (array.size() == 0) break;
                            for (Object o : array) {
                                JSONObject obj = (JSONObject) o;
                                Boolean show = obj.getBoolean("show_save_to_nd");
                                if (obj.getInteger("status_code") == 0 && show != null ? show : false) {
                                    String taskId = obj.getString("task_id");
                                    //存至网盘
                                    url = "http://mydiskm.uc.cn/ajax/saveToNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447330257922&taskId=" + taskId;
                                    Response response = httpRequest(url);
                                    System.out.println(response.result);
                                }
                            }
                            if (array.size() < 20) {
                                break;
                            }
                            offset += 20;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    private static final Executor executor = new ThreadPoolExecutor(12, 15, 30, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<Runnable>());
//
    private void transeTask() {
        new Thread(new Runnable() {
            public void run() {
                while (tasks.size() > 0) {
                    int offset = 0;
                    boolean quit = false;
                    while (!quit) {
                        //列出文件列表
                        try {
                            System.out.println("---transeTask()----");
                            String url = "http://mydiskm.uc.cn/netdisk/ajaxPhoneNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447331239396&dirId=2&type=all&edit_mode=0&moveState=0&offset=" + offset;
                            String result = httpRequest(url).result;
                            JSONArray fileDirList = JSON.parseObject(result).getJSONArray("dirList");
                            if (fileDirList.size() == 0) {
                                quit = true;
                            }
                            for (Object o1 : fileDirList) {
                                JSONObject fileDir = (JSONObject) o1;
                                String dirid = fileDir.getString("id");
                                String name = fileDir.getString("dirname");
                                BtMovie movie = tasks.remove(name.replaceAll("amp;", ""));
                                if (movie != null) {
                                    //获取文件信息
                                    url = "http://mydiskm.uc.cn/netdisk/ajaxPhoneNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447331247447&dirId=" + dirid + "&type=all&edit_mode=0&moveState=0&offset=0";
                                    result = httpRequest(url).result;
                                    JSONObject fileObj = JSON.parseObject(result).getJSONArray("fileList").getJSONObject(0);
                                    String fid = fileObj.getString("id");
                                    String fname = fileObj.getString("filename");
                                    String djangoID = fileObj.getString("djangoID");

//                                    String tranferUrl = "http://mydiskm.uc.cn/transcode/newTask?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1448201540015&djangoId=" + djangoID + "&filename=" + URLEncoder.encode(fname) + "&clarity=1";
//                                    String html = httpRequest(tranferUrl).result;
//                                    executor.execute(new TranseThread(movie,djangoID,fname));
                                    String getDownloadUrl = "http://mydiskm.uc.cn/module/getDownloadLink?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&djangoId="+djangoID+"&filename="+URLEncoder.encode(fname)+"&_="+System.currentTimeMillis();
                                    result = httpRequest(getDownloadUrl).result;
                                    try {
                                        JSONObject download = JSON.parseObject(result);
                                        String downloadUrl = download.getString("link").replaceAll("ext:uc_dw:", "").split("\\|")[0];
                                        movie.setUclink(downloadUrl);
                                        updateMovie(movie);
                                        System.out.println("update:" + movie.getName());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (fileDirList.size() < 20) {
                                quit = true;
                                break;
                            }
                            offset += 20;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("--(end) transeTask---");
            }
        }).start();

    }
//    private class TranseThread implements Runnable{
//        private BtMovie movie;
//        private String djangoID;
//        private String fname;
//        public TranseThread(BtMovie movie, String djangoID,String fname) {
//            this.movie = movie;
//            this.djangoID = djangoID;
//            this.fname = fname;
//        }
//
//        public void run() {
//            boolean q = false;
//            while (!q) {
//                int offset = 0;
//                boolean quit = false;
//                while (!quit) {
//                    try {
//                        String tranferListUrl = " http://mydiskm.uc.cn/transcode/ajaxGetList?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1448201540819&offset="+offset;
//                        String html = httpRequest(tranferListUrl).result;
//                        JSONArray array = JSON.parseArray(JSON.parseObject(html).getString("fileList"));
//                        if (array != null && array.size() > 0) {
//                            for (int i = 0; i < array.size(); i++) {
//                                JSONObject object = array.getJSONObject(i);
//                                if (djangoID.equals(object.getString("djangoId"))) {
//                                    String taskId = object.getString("taskId");
//                                    if (object.getBoolean("success")) {
//                                        String taskInfoUrl = "http://mydiskm.uc.cn/module/getTaskInfo?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1448201541140&taskId=" + taskId + "&djangoID=" + djangoID + "&status=527&type=1&filename=" + URLEncoder.encode(fname);
//                                        html = httpRequest(taskInfoUrl).result;
//                                        JSONObject taskInfo = JSON.parseObject(html);
//                                        String link = taskInfo.getString("link");
//                                        System.out.println(link);
//                                        movie.setUclink(link);
//                                        updateMovie(movie);
//                                        System.out.println("update:" + movie.getName());
//                                        q = true;
//                                    }
//                                    quit = true;
//                                    break;
//                                }
//                            }
//                        }
//                        if(array != null && array.size() < 10)break;
//                        offset+=10;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                try {
//                    Thread.sleep(1000 * 20);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    private void updateMovie(BtMovie movie){
        dao.updateMovie(movie.getObjectId(), new String[]{"path", "uclink"}, new String[]{movie.getPath(), movie.getUclink()});
    }
    private Response httpRequest(String u) throws IOException {
        return httpRequest(u,null,null);
    }
    private Response httpRequest(String u,String params) throws IOException {
        return httpRequest(u,params,null);
    }
    private OkHttpClient httpClient = new OkHttpClient();
    private Response httpRequest(String u, String params, Map<String, String> headers) throws IOException {
//        httpClient.setFollowRedirects(false);
        Request.Builder builder = new Request.Builder().url(u);
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                builder = builder.addHeader(key,headers.get(key));
            }
        }
        if (cookie != null) {
            builder = builder.addHeader("Cookie",cookie);
        }
        builder = builder.addHeader("X-UCBrowser-UA","dv(N1);pr(UCBrowser/10.6.0.620);ov(Android 5.1.1);ss(768*976);pi(1536*1952);bt(UC);pm(1);bv(1);nm(0);im(0);sr(0);nt(2);");
        builder = builder.addHeader("User-Agent","Mozilla/5.0 (Linux; U; Android 5.1.1; zh-CN; N1 Build/A5CN701) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.6.0.620 U3/0.8.0 Mobile Safari/534.30");
        if(!TextUtils.isEmpty(params)){
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType,params);
            builder = builder.post(body);
        }else{
            builder = builder.get();
        }
        com.squareup.okhttp.Response response =  httpClient.newCall(builder.build()).execute();
        Headers hs = response.headers();
        Map<String,String > receiveHeaders = new HashMap<String, String>();
        for(int i = 0;i<hs.size();i++){
            receiveHeaders.put(hs.name(i),hs.value(i));
        }
        return new Response(response.code(),response.body().string(),receiveHeaders);
    }
    class Response{
        int code;
        String result;
        Map<String,String> headers;

        public Response(int code,String result, Map<String, String> headers) {
            this.code = code;
            this.result = result;
            this.headers = headers;
        }
    }
    private String httpGet(String u) throws IOException{
        URL url = new URL(u);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("GET");
        InputStream in = new BufferedInputStream(connection.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            out.flush();
        }
        byte[] data = out.toByteArray();
        return new String(data);
    }
}
