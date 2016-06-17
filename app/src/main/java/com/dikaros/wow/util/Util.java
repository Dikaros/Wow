package com.dikaros.wow.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;

import com.dikaros.wow.Config;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
     *
     * @param str
     * @return
     */
    public static String getMd5Code(String str) {
        String md5 = null;
        try {
            //使用系统的方法回去MD5码
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
        //打开编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //编辑
        editor.putString(key, token);
        //提交
        editor.commit();

    }

    public static void setPreferenceSet(Context context, String key, Set<String> set) {
        //设置内部存储字段
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.APP_ID,
                Context.MODE_PRIVATE);
        //打开编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //编辑
        editor.putStringSet(key, set);
        //提交
        editor.commit();
    }

    public static Set<String> getPreferenceSet(Context context, String key) {
        //返回id为Config.APP_ID的内部存储字段
        return context
                .getSharedPreferences(Config.APP_ID, Context.MODE_PRIVATE).getStringSet(key, null);
    }


    /**
     * 获取Preference
     *
     * @param context
     * @param key
     * @return
     */
    public static String getPreference(Context context, String key) {
        //返回id为Config.APP_ID的内部存储字段
        return context
                .getSharedPreferences(Config.APP_ID, Context.MODE_PRIVATE)
                .getString(key, null);
    }

    /**
     * 将文字转化为base64格式
     *
     * @param str
     * @return
     */
    public static String toBase64(String str) {
        //读取的str的byte数组
        byte[] b = null;
        //返回结果
        String s = null;
        try {
            //读取
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            try {
                //转换
                s = new String(Base64.encode(b, Base64.DEFAULT), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    /**
     * 将文字转化为base64格式
     *
     * @param str
     * @return
     */
    public static String toBase64(byte[] str) {
        String s = null;
        if (str != null) {
            try {
                s = new String(Base64.encode(str, Base64.DEFAULT), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    /**
     * 将base64转化为文字
     *
     * @param base64
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getFromBase64(String base64) {
        try {
            return new String(Base64.decode(base64.getBytes(), Base64.DEFAULT), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将base64转存为文件
     *
     * @param base64
     * @return
     */
    public static String storeFileFromBase64(Context context, String base64) {
        try {
            String fileName = System.currentTimeMillis()+".amr";
            byte[] bytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
//            File path = context.getDir("/voice", context.MODE_PRIVATE);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/wow/audio";
            File paths = new File(path);
            if (!paths.exists()){
                paths.mkdirs();
            }
            FileOutputStream outputStream = new FileOutputStream(new File(path+"/"+fileName),true);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            Log.e("wow",path+"/"+fileName);
            return path+"/"+fileName;
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("wow",e.getMessage());
        }
        return null;
    }

    /**
     * 获取Bitmap
     *
     * @param base64
     * @return
     */
    public static Bitmap getBitmapFromBase64(String base64) {
        //使用Base64解码获取图片
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /**
     * 根据uri获取系统音频
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getAudioPathFromUri(Context context, Uri uri) {
//        String[] proj = {MediaStore.Audio.Media.DATA};
//        //查询系统数据库
//
//        Cursor actualAudioCursor = activity.getContentResolver().query(uri,proj,null,null,null);
//        //移动到第一个元素
//        actualAudioCursor.moveToFirst();
//        //获得查询项的index
//        int actual_audio_column_index = actualAudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//        //获取地址
//        String audio_path = actualAudioCursor.getString(actual_audio_column_index);
//
//        return audio_path;
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Audio.Media.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Audio.Media.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
