package com.dikaros.wow;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dikaros.wow.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class EntryActivity extends AppCompatActivity {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(EntryActivity.this, LoginActivity.class);

                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    Intent intent2 = new Intent(EntryActivity.this, ShowActivity.class);

                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        //这个Activity处理一些事件
        new Thread() {
            @Override
            public void run() {
                try {
                    //休眠一秒
                    sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //获取本地存储的用户信息
                String userMsg = Util.getPreference(EntryActivity.this, "user_msg");

                //如果用户为空
                if (userMsg == null) {
                    //通知handler
                    handler.sendEmptyMessage(0);
                }
                //如果用户信息不为空
                else if (userMsg != null) {
                    try {
                        //解析用户信息
                        JSONObject root = new JSONObject(userMsg);
                        //获取用户名
                        String userName = root.getString("name");
                        //获取sessionId
                        String sessionId = root.getString("sessionId");
                        Config.userId = root.getLong("id");
                        //设置sessionId
                        Config.WEBSOCKET_SESSION = sessionId;
                        //设置用户名
                        Config.userName = userName;
                        //设置用户信息
                        Config.userMessage = root.getString("personalMessage");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(1);

            }
        }.start();


    }
}
