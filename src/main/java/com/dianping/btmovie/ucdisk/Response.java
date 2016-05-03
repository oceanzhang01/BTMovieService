package com.dianping.btmovie.ucdisk;

import java.util.Map;

/**
 * Created by oceanzhang on 16/1/13.
 */
public class Response {
    private int code;
    private String data;
    private Map<String,String> headers;
    private String error;

    public Response(int code, String data, Map<String, String> headers, String error) {
        this.code = code;
        this.data = data;
        this.headers = headers;
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
