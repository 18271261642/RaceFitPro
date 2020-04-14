package com.example.bozhilun.android.w30s.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import java.io.IOException;

/**
 * Created by Admin
 * Date 2019/7/8
 */
public class W37CommBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "W37CommBroadCastReceiver";

    private Vibrator mVibrator;

    private MediaPlayer mMediaPlayer;

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"---action-----"+action);
        if(action == null)
            return;
        if(action.equals(W37BleOperateManager.W37_FIND_PHONE_ACTION)){
            findPhone();
        }
    }


    //查找手机
    private void findPhone(){
        try {
            mVibrator = (Vibrator) MyApp.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            mMediaPlayer = new MediaPlayer();
            AssetFileDescriptor file = MyApp.getContext().getResources().openRawResourceFd(R.raw.music);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                        file.getLength());
                mMediaPlayer.prepare();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setVolume(0.5f, 0.5f);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.start();
            if (mVibrator.hasVibrator()) {
                //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                mVibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);//查找手机是调用系统震动
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
