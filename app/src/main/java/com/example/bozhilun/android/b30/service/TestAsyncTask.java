package com.example.bozhilun.android.b30.service;

import android.os.AsyncTask;
import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import java.util.List;

/**
 * Created by Admin
 * Date 2019/10/10
 */
public class TestAsyncTask extends AsyncTask<Integer,Void,Void> {

    private static final String TAG = "TestAsyncTask";

    private int tagCode = 0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        tagCode = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),"tagCode",0);
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        if(isCancelled())
            return null;




        return null;

     }


    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
