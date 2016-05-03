package com.dianping.btmovie.baidu;

import java.util.List;

/**
 * Created by min on 2015/5/3.
 */
public class BaiduDiskFile {
        private List<TorrentFile> magnet_info;
        private int total;
        private String request_id;

    public List<TorrentFile> getMagnet_info() {
        return magnet_info;
    }

    public void setMagnet_info(List<TorrentFile> magnet_info) {
        this.magnet_info = magnet_info;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
}
