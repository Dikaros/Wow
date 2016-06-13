package com.dikaros.wow.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

import com.dikaros.wow.Config;

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

}
