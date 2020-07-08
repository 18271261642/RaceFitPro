package com.example.bozhilun.android.mvp.fragment;

import com.example.bozhilun.android.b30.model.CusVPHalfHourBpData;
import com.example.bozhilun.android.b30.model.CusVPHalfRateData;
import com.example.bozhilun.android.b30.model.CusVPHalfSportData;
import com.example.bozhilun.android.b30.model.CusVPSleepData;
import com.example.bozhilun.android.mvp.BaseMvpPresent;
import java.util.List;

/**
 * Created by Admin
 * Date 2020/5/22
 */
public class IRecordPresent extends BaseMvpPresent<IB31RecordView> {

    private IB31RecordModel ib31RecordModel;

    public IRecordPresent() {
        ib31RecordModel = new IRecordImpl();
    }


    //获取详细步数
    public void getDetailStepDay(){
        if(view == null)
            return;
        if(view.recordMac() == null)
            return;
        ib31RecordModel.getStepDetailData(view.recordMac(), view.recordDay(), new DbDataListener<List<CusVPHalfSportData>>() {
            @Override
            public void commDbData(List<CusVPHalfSportData> cusVPHalfSportData) {
                view.stepDetailData(cusVPHalfSportData);
            }
        });
    }


    //获取心率详细信息
    public void getHeartDetailDay(){
        if(view == null)
            return;
        if(view.recordMac() == null)
            return;
        ib31RecordModel.getHeartDetailData(view.recordMac(), view.recordDay(), new DbDataListener<List<CusVPHalfRateData>>() {
            @Override
            public void commDbData(List<CusVPHalfRateData> cusVPHalfRateData) {
                view.heartDetailData(cusVPHalfRateData);
            }
        });
    }


    //获取血压详细信息
    public void getBloodDetailDay(){
        if(view == null)
            return;
        if(view.recordMac() == null)
            return;
        ib31RecordModel.getBloodDetailData(view.recordMac(), view.recordDay(), new DbDataListener<List<CusVPHalfHourBpData>>() {
            @Override
            public void commDbData(List<CusVPHalfHourBpData> cusVPHalfHourBpData) {
                view.bloodDetailData(cusVPHalfHourBpData);
            }
        });

    }

    //睡眠信息
    public void getSleepDetailDay(){
        if(view == null)
            return;
        if(view.recordMac() == null)
            return;
        ib31RecordModel.getSleepDetailData(view.versionCode(), view.recordMac(), view.recordDay(), new DbDataListener<CusVPSleepData>() {
            @Override
            public void commDbData(CusVPSleepData cusVPSleepData) {
                view.sleepDetailSleep(cusVPSleepData);
            }
        });
    }
}
