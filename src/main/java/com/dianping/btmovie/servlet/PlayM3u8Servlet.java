package com.dianping.btmovie.servlet;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by oceanzhang on 15/10/10.
 */
@WebServlet(name = "PlayM3u8Servlet",urlPatterns = "/PlayM3u8.ts")
public class PlayM3u8Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String u = new String(Base64.decodeBase64(request.getParameter("url")));
        URL url = new URL(u);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream in = new BufferedInputStream(conn.getInputStream());
        byte[] buffer = new byte[2048];
        response.setContentType("video/vnd.dlna.mpeg-tts");
        ByteArrayOutputStream out = new ByteArrayOutputStream() ;
        int len;
        while((len = in.read(buffer)) != -1){
            out.write(buffer,0,len);
            out.flush();
        }
        out.close();
        byte[] data = out.toByteArray();
        response.setContentLength(data.length);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        outputStream.write(data);
        outputStream.flush();
        in.close();
        outputStream.close();
    }
}
