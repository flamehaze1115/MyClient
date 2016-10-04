package com.think.android.myclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

/**
 * Created by Think on 2016/9/30.
 */
public class ServerThread extends Thread {

    private Socket socket;
    private String content = null;
    private Context context;

    public ServerThread(Socket socket, Context context) throws IOException{
        this.socket = socket;
        this.context = context;
        content = MainActivity.editText3.toString();
    }
    @Override
    public void run() {
        String line = null;
        InputStream inputStream;
        OutputStream outputStream;

        try{
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            if(MainActivity.SendFlag) {
                outputStream.write(content.getBytes("utf-8"));
                MainActivity.SendFlag = false;

                outputStream.flush();
            }
            //半关闭socket
            socket.shutdownOutput();
            //获取客户端的信息
            Handler handler = new Handler();
            Message msg_receive = handler.obtainMessage();
            msg_receive.what = MainActivity.ReceiveNum;
            msg_receive.obj = "Receive";
            while((line=bufferedReader.readLine())!=null){
              //  MainActivity.receive.append(line);   //不能在子线程中更新UI元素
                msg_receive.obj = msg_receive+line;
            }
            handler.sendMessage(msg_receive);
            //关闭输入输出流
            outputStream.close();
            bufferedReader.close();
            inputStream.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
