package com.example.violetsky.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button button;
    private TextView textView1;
    private EditText editView, editView1;
    private String clientMessage, serverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        textView1 = (TextView) findViewById(R.id.textView1);
        editView = (EditText) findViewById(R.id.editText);
        editView1 = (EditText) findViewById(R.id.editText1);
        button = (Button) findViewById(R.id.button);
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
        textView.setText("My Ip Address : " + ip);
        editView1.setHint("Server IPAddress");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientMessage = String.valueOf(editView.getText());
                serverIP = String.valueOf(editView1.getText());
                editView.setText("");
                CreateThread(false);
            }
        });


    }

    public void ServerSocket(ServerSocket ss) {
        try {
            ss = new ServerSocket(9999);
            Socket sc;
            while (true) {
                sc = ss.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                line = br.readLine();
                while(line != null){
                    sb.append(line + "\n");
                    line = br.readLine();
                }
//                handler.obtainMessage(Integer.parseInt(br.readLine()));
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("Content", sb.toString());
                bundle.putString("IPAddress", String.valueOf(sc.getRemoteSocketAddress()));
                msg.obj = bundle;
                handler.sendMessage(msg);
//                System.out.println("Connection from Client(" + sc.getInetAddress().getHostAddress() + ":" + sc.getPort() + ")" + br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ClientSocket(Socket cs) {
        try {
            cs = new Socket(serverIP, 9999);
            DataOutputStream out = new DataOutputStream(cs.getOutputStream());
            Log.e("ClientSocket", clientMessage);
            out.writeChars(clientMessage);
            cs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String Text;
            Text = (String) textView1.getText();
            Bundle bundle = (Bundle) msg.obj;
            textView1.setText("Message from " + bundle.getString("IPAddress") +"\n" + bundle.getString("Content") + "\n\n" + Text);
        }
    };

    public void CheckConnect() {
        ConnectivityManager conManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        String Text;
        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    Text = (String) textView1.getText();
                    textView1.setText("Connect with Wifi" + "\n" + Text);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    Text = (String) textView1.getText();
                    textView1.setText("Connect with 3G/4Gi" + "\n" + Text);
                    break;
                default:
                    break;
            }
        } else {
            Text = (String) textView1.getText();
            textView1.setText("準備開啟....\n" + "未開啟Wifi" + "\n" + Text);
//            OpenWifi();
        }
    }

    public void OpenWifi() {
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled())
            wifi.setWifiEnabled(true);
        String Text = (String) textView1.getText();
        textView1.setText("已開啟Wifi" + "\n" + Text);
//        CheckConnect();
    }

    public void CreateThread(boolean isServer) {
//        if (thread != null) {
//        } else {
        Thread thread_s, thread_c;
        if (isServer) {
            thread_s = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerSocket ss = null;
                    ServerSocket(ss);
//
//                    try {
//                        int i = 0;
//                        while (true) {
//                            Message msg = new Message();
//                            msg.obj = i;
//                            handler.sendMessage(msg);
//                            Thread.sleep(1000);
//                            i++;
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }) {
            };
            thread_s.start();
        } else {
            thread_c = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("ThreadClient", "");
                    Socket cs = null;
                    ClientSocket(cs);
                }
            });
            thread_c.start();
        }
//            thread.start();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CreateThread(true);
//        CheckConnect();
    }
}
