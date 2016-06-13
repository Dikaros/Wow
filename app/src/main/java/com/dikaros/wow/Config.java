package com.dikaros.wow;

/**
 * Created by Dikaros on 2016/6/11.
 */
public class Config {
    public static String APP_ID = "com.dikaros.wow";

    private Config() {
    }

    /**
     * WebSocket的连接地址
     */

    public static String WEBSOCKET_SESSION = null;

    public static boolean inWait = false;

    public static final String HOST = "http://123.206.75.202";

    public static final String SERVER_PORT = "8080";

    public static final String WEBSOCKET_ADDRESS = "ws://"+HOST+":"+SERVER_PORT+"/WowServer/websocket";

    public static final String HTTP_LOGIN_ADDRESS = "http://123.206.75.202:8080/WowServer/login.do";

    public static final String REGIST_ADDRESS = "http://123.206.75.202:8080/WowServer/regist.do";

}
