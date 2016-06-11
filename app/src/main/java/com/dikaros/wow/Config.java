package com.dikaros.wow;

/**
 * Created by Dikaros on 2016/6/11.
 */
public class Config {
    private Config() {
    }

    /**
     * WebSocket的连接地址
     */
    public static final String WEBSOCKET_ADDRESS = "ws://139.196.229.162:9995";


    /**
     * 历史记录的请求地址
     */
    public static final String RECORD_ADDRESS = "http://139.196.229.162/TBB/solar/TBBHistory/getHistoryByDay.json";

}
