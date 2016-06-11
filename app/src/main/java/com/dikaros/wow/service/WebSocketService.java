package com.dikaros.wow.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dikaros.wow.Config;
import com.dikaros.wow.net.websocket.BaseWebSocketClient;

import java.net.URISyntaxException;
import java.util.Random;

public class WebSocketService extends Service {


    //临时数据计数器，记录传来的数据条数
    public  static int messageCount=0;

    //websocket打开
    public static final String ACTION_WEBSOCKET_OPEN = "com.dikaros.websocket.open";
    //websocket收到数据
    public static final String ACTION_WEBSOCKET_ON_MESSAGE = "com.dikaros.websocket.onmessage";

    //websocket关闭
    public static final String ACTION_WEBSOCKET_CLOSE = "com.dikaros.websocket.close";

    public static final String DATA_MESSAGE_COUNT = "websocket_message_count";
    //websocket关闭原因
    public static final String WEBSOCKET_CLOSE_REASON = "websocket_close_by_remote";

    //websocket信息
    public static final String WEBSOCKET_MESSAGE = "websocket_message";

    //启动websocket
    public static final String START_WEBSOCKET = "startWebSocketClient";

    //关闭websocekt
    public static  final  String CLOSE_WEBSOCKET = "closeWebSokcetClient";

    public WebSocketService() {

    }

    boolean startOnce = false;

    boolean connected = false;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Timer timer=new Timer();
//        timer.schedule(task,2000,10000);

    }
    //WebSocket客户端
    BaseWebSocketClient client = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            boolean start = intent.getBooleanExtra(START_WEBSOCKET, false);
            if (start&&client==null) {
               doConnectWebSocket();
            }

            boolean close = intent.getBooleanExtra(CLOSE_WEBSOCKET, false);
            if (close&&client!=null) {
                client.close();
                client = null;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        client =null;
        super.onDestroy();
    }


    /**
     * 创建并连接websocket
     */
    private synchronized void doConnectWebSocket(){
        try {
            client = new BaseWebSocketClient(Config.WEBSOCKET_ADDRESS);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.setWebSocketListener(new BaseWebSocketClient.WebSocketClientListener() {
            @Override
            public void onOpen() {
                Log.i("websocket", "websocket open");
                Intent openIntent = new Intent();
                openIntent.setAction(ACTION_WEBSOCKET_OPEN);
                sendBroadcast(openIntent);
                Random random = new Random();
                int userId = random.nextInt(89999)+10000;
                client.send("{\"mtype\":\"100100\",\"tuser\":null,\"fuser\":\""+userId+"\",\"messageBody\":null}");
                Log.i("websocket","send:{\"mtype\":\"100100\",\"tuser\":null,\"fuser\":\""+userId+"\",\"messageBody\":null}");
                startOnce = true;
                connected = true;
            }

            @Override
            public void onMessage(String message) {
                messageCount++;
                Log.i("websocket", "websocket onMessage"+messageCount);
                Intent messageIntent = new Intent();
                messageIntent.setAction(ACTION_WEBSOCKET_ON_MESSAGE);
                messageIntent.putExtra(WEBSOCKET_MESSAGE, message);
                messageIntent.putExtra(DATA_MESSAGE_COUNT,messageCount);
                sendBroadcast(messageIntent);

            }

            @Override
            public void onClose(boolean remote) {
                messageCount = 0;
                Intent closeIntent = new Intent();
                closeIntent.setAction(ACTION_WEBSOCKET_CLOSE);
                closeIntent.putExtra(WEBSOCKET_CLOSE_REASON, remote);
                sendBroadcast(closeIntent);
                Log.i("websocket", "websocket close");
                connected = false;
               // 试着重新连接
                client.close();
                client = null;

            }
        });
        //连接服务器
        client.connect();
    }

    boolean reconnector = false;




}
