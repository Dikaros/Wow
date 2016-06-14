package com.dikaros.wow;

import com.dikaros.wow.bean.ImMessage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dikaros on 2016/6/11.
 */
public class Config {
    public static String APP_ID = "com.dikaros.wow";

    /**
     * WebSocket的连接地址
     */

    public static long userId = 0;

    public static HashMap<Long,List<ImMessage>> reveivedMap=new HashMap<>();

    public static HashMap<Long,List<ImMessage>> sendedMap=new HashMap<>();

    public static String WEBSOCKET_SESSION = null;

    public static boolean inWait = false;

    public static final String HOST = "http://123.206.75.202";

    public static final String SERVER_PORT = "8080";

    public static final String WEBSOCKET_ADDRESS = "ws://123.206.75.202:8080/WowServer/websocket?userId=";

    public static final String HTTP_LOGIN_ADDRESS = "http://123.206.75.202:8080/WowServer/login.do";

    public static final String REGIST_ADDRESS = "http://123.206.75.202:8080/WowServer/regist.do";

    public static final String HTTP_AVATAR_ADDRESS="http://123.206.75.202:8080/WowServer";

    public static final String HTTP_GET_FRIEND = "http://123.206.75.202:8080/WowServer/friend/query.do";

}
