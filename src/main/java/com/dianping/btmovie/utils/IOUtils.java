package com.dianping.btmovie.utils;

import java.io.*;

/**
 * Created by oceanzhang on 16/1/13.
 */
public class IOUtils {
    public static String readStreamUTF8(InputStream in) throws IOException {
        if(in == null)
            throw new IOException("in == null");
        byte[] data = readStream(in);
        return new String(data,"utf-8");
    }
    public static byte[] readStream(InputStream in)throws IOException{
        if(in == null)
            throw new IOException("in == null");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1){
            bout.write(buffer,0,len);
        }
        in.close();
        return bout.toByteArray();
    }

    public static void writeStream(OutputStream out,byte[] data){

    }

    public static String readFile(String path) throws IOException {
        FileInputStream in = new FileInputStream(path);
        String s = readStreamUTF8(in);
        in.close();
        return s;
    }
    public static void writeFile(String path,String data) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        out.write(data.getBytes("utf-8"));
        out.close();
    }
}
