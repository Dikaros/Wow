package com.dikaros.wow.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dikaros.wow.util.AlertUtil;

import java.io.IOException;

public class AudioService extends Service {

    MediaPlayer mediaPlayer;
    public AudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mediaPlayer = new MediaPlayer();
        AlertUtil.toastMess(this,"服务启动");
        return new AudioBinder();
    }

    public class AudioBinder extends Binder{
        public void play(String path) throws IOException {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            Log.e("wow",path);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);

            mediaPlayer.prepare();
            mediaPlayer.start();

        }
    }
}
