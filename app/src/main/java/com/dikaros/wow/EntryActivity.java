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
//            Intent intent = new Intent(EntryActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
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
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                handler.sendEmptyMessage(1);
                String userMsg=Util.getPreference(EntryActivity.this, "user_msg") ;
                if (userMsg== null) {

                    handler.sendEmptyMessage(0);
                } else {
                    if (userMsg!=null){
                        try {
                            JSONObject root = new JSONObject(userMsg);
                            String userName = root.getString("name");
                            String userPassword = root.getString("password");
                            String userPhone = root.getString("phone");
                            String sessionId = root.getString("sessionId");
                            Config.WEBSOCKET_SESSION = sessionId;
                            Config.userId = root.getLong("id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();


    }
}
