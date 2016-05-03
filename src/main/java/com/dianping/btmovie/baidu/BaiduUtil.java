//package com.dianping.btmovie.baidu;
//
//import java.io.IOException;
//
//public class BaiduUtil {
//
//    public static void main(String ...args){
//        System.out.println(addOfflineTask("magnet:?xt=urn:btih:c888890bbc75526f415c46551481e45e87dc23ab&tr=http://bt.mp4ba.com:2710/announce"));
//    }
//
//	private static BaiduService baidu = BaiduService.getInstance();
//
//	static int retryCount = 0;
//	/**
//	 * 根据磁力链接获取播放数据
//	 * @param torrentUrl
//	 * @return
//	 */
//	public static String addOfflineTask(String torrentUrl){
//		try {
//			String content = baidu.addOfflineTask(torrentUrl);
//			retryCount = 0;
//			return content;
//		} catch (IOException e) {
//			e.printStackTrace();
//			if(e instanceof BaiduException){
//				BaiduException ex = (BaiduException)e;
//				int code = ex.getStatucCode();
//				if (code == 403) {
//					try {
//						String token = baidu.login("546107362@qq.com","zm921210");
//						if (token != null) {
//							if (++retryCount < 2) {
//								return addOfflineTask(torrentUrl);
//							}
//						}
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
//		}
//		return null;
//	}
//
//	public static String readM3u8Content(String path){
//		try {
//			return baidu.readM3u8Content(path);
//		} catch (IOException e) {
//			e.printStackTrace();
//			BaiduException ex = (BaiduException)e;
//			int code = ex.getStatucCode();
//			if (code == 403) {
//				try {
//					String token = baidu.login("546107362@qq.com","zm921210");
//					if (token != null) {
//						if (++retryCount < 2) {
//							return readM3u8Content(path);
//						}
//					}
//                    System.err.println("login failed");
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//		return null;
//	}
//
//
//    public static byte[] getImage(String path){
//        try {
//            return baidu.getImage(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static boolean checkLogin(){
//		try {
//			String token = baidu.checkLogin();
//			if(token != null){
//				return true;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//}
