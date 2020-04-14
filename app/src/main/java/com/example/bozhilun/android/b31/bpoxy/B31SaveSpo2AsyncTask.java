package com.example.bozhilun.android.b31.bpoxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b31.model.B31Spo2hBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存Spo2数据
 * Created by Admin
 * Date 2019/9/24
 */
public class B31SaveSpo2AsyncTask extends AsyncTask<List<Spo2hOriginData>,Void,Void>{

    private static final String TAG = "B31SaveSpo2AsyncTask";


    private Gson gson = new Gson();



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MyApp.getContext().registerReceiver(broadcastReceiver,new IntentFilter(WatchUtils.B31_SPO2_COMPLETE));
    }


    @Override
    protected Void doInBackground(List<Spo2hOriginData>... lists) {
        List<Spo2hOriginData> spo2hOriginDataList = lists[0];
        if(spo2hOriginDataList != null){
            saveSpo2ToDbServer(spo2hOriginDataList);
        }

        return null;
    }




    private void saveSpo2ToDbServer(List<Spo2hOriginData> spo2hOriginDataList) {
        List<B31Spo2hBean> lt = new ArrayList<>();
        String bleMac = MyApp.getInstance().getMacAddress();
        for(Spo2hOriginData spo2hOriginData : spo2hOriginDataList){
            int spo2TimeBefore = spo2hOriginData.getmTime().getHMValue();
            if(spo2TimeBefore <= 420){
                B31Spo2hBean b31Spo2hBean = new B31Spo2hBean();
                b31Spo2hBean.setBleMac(bleMac);
                b31Spo2hBean.setDateStr(spo2hOriginData.getDate());
                b31Spo2hBean.setSpo2hOriginData(gson.toJson(spo2hOriginData));
                //Log.e(TAG,"-----spo2="+b31Spo2hBean.toString());
                lt.add(b31Spo2hBean);
            }

        }

        int delSpo2 = LitePal.deleteAll(B31Spo2hBean.class, "dateStr=? and bleMac=?", WatchUtils.getCurrentDate()
                , bleMac);
        Log.e(TAG,"--------删除spo2="+delSpo2);

        LitePal.saveAll(lt);


        Intent intent = new Intent();
        intent.setAction(WatchUtils.B31_SPO2_COMPLETE);
        MyApp.getContext().sendBroadcast(intent);
    }



    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(broadcastReceiver != null)
            MyApp.getContext().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
