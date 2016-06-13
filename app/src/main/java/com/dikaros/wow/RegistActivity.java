package com.dikaros.wow;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dikaros.wow.net.asynet.AsyNet;
import com.dikaros.wow.net.asynet.NormalAsyNet;
import com.dikaros.wow.util.AlertUtil;
import com.dikaros.wow.util.NetUtil;
import com.dikaros.wow.util.SimpifyUtil;
import com.dikaros.wow.util.annotation.FindView;
import com.dikaros.wow.util.annotation.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RegistActivity extends AppCompatActivity implements AsyNet.OnNetStateChangedListener<String> {


    @FindView(R.id.regist_phone)
    EditText etPhone;

    @FindView(R.id.regist_password)
    EditText etPasswd;

    @FindView(R.id.regist_confirm)
    EditText etConfirm;

    @FindView(R.id.btn_get_confirm)
    Button btnGetConfirm;

    @FindView(R.id.btn_regist_action)
    Button btnRegist;

    @FindView(R.id.regist_progress)
    ProgressBar progressBar;

    @FindView(R.id.email_regist_form)
    LinearLayout llRegist;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        SimpifyUtil.findAll(this);
        timer = new Timer();
    }


    NormalAsyNet asyNet;

    @OnClick(R.id.btn_regist_action)
    public void attempRegist(View v) {
        if (etPhone.getText().toString().length() < 6) {
            etPasswd.setError("密码需要大于6位");
            return;
        }

        if (etPhone.getText().toString().length() != 11) {
            etPhone.setError("手机号码不合法");
            return;
        }
        if (etConfirm.getText().toString().equals("")) {
            etConfirm.setError("不能为空");
            return;
        }


        if (confirmCode == null || !etConfirm.getText().toString().equals(confirmCode)) {
            etConfirm.setError("验证码输入错误");
            return;
        }

        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put("phone", etPhone.getText().toString());
            object.put("password", etPasswd.getText().toString());
            array.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        asyNet = new NormalAsyNet(Config.REGIST_ADDRESS, "jsonFile", array.toString(), AsyNet.NetMethod.POST);
        asyNet.setOnNetStateChangedListener(this);
        asyNet.execute();

    }

    public final int ON_COUNT = 1;
    public final int START_COUNT = 2;
    public final int FINISH_COUNT = 3;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ON_COUNT:
                    time--;
                    btnGetConfirm.setText("获取验证码(" + time + ")");
                    break;
                case START_COUNT:
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(time == 0 ? FINISH_COUNT : ON_COUNT);
                        }
                    };
                    timer.schedule(timerTask, 0, 1000);

                    btnGetConfirm.setEnabled(false);
                    Config.inWait = true;

                    break;
                case FINISH_COUNT:
                    timerTask.cancel();
                    time = 60;
                    btnGetConfirm.setEnabled(true);
                    Config.inWait = false;
                    btnGetConfirm.setText("获取验证码");

                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    Random random = new Random();
    String confirmCode;

    @OnClick(R.id.btn_get_confirm)
    public void getSmsCode(View v) {

        if (etPhone.getText().toString().length() != 11) {
            etPhone.setError("手机号码不合法");
            return;
        }
        if (!Config.inWait) {
            //0-99999
            int i = random.nextInt(899999) + 10000;
            confirmCode = String.valueOf(i);
//            AlertUtil.toastMess(this,confirmCode);
//            HashMap<String,Object> map = new HashMap<>();
//            map.put("phone",etPhone.getText().toString());
//            map.put("content",confirmCode);
//            NormalAsyNet net = new NormalAsyNet("http://apis.baidu.com/baidu_communication/sms_verification_code/smsverifycode",map, AsyNet.NetMethod.GET);
//            net.addHeader("apikey","db642b2fac4fafe26849179ad8883592");
//            net.execute();
            new Thread() {
                @Override
                public void run() {
                    String t = NetUtil.sendSmsCode(etPhone.getText().toString(), confirmCode);
                    if (t != null) {
                        handler.sendEmptyMessage(START_COUNT);
                    }
                }
            }.start();

        }
    }


    int time = 60;

    TimerTask timerTask = null;

    @Override
    public void beforeAccessNet() {
        btnRegist.setEnabled(false);
        llRegist.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void afterAccessNet(String result) {
        btnRegist.setEnabled(true);
        llRegist.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (result != null && !result.equals("{}")) {
            try {
                JSONObject object = new JSONObject(result);
                int code = object.getInt("code");
                String message = object.getString("message");
                if (code == 201) {
                    //注册
                    AlertUtil.toastMess(this, message + "");
                    //保存用户信息
                    finish();
                } else if (code == 202) {
                    etPhone.setError("手机号已被使用");
                } else if (code == 500) {
                    AlertUtil.showSnack(btnGetConfirm, message + "");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void whenException(Throwable t) {
        llRegist.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        AlertUtil.showSnack(btnRegist,"网络异常");
        btnRegist.setEnabled(true);

    }

    @Override
    public void onProgress(Integer progress) {

    }

}
