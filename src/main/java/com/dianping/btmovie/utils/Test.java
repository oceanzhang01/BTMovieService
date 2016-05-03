package com.dianping.btmovie.utils;

import java.io.IOException;

/**
 * Created by oceanzhang on 15/8/11.
 */
public class Test {
    public static  void main(String ... args) throws IOException {
        String html = HttpUtils.get("http://pan.baidu.com/share/streaming?channel=chunlei&uk=778146116&type=M3U8_AUTO_480&path=%2F%E7%99%BE%E5%9B%A2%E5%A4%A7%E6%88%98.The.Hundred.Regiments.Offensive.2015.HD720P.X264.AAC.Mandarin.CHS-ENG.Mp4Ba%2FB%E5%9B%A2%E5%A4%A7%E6%88%98.The.Hundred.Regiments.Offensive.2015.HD720P.X264.AAC.Mandarin.CHS-ENG.Mp4Ba.mp4&sign=169868e9f7fa4c594b77aa0cb42621b98de0462f&timestamp=1442719668&shareid=3806523637");
        System.out.println(html);
    }
}
