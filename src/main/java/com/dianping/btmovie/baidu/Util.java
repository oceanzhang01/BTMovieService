package com.dianping.btmovie.baidu;

/**
 * Created by min on 2015/5/3.
 */
public class Util {
    public static String substring(String str, String s1, String s2) {
        // 1、先获得0-s1的字符串，得到新的字符串sb1
        // 2、从sb1中开始0-s2获得最终的结果。
        try {
            StringBuffer sb = new StringBuffer(str);
            String sb1 = sb.substring(sb.indexOf(s1) + s1.length());
            return String.valueOf(sb1.substring(0, sb1.indexOf(s2)));
        } catch (StringIndexOutOfBoundsException e) {
            return str;
        }
    }
}
