package com.dikaros.wow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.TypedValue;

import com.dikaros.wow.Config;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class Util {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    /**
     * 获取md5
     * @param str
     * @return
     */
    public static String getMd5Code(String str) {
        String md5 = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5 ");
            byte[] buf = digest.digest(str.getBytes());
            BigInteger b = new BigInteger(buf);
            //转换成16进制
            md5 = b.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 设置preference
     *
     * @param context
     * @param key
     * @param token
     */
    public static void setPreference(Context context, String key, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_ID,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, token);
        editor.commit();

    }

    public static void setPreferenceSet(Context context, String key, Set<String> set){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_ID,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key,set);
        editor.commit();
    }

    public static Set<String> getPreferenceSet(Context context, String key){
        return context
                .getSharedPreferences(Config.APP_ID, Context.MODE_PRIVATE).getStringSet(key,null);
    }


    /**
     * 获取Preference
     *
     * @param context
     * @param key
     * @return
     */
    public static String getPreference(Context context, String key) {
        return context
                .getSharedPreferences(Config.APP_ID, Context.MODE_PRIVATE)
                .getString(key, null);
    }

    /**
     * 将文字转化为base64格式
     * @param str
     * @return
     */
    public static String toBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            try {
                s = new String(Base64.encode(b,Base64.DEFAULT),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
    /**
     * 将文字转化为base64格式
     * @param str
     * @return
     */
    public static String toBase64(byte[] str) {
        String s = null;
        if (str != null) {
            try {
                s = new String(Base64.encode(str,Base64.DEFAULT),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return s;
    }
    /**
     * 将base64转化为文字
     * @param base64
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getFromBase64(String base64)  {
        try {
            return new String(Base64.decode(base64.getBytes(),Base64.DEFAULT),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Bitmap
     * @param base64
     * @return
     */
    public static Bitmap getBitmapFromBase64(String base64){
        byte[] bytes= Base64.decode(base64,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
