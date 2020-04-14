package com.suchengkeji.android.w30sblelibrary;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2018/5/28.
 */

/**
 * 监听蓝牙的开关状态
 */
public class BlueStateReceiver extends BroadcastReceiver {

    private static final String TAG = "BlueStateReceiver";

    private OnBleStateListener onBleStateListener;

    public void setOnBleStateListener(OnBleStateListener onBleStateListener) {
        this.onBleStateListener = onBleStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"------action="+action);
        if(action != null){
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                Log.e(TAG,"-----bleState--="+bleState);
                switch (bleState){
                    case BluetoothAdapter.STATE_TURNING_ON: //蓝牙打开 11
                        //MyApp.getInstance().openW30SBLEServices();
                        if(onBleStateListener != null){
                            onBleStateListener.theBleStateData(0);
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:    //蓝牙关闭 13
                        if(onBleStateListener != null){
                            onBleStateListener.theBleStateData(1);
                        }
                        break;
                    case BluetoothAdapter.STATE_ON: //蓝牙打开状态 12

                        break;
                    case BluetoothAdapter.STATE_OFF:    //蓝牙关闭 10

                        break;
                }
            }
        }
    }

    public interface OnBleStateListener{
        void theBleStateData(int stateCode);
    }
}
