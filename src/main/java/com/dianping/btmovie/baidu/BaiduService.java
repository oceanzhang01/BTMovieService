package com.dianping.btmovie.baidu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianping.btmovie.baidu.BaiduManager.BaiduAccount;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduService {
	private static final String SAVE_PATH = "/BTGODMOVIE";
	private BaiduConnectService bc = null;
	private BaiduAccount baiduAccount;
	private boolean isLogin;
	private String bdToken;
	private final HashMap<String,String> headers = new HashMap<String, String>(1);
	private static final String HOME_URL = "http://pan.baidu.com";
	public BaiduService(BaiduAccount baiduAccount) {
		this.bc = new BaiduConnectService(baiduAccount.user);
		this.baiduAccount = baiduAccount;
		this.login();
	}

	public boolean isLogin() {
		return isLogin;
	}

	public BaiduAccount getBaiduAccount() {
		return baiduAccount;
	}

	/**
	 * 查询种子文件信息URL
	 * @return
	 */
	private String queryMagnetinfoUrl() {
		return "http://pan.baidu.com/rest/2.0/services/cloud_dl?bdstoken="
				+ bdToken + "&channel=00000000000000000000000000000000&clienttype=8&app_id=250528";
	}

	/**
	 * 获取待下载种子文件序号
	 * @param magnetPath 磁力链接
	 * @return
	 * @throws IOException
	 */
	private int queryMagnetinfo(String magnetPath) throws IOException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("method", "query_magnetinfo"));
		list.add(new BasicNameValuePair("source_url", magnetPath));
		list.add(new BasicNameValuePair("save_path", "/"));
		list.add(new BasicNameValuePair("type", "4"));
		String body = bc.execute(queryMagnetinfoUrl(), list, headers);
		BaiduDiskFile baiduFile = JSON.parseObject(body, BaiduDiskFile.class);
		if (baiduFile != null && baiduFile.getMagnet_info() != null) {
			int index = 0;
			for (TorrentFile file : baiduFile.getMagnet_info()) {
				++index;
				if (file.getSize() >= 1024 * 1024 * 50) {
					return index;
				}
			}
		}
		return -1;
	}
    public String searchMagnetinfo(String hash) {
        try {
        	String magnetPath = hash;
        	if(!hash.startsWith("magnet:?xt=urn:btih:")){
        		magnetPath = "magnet:?xt=urn:btih:" + hash;
        	}
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("method", "query_magnetinfo"));
            list.add(new BasicNameValuePair("source_url", magnetPath));
            list.add(new BasicNameValuePair("save_path", "/"));
            list.add(new BasicNameValuePair("type", "4"));
            String body = bc.execute(queryMagnetinfoUrl(), list, headers);
            BaiduDiskFile baiduFile = JSON.parseObject(body, BaiduDiskFile.class);
            if (baiduFile != null && baiduFile.getMagnet_info() != null) {
                int index = 0;
                for (TorrentFile file : baiduFile.getMagnet_info()) {
                    ++index;
                    if (file.getSize() >= 1024 * 1024 * 50) {
                        return file.getFile_name();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * 添加离线下载 然后获取云盘文件路径
	 * @param fileHash
	 * @return
	 * @throws IOException
	 */
	public String addOfflineTask(String fileHash)  {
        try {
            String magnetPath = "magnet:?xt=urn:btih:" + fileHash;
            int index = queryMagnetinfo(magnetPath);
            if (index == -1) {
                throw new BaiduException(-1, "没有找到待下载的视频文件!");
            }
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("method", "add_task"));
            list.add(new BasicNameValuePair("source_url", magnetPath));
            list.add(new BasicNameValuePair("save_path", SAVE_PATH));
            list.add(new BasicNameValuePair("type", "4"));
            list.add(new BasicNameValuePair("file_sha1", ""));
            list.add(new BasicNameValuePair("selected_idx", index + ""));
            list.add(new BasicNameValuePair("task_from", "1"));
            list.add(new BasicNameValuePair("t", System.currentTimeMillis() + ""));
            String body = bc.execute(queryMagnetinfoUrl(), list, headers);
            int rapid_download = JSONObject.parseObject(body).getInteger(
                    "rapid_download");
            final int task_id = JSONObject.parseObject(body).getInteger("task_id");
            if (rapid_download == 1) {

                // add offline task success
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            clearOfflineTask();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                String oldPath = getRealPlayPath(task_id);
                if (oldPath != null) {
                    String newPath = movieAndrename(oldPath, "/movies", fileHash + oldPath.substring(oldPath.lastIndexOf(".")));
                    return newPath;
                }

            }
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cancelTask(task_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (IOException e){
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * 移动文件到指定路径 并且重命名  返回最新文件路径
	 * @param path
	 * @param movieTo
	 * @param newName
	 */
	private String movieAndrename(String path,String movieTo,String newName) throws IOException {
		String url = "http://pan.baidu.com/api/filemanager?channel=chunlei&clienttype=0&web=1&opera=move&async=2&bdstoken="+bdToken+"&channel=chunlei&clienttype=0&web=1&app_id=250528";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//        path = "/BTGODMOVIE/lala/lalala.mp4";
		JSONObject object = new JSONObject();
		object.put("path", path);
		object.put("dest", movieTo);// /movies
//        newName = path.substring(path.lastIndexOf("/") + 1);
        object.put("newname", newName);
        object.put("ondup","overwrite");
		JSONArray array = new JSONArray();
        array.add(object);
        params.add(new BasicNameValuePair("filelist", array.toJSONString()));
        HashMap<String,String> header = new HashMap<String, String>();
        header.putAll(headers);
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		String result = bc.execute(url, params, header); //taskid
        JSONObject obj = JSONObject.parseObject(result);
        if(obj.getIntValue("errno") == 0){
            int status;
            while((status = queryMovieAndRemoveTask(obj.getString("taskid"),params,header)) >=0){
                if(status == 0){
                    return movieTo+"/"+newName;
                }
            }

		}
		System.out.println(result);
		throw new IOException("movie and rename failed");
	}
    private int queryMovieAndRemoveTask(String taskId,List<NameValuePair> params,HashMap<String,String> header) throws IOException {
        String url = "http://pan.baidu.com/share/taskquery?&taskid="+taskId+"&bdstoken="+bdToken+"&channel=chunlei&clienttype=0&web=1&app_id=250528";
        String result = bc.execute(url, params, header);
        JSONObject obj = JSON.parseObject(result);
        if(obj != null && obj.getIntValue("errno") == 0 ){
            int taskError = obj.getIntValue("task_errno");
            if(taskError == 0){
                if(obj.getString("status").equals("success")){
                    return  0;
                }
                return  1;
            }else if(taskError == -30){ //已经有同名文件
                return 0;
            }
        }
        return  -1;
    }
    private String getRealPlayPath(int taskId) throws IOException {
        String url = "http://pan.baidu.com/rest/2.0/services/cloud_dl?bdstoken="
                +bdToken +"&task_ids="
                +taskId+"&op_type=1&method=query_task&app_id=250528&t="
                +System.currentTimeMillis()+"&channel=00000000000000000000000000000000&clienttype=8&app_id=250528" ;
        String str = bc.execute(url,headers);
        JSONObject object = JSONObject.parseObject(str);
        if(object.getInteger("error_code") == 0){
           JSONObject taskInfo = object.getJSONObject("task_info").getJSONObject(taskId + "");
            String savePath = taskInfo.getString("save_path");
            JSONArray  filePaths = taskInfo.getJSONArray("file_list");
            if(filePaths != null && filePaths.size()>0){
                JSONObject obj = (JSONObject) filePaths.get(0);
                return savePath+obj.getString("file_name");
            }
        }
        return null;
    }
	private void cancelTask(int taskId) throws Exception {
		String url = "http://pan.baidu.com/rest/2.0/services/cloud_dl?bdstoken="
				+ bdToken
				+ "&task_id="
				+ taskId
				+ "&method=cancel_task&app_id=250528&t="
				+ System.currentTimeMillis()
				+ "&bdstoken="
				+ bdToken
				+ "&channel=00000000000000000000000000000000&clienttype=8&app_id=250528";
		bc.execute(url, headers);
	}

	private String clearTaskUrl() {
		return "http://pan.baidu.com/rest/2.0/services/cloud_dl?bdstoken="
				+ bdToken + "&method=clear_task&app_id=250528&t="
				+ System.currentTimeMillis() + "&bdstoken=" + bdToken
				+ "&channel=00000000000000000000000000000000&clienttype=8&app_id=250528";
	}

	private void clearOfflineTask() throws Exception {
		bc.execute(clearTaskUrl(),headers);
	}

    /**
     * 根据云盘路径 读取播放m3u8内容
     * @param path
     * @return
     * @throws IOException
     */
    public String readM3u8Content(String path) {
        if(path == null || path.trim().equals(""))
            return null;
        try {
            String url="https://pcs.baidu.com/rest/2.0/pcs/file?method=streaming&path="+URLEncoder.encode(path,"utf-8")+"&type=M3U8_AUTO_480&app_id=250528";
            HashMap<String,String>headre=new HashMap<String,String>();
            headre.put("X-Requested-With","ShockwaveFlash/17.0.0.134");
            headre.putAll(headers);
            String body=bc.execute(url, headre);
//        System.out.print(body);
            return body;
        }catch (IOException e){
            e.printStackTrace();
        }
        return  null;
    }

    private String searchRemoteFileUrl(String path, String key) {
        return "http://pan.baidu.com/api/search?channel=chunlei&clienttype=0&web=1&num=100&page=1&dir=&order=name&desc=1&showempty=0&key="
                +key
                +"&searchPath="
                +path
                +"&recursion=1&_="
                +System.currentTimeMillis()
                +"&bdstoken="+bdToken
                +"&channel=chunlei&clienttype=0&web=1&app_id=250528";
    }

    public String searchRemoteFile(String path,String fileKey) {
        try {
            String body = bc.execute(searchRemoteFileUrl(path, fileKey),headers);
            JSONObject object = JSON.parseObject(body);
            if (object != null && object.getIntValue("errno") == 0) {
                JSONArray array = JSON.parseArray(object.getString("list"));
                if (array != null && array.size() > 0) {
                    return array.getJSONObject(0).getString("path");
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return  null;
    }
    public List<String> listFile(String path) throws IOException{
    	String url = "http://pan.baidu.com/api/list?channel=chunlei&clienttype=0&web=1&num=10000&page=1&dir=%2Fmovies&order=time&desc=1&showempty=0&_=1446816637147&bdstoken="+bdToken+"&channel=chunlei&clienttype=0&web=1&app_id=250528";
    	String json = bc.execute(url);
    	JSONObject obj = JSON.parseObject(json);
    	List<String> paths = new ArrayList<String>();
    	if(obj != null && obj.getInteger("errno") == 0){
    		JSONArray arr = obj.getJSONArray("list");
    		for(Object o:arr){
    			JSONObject oo = (JSONObject) o;
    			paths.add(oo.getString("path"));
    		}
    	}
    	return paths;
    }
    public byte[] getImage(String path) throws IOException{
        if (path == null || path.trim().equals("")){
            return  null;
        }
        String url = "http://pcs.baidu.com/rest/2.0/pcs/thumbnail?method=generate&path="+URLEncoder.encode(path,"utf-8")+"&quality=100&size=c200_u120&app_id=250528&devuid=141813160743353";
        return bc.executeGetData(url,headers);
    }

	public synchronized boolean checkLogin(){
		try {
			String body = bc.execute("http://pan.baidu.com/disk/home", headers);
			//"bdstoken":"4e4e2a44345082612aa76307e0772f21"
            Pattern pattern = Pattern.compile("\"bdstoken\":\"\\w+\"");
			Matcher matcher = pattern.matcher(body);
			if (matcher.find()) {
				String group = matcher.group().replaceAll("\"bdstoken\"","");
				this.bdToken = Util.substring(group, "\"", "\"");
				isLogin = true;
				return true;
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		isLogin = false;
		return false;
	}

	// ///////////////login////////////////////////////////
	public synchronized boolean login(){
		try{
            bc.execute("http://www.baidu.com/");
            bc.execute("http://wappass.baidu.com/wp/api/login?v=1439520441484",
                    produceFormEntity(baiduAccount.user, baiduAccount.psd));
            return checkLogin();
		}catch(Exception e){
			return false;
		}
	}

	private  List<NameValuePair> produceFormEntity(String username,String password)
			throws UnsupportedEncodingException {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("tt", "" + System.currentTimeMillis()));
		list.add(new BasicNameValuePair("tpl", "neidisk"));
		list.add(new BasicNameValuePair("login_share_strategy", "disabled"));
		list.add(new BasicNameValuePair("isPhone", ""));
		list.add(new BasicNameValuePair("username", username));
		list.add(new BasicNameValuePair("password", password));
		list.add(new BasicNameValuePair("verifycode", ""));
		list.add(new BasicNameValuePair("clientfrom", "native"));
		list.add(new BasicNameValuePair("client", "android"));
		list.add(new BasicNameValuePair("adapter", "3"));
		list.add(new BasicNameValuePair("act", "implicit"));

		list.add(new BasicNameValuePair("loginLink", "0"));
		list.add(new BasicNameValuePair("smsLoginLink", "0"));
		list.add(new BasicNameValuePair("lPFastRegLink", "0"));
        list.add(new BasicNameValuePair("subpro", "netdiskandroid"));
		list.add(new BasicNameValuePair("action", "login"));
		list.add(new BasicNameValuePair("loginmerge", "1"));
		list.add(new BasicNameValuePair("isphone", "0"));
		list.add(new BasicNameValuePair("logLoginType", "sdk_login"));

		return list;
	}

}
