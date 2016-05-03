package com.dianping.btmovie.ucdisk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by oceanzhang on 16/1/12.
 */
public class UCDiskServer extends Thread{
    private static ServerSocket serverSocket;
    private static volatile boolean stop = false;
    private static final Executor executor = new ThreadPoolExecutor(6,8,30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
    private static List<ClientSocket> socketList = new ArrayList<ClientSocket>();
    public static void main(String...args){
        new UCDiskServer().start();
    }
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(9999);
            while (!stop) {
                Socket socket = serverSocket.accept();
                System.out.println("accept:"+socket.getRemoteSocketAddress().toString());
                ClientSocket clientSocket = new ClientSocket(socket);
                socketList.add(clientSocket);
                executor.execute(clientSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void onClientSocketBroken(ClientSocket socket){
        socketList.remove(socket);
        socket.closeClient();
    }
    public static void startServer(){
        new UCDiskServer().start();
    }
    public static void stopServer(){
        for(ClientSocket socket : socketList){
            socket.closeClient();
        }
        socketList.clear();
        stop = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class ClientSocket implements Runnable{
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        public ClientSocket(Socket socket) {
            this.socket = socket;
        }
        private void closeClient(){
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private void pong(){
            write(1,null);
        }
        public void write(int code,String payload){
            JSONObject object = new JSONObject();
            object.put("code",code);
            if(payload != null) {
                object.put("payload", payload);
            }
            try {
                out.writeUTF(object.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
                onClientSocketBroken(this);
            }
        }
        public void run() {
            try {
                this.in = new DataInputStream(socket.getInputStream());
                this.out = new DataOutputStream(socket.getOutputStream());
                while (socket.isConnected()) {
                    String utf = in.readUTF();
                    JSONObject object = JSON.parseObject(utf);
                    int code = object.getInteger("code");
                    if(code == 0){
                        System.out.println("receive ping.");
                        pong();
                        continue;
                    }
                    String payload = object.getString("payload");
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                onClientSocketBroken(this);
            }
        }
    }
}
