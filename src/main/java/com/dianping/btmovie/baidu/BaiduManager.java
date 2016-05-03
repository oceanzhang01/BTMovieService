package com.dianping.btmovie.baidu;

import java.util.*;

import com.dianping.btmovie.bmob.BmobHelper;

/**
 * Created by oceanzhang on 15/9/28.
 */
public class BaiduManager {
    private List<BaiduAccount> baiduAccounts = new ArrayList<BaiduAccount>();
    private List<BaiduService> baiduServices = new ArrayList<BaiduService>();
    private boolean destory = false;
    private BaiduManager(){
    }
    public void init(){
    	List<BaiduAccount> accounts = BmobHelper.getInstance().getBaiduAccounts();
    	if(accounts != null && accounts.size() >0 ){
    		baiduAccounts.addAll(accounts);
    	}else{
    		baiduAccounts.add(new BaiduAccount("546107362@qq.com","zm921210"));
            baiduAccounts.add(new BaiduAccount("1099788817@qq.com","zm921210"));
            baiduAccounts.add(new BaiduAccount("nieiqmhj997038@sina.com","sunny968"));
    	}
    	for(BaiduAccount account:baiduAccounts){
        	BaiduService service = new BaiduService(account);
        	baiduServices.add(service);
        }
        new Thread(new Runnable() {
            public void run() {
                while (!destory){
                	try {
                        Thread.sleep(1000*60*10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<BaiduService> unLogins = new ArrayList<BaiduService>();
                    for(BaiduService service:baiduServices){
                       if(!service.checkLogin()){
                           unLogins.add(service);
                       }
                    }
                    synchronized (baiduServices){
                        for(BaiduService service:unLogins){
                        	service.login();
                        }
                    }
                    unLogins.clear();
                }
            }
        }).start();
    }
    public void destory(){
    	this.destory = true;
    }
    private static BaiduManager instance = null;

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new BaiduManager();
        }
    }

    public static BaiduManager getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }
    
    public List<BaiduService> getBaiduServices() {
		return baiduServices;
	}
	public BaiduService getBaiduService(String usre){
        for(BaiduService baiduService:baiduServices){
            if(baiduService.getBaiduAccount().user.equals(usre)){
                return baiduService;
            }
        }
        return null;
    }
    /**
     * 随机返回一个可用的BaiduService
     * @return
     */
    public BaiduService getBaiduService(){
        if(baiduServices != null && baiduServices.size() > 0){
        	Collections.shuffle(baiduServices);
        	for(BaiduService service:baiduServices){
                if(service.isLogin()){
                    return service;
                }
            }
        }
        return null;
    }
    public static class BaiduAccount {
        public BaiduAccount(String user, String psd) {
            this.user = user;
            this.psd = psd;
        }
        
        public BaiduAccount() {
		}

		String user;
        String psd;
        boolean isUsed = false;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPsd() {
            return psd;
        }

        public void setPsd(String psd) {
            this.psd = psd;
        }
    }
}
