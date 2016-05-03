package com.dianping.btmovie.ucdisk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.baidu.BaiduManager;
import com.dianping.btmovie.baidu.BaiduService;
import com.dianping.btmovie.bmob.BmobHelper;
import com.dianping.btmovie.db.MovieDao;
import com.dianping.btmovie.db.MovieDaoImpl;
import com.dianping.btmovie.entity.BtMovie;
import com.dianping.btmovie.utils.SendMail;
import com.dianping.btmovie.utils.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oceanzhang on 16/1/13.
 */
public class UCDiskTaskService {
    UCDiskConnectService httpService = UCDiskConnectService.getService();
    private static final String DEFAULT_COOKIE = "_uab_collina=145266942204591908288002; _umdata=535523100CBE37C35FEC4931D5F9CAF3413EC013C2F8E7FE310A8288CB412692EFEB69036032023F525EB6438264E4A71035F7D44EEB01A60D2F744D99A6ACB97B37131448EB2C04; _UP_RI_=cd1458c3-d046-4e35-b682-45e37849709d; _UP_6D1_64_=069; _UP_E8A_7D_=837051151`z02110917``1452670459; _UP_E37_B7_=sg1a4e25e3a9074645107f5a0447fdf84a7; _UP_F7E_8D_=\\\"d6l+og/Fn2O+nKPyaFvmFkPwKLOVbxJPcg0RzQPI6KkAb4403nUydBFHs+Q/pshPAMtlYPMuyO4GjNiFpCOHD+7rSXFm5RjbW1/mvBLvMwKTOk2wEvbokFCfwAd1vIZUTkguXFIpylhYIGPd1Sag/uE3CCjybdDCPU2ESSGfc7c=\\\"; _UP_D_=mobile; _UP_BT_=html5; _UP_L_=zh; _UP_30C_6A_=wx641263c21b4a589fe6cd8950329f25";
    private boolean login = false;
    private MovieDao dao = new MovieDaoImpl();
    private volatile boolean stopOfflineTask = false;
    private ConcurrentHashMap<String, BtMovie> tasks = new ConcurrentHashMap<String, BtMovie>();
    private Thread queryOfflineThread = null;
    private Thread transeTaskThread = null;

    private static volatile UCDiskTaskService instance = null;
    private UCDiskTaskService(){
        init();
    }
    public static UCDiskTaskService getInstance(){
        if(instance == null){
            synchronized (UCDiskConnectService.class){
                if(instance == null){
                    instance = new UCDiskTaskService();
                }
            }
        }
        return instance;
    }


    private volatile boolean stopUpdateUCLinkTask = false;
    private ConcurrentHashMap<String, BtMovie> updateUclinkTasks = new ConcurrentHashMap<String, BtMovie>();
    private Thread getUpdateUCLinkThread = null;

    public void init() {
        int retry = 0;
        while (!login && retry++ < 3) {
            String cookie = BmobHelper.getInstance().getUCCookie();
            cookie = cookie == null ? DEFAULT_COOKIE : cookie;
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Cookie", cookie);
            try {
                Response resp = httpService.execute("http://api.open.uc.cn/cas/login?client_id=37&uc_param_str=nieisivefrpfbilaprligiwiutst&client_id=37&browsertype=html5", headers);
                if (resp.getData() != null && resp.getData().contains("fileLimitDownloadMsg")) {
                    login = true;
                    System.out.println("login success..");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!login){
            //notify
            SendMail.sendEmail();
        }
    }
    private void stopOfflineTask(){
        stopOfflineTask = true;
        if(queryOfflineThread != null){
            queryOfflineThread.interrupt();
            queryOfflineThread = null;
        }
        if(transeTaskThread != null){
            transeTaskThread.interrupt();
            transeTaskThread = null;
        }
        tasks.clear();
    }
    public void startOfflineTask(){
        stopOfflineTask();
        stopOfflineTask = false;
        try {
            List<BtMovie> movies = dao.getMovies(new String[]{"uclink","m3u8Path"}, new String[]{"",""}, 1, 20, null, "objectId", "name", "torrentUrl","path");
            if (movies != null && movies.size() > 0) {
                for (BtMovie movie : movies) {
                    String path = movie.getPath();
                    if (TextUtils.isEmpty(path)) {
                        BaiduService service = BaiduManager.getInstance().getBaiduService();
                        if(service != null) {
                            String name = service.searchMagnetinfo(movie.getTorrentUrl());
                            if (!TextUtils.isEmpty(name)) {
                                path = name.substring(0, name.indexOf("/"));
                                movie.setPath(path);
                                dao.updateMovie(movie.getObjectId(), new String[]{"path"}, new String[]{path});
                            }
                        }
                    }
                    String movieName = subMovieName(movie.getName());
                    System.out.println("add offline task:"+movieName);
                    if (TextUtils.isEmpty(path)) continue;
                    path = path.length() > 40 ? path.substring(0,40) : path;
                    tasks.put(path, movie);
                    addOfflineTask(movie.getTorrentUrl());
                }
                queryOfflineTask();
                transeTask();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopUpdateUCLinkTask(){
        stopUpdateUCLinkTask = true;
        if(getUpdateUCLinkThread != null){
            getUpdateUCLinkThread.interrupt();
            getUpdateUCLinkThread = null;
        }
        updateUclinkTasks.clear();
    }
    public void startUpdateUCLinkTask(){
        stopUpdateUCLinkTask();
        stopUpdateUCLinkTask = false;
        String sql = "SELECT path,objectId,torrentUrl,name FROM btmovie WHERE uclink <> ? ORDER BY pubTime DESC";
        List<BtMovie> movies = dao.getMovies(sql, "");
        if(movies != null && movies.size() > 0){
            for(BtMovie movie:movies){
                String path = movie.getPath();
                if (TextUtils.isEmpty(path)) {
                    BaiduService service = BaiduManager.getInstance().getBaiduService();
                    if(service != null) {
                        String name = service.searchMagnetinfo(movie.getTorrentUrl());
                        if (!TextUtils.isEmpty(name)) {
                            path = name.substring(0, name.indexOf("/"));
                            movie.setPath(path);
                            dao.updateMovie(movie.getObjectId(), new String[]{"path"}, new String[]{path});
                        }
                    }
                }
                if(TextUtils.isEmpty(path))continue;
                path = path.length() > 40 ? path.substring(0,40) : path;
                updateUclinkTasks.put(path, movie);
            }
            getUclink();
        }
    }

    private void addOfflineTask(String magnetLink) throws IOException {
        //添加离线下载
        String url = "http://mydiskm.uc.cn/uclxmgr/newtask?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&fromdirid=&newlx_url=" + URLEncoder.encode(magnetLink, "UTF-8");
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Accept", "application/json, text/javascript, */*");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Referer", "http://mydiskm.uc.cn/file/detail?uc_param_str=frpfvesscplaprnisieint&appid=1&fid=10963927248353262593&dirId=4496758183449225217&type=all");
        httpService.execute(url, headers);
        url = "http://mydiskm.uc.cn/deposit/doSave?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2";
//        String data = "typeoffile=link_url&link_url=" + URLEncoder.encode(magnetLink, "UTF-8") + "&link_file_size=0&link_file_cookie=link_file_ref=&link_file_name=磁力链文件";
        headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,UC/151,alipay/un");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Referer", "http://mydiskm.uc.cn/deposit/index?uc_param_str=frpfvesscplaprnisieint&fromdirid=");
        headers.put("Accept-Encoding", "gzip");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("typeoffile","link_url"));
        params.add(new BasicNameValuePair("link_url",magnetLink));
        params.add(new BasicNameValuePair("link_file_size","0"));
        params.add(new BasicNameValuePair("link_file_cookie","link_file_ref="));
        params.add(new BasicNameValuePair("link_file_name","磁力链文件"));
        httpService.execute(url, params, headers);
    }
    private void queryOfflineTask() {
        queryOfflineThread = new Thread(new Runnable() {
            public void run() {
                while (tasks.size() > 0 && !Thread.currentThread().isInterrupted()) {
                    int offset = 0;
                    while (!stopOfflineTask) {
                        try {
                            //获取离线下载列表
                            String url = "http://mydiskm.uc.cn/uclxmgr/ajaxGetData?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447320945886" + (offset == 0 ? "" : "&&offset=" + offset);
                            String result = httpService.execute(url).getData();
                            JSONObject object = JSON.parseObject(result);
                            JSONArray array = object.getJSONArray("taskList");
                            if (array.size() == 0) break;
                            System.out.println(array.toString());
                            for (Object o : array) {
                                JSONObject obj = (JSONObject) o;
                                Boolean show = obj.getBoolean("show_save_to_nd");
                                if (obj.getInteger("status_code") == 0 && show != null ? show : false) {
                                    String taskId = obj.getString("task_id");
                                    //存至网盘
                                    url = "http://mydiskm.uc.cn/ajax/saveToNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447330257922&taskId=" + taskId;
                                    httpService.execute(url);
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
                    try {
                        Thread.sleep(1000 * 60 * 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("---(end) queryOfflineTask---");
            }
        });
        queryOfflineThread.start();
    }

    private void transeTask() {
        transeTaskThread = new Thread(new Runnable() {
            public void run() {
                while (!stopOfflineTask && !Thread.currentThread().isInterrupted() && tasks.size() > 0) {
                    int offset = 0;
                    boolean quit = false;
                    while (!quit) {
                        //列出文件列表
                        try {
                            String url = "http://mydiskm.uc.cn/netdisk/ajaxPhoneNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447331239396&dirId=2&type=all&edit_mode=0&moveState=0&offset=" + offset;
                            String result = httpService.execute(url).getData();
                            JSONArray fileDirList = JSON.parseObject(result).getJSONArray("dirList");
                            if (fileDirList.size() == 0) {
                                quit = true;
                            }
                            for (Object o1 : fileDirList) {
                                JSONObject fileDir = (JSONObject) o1;
                                String dirid = fileDir.getString("id");
                                String name = fileDir.getString("dirname");
                                name = name.replaceAll("amp;", "");
                                name = name.length() > 40 ? name.substring(0,40) : name;
                                BtMovie movie = tasks.remove(name);
                                if (movie != null) {
                                    //获取文件信息
                                    url = "http://mydiskm.uc.cn/netdisk/ajaxPhoneNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447331247447&dirId=" + dirid + "&type=all&edit_mode=0&moveState=0&offset=0";
                                    result = httpService.execute(url).getData();
                                    JSONObject fileObj = JSON.parseObject(result).getJSONArray("fileList").getJSONObject(0);
                                    String fid = fileObj.getString("id");
                                    String fname = fileObj.getString("filename");
                                    String djangoID = fileObj.getString("djangoID");
                                    String download720PLink = fileObj.getString("download").replaceAll("ext:uc_dw:", "").split("\\|")[0];
                                    String getDownloadUrl = "http://mydiskm.uc.cn/module/getDownloadLink?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&djangoId="+djangoID+"&filename="+URLEncoder.encode(fname)+"&_="+System.currentTimeMillis();
                                    result = httpService.execute(getDownloadUrl).getData();
                                    try {
                                        JSONObject download = JSON.parseObject(result);
                                        String downloadUrl = download.getString("link").replaceAll("ext:uc_dw:", "").split("\\|")[0];
                                        if(StringUtils.isEmpty(downloadUrl)){
                                            downloadUrl = null;
                                        }
                                        movie.setUclink(downloadUrl);
                                        movie.setUclink720P(download720PLink);
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
        });
        transeTaskThread.start();

    }

    private void getUclink() {
        getUpdateUCLinkThread = new Thread(new Runnable() {
            public void run() {
                int offset = 0;
                boolean quit = false;
                while (!quit && !stopUpdateUCLinkTask && !Thread.currentThread().isInterrupted()) {
                    //列出文件列表
                    try {
                        String url = "http://mydiskm.uc.cn/netdisk/ajaxPhoneNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1450514705837&dirId=2&type=all&edit_mode=0&moveState=0&offset=" + offset;
                        String result = httpService.execute(url).getData();
                        JSONArray fileDirList = JSON.parseObject(result).getJSONArray("dirList");
                        if (fileDirList.size() == 0) {
                            quit = true;
                        }
                        for (Object o1 : fileDirList) {
                            JSONObject fileDir = (JSONObject) o1;
                            String dirid = fileDir.getString("id");
                            String name = fileDir.getString("dirname");
                            name = name.replaceAll("amp;", "");
                            name = name.length() > 40 ? name.substring(0,40) : name;
                            BtMovie movie = updateUclinkTasks.remove(name);
                            if (movie != null) {
                                //获取文件信息
                                url = "http://mydiskm.uc.cn/netdisk/ajaxPhoneNd?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&_=1447331247447&dirId=" + dirid + "&type=all&edit_mode=0&moveState=0&offset=0";
                                result = httpService.execute(url).getData();
                                JSONObject fileObj = JSON.parseObject(result).getJSONArray("fileList").getJSONObject(0);
                                String fid = fileObj.getString("id");
                                String fname = fileObj.getString("filename");
                                String djangoID = fileObj.getString("djangoID");
                                String download720PLink = fileObj.getString("download").replaceAll("ext:uc_dw:", "").split("\\|")[0];
                                String getDownloadUrl = "http://mydiskm.uc.cn/module/getDownloadLink?uc_param_str=frpfvesscplaprnisieint&fr=android&pf=151&ve=10.6.0.620&ss=768x976&cp=isp:%E7%94%B5%E4%BF%A1;prov:%E4%B8%8A%E6%B5%B7;city:%E4%B8%8A%E6%B5%B7;na:%E4%B8%AD%E5%9B%BD;cc:CN;ac:&la=zh-CN&pr=UCMobile&ni=bTkwBNgw64Wvr2bb5Y5rnu47RZBvBmMEdF7waZeiM5dKNg%3D%3D&si=&ei=&nt=2&djangoId="+djangoID+"&filename="+URLEncoder.encode(fname)+"&_="+System.currentTimeMillis();
                                try {
                                    result = httpService.execute(getDownloadUrl).getData();
                                }catch (Exception e){

                                }
                                String downloadUrl = null;
                                try {
                                    JSONObject download = JSON.parseObject(result);//ext:uc_dw
                                    downloadUrl = download.getString("link").replaceAll("ext:uc_dw:", "").split("\\|")[0];
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(TextUtils.isEmpty(downloadUrl)){
                                    downloadUrl = null;
                                }
                                movie.setUclink(downloadUrl);
                                movie.setUclink720P(download720PLink);
                                updateMovie(movie);
                                System.out.println("update:" + movie.getName());
                            }
                        }
                        if (fileDirList.size() < 20) {
                            quit = true;
                            break;
                        }
                        offset += 20;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("--(end) update ucLink Task---");
            }
        });
        getUpdateUCLinkThread.start();
    }


    private void updateMovie(BtMovie movie){
        System.out.println("update movie:"+movie.getUclink());
        dao.updateMovie(movie.getObjectId(), new String[]{"path", "uclink"}, new String[]{movie.getPath(), movie.getUclink()});
    }
    private static String subMovieName(String name) {
        Pattern pattern = Pattern.compile("\\.\\w+");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return name.substring(0, name.indexOf(matcher.group()));
        }
        return name;
    }
    public static void main(String ... args) throws IOException {
        BaiduManager.getInstance().init();
        UCDiskTaskService service = new UCDiskTaskService();
        service.startOfflineTask();
//        service.startUpdateUCLinkTask();
    }
}
