package com.think.android.myclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static int MsgIP = 1;
    final static int MsgPort = 2;
    final static int ReceiveNum = 3;
    private EditText editText;
    private EditText editText2;
    private TextView receive;
    public static EditText editText3;
    private Button buttonSend;
    public static boolean SendFlag = false;
    private ServerSocket serverSocket =null;




    public static ArrayList<Socket> socketArrayList = new ArrayList<Socket>();
    private InputStream inputStream;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MsgIP:
                    editText.setText(msg.obj.toString());
                    break;
                case MsgPort:
                    editText2.setText(msg.obj.toString());
                    break;
                case ReceiveNum:
                    receive.append(msg.obj.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        receive = (TextView) findViewById(R.id.receive);
        buttonSend = (Button) findViewById(R.id.button);

        try{
            serverSocket = new ServerSocket(30000);
        }catch (IOException e){
            e.printStackTrace();
        }
        GetIpAddress.getLocalIpAddress(serverSocket);

        //获取IP、Port
        Message message_1 = handler.obtainMessage();
        message_1.what = MsgIP;
        message_1.obj = "IP: " + GetIpAddress.getIP();
        handler.sendMessage(message_1);
        Message message_2 = handler.obtainMessage();
        message_2.what = MsgPort;
        message_2.obj = "PORT: " + GetIpAddress.getPORT();
        handler.sendMessage(message_2);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendFlag = true;

            }
        });

        new Thread(networkTask).start();
    }

    /*
    * 服务器接受数据：
    * 多线程*/

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            /*try {
                ServerSocket serverSocket = new ServerSocket(30000);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept(); //应该放在其他线程里面去，不能放在主线程中
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    new ServerThread(socket,MainActivity.this).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    } ;

}
