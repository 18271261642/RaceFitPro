package com.example.bozhilun.android.b30.b30run;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afa.tourism.greendao.gen.B30DevicesSportDao;
import com.afa.tourism.greendao.gen.DaoSession;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30DevicesSport;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ISportModelOriginListener;
import com.veepoo.protocol.listener.data.ISportModelStateListener;
import com.veepoo.protocol.model.datas.SportModelOriginHeadData;
import com.veepoo.protocol.model.datas.SportModelOriginItemData;
import com.veepoo.protocol.model.datas.SportModelStateData;
import com.veepoo.protocol.model.enums.ESportModelStateStauts;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 手环运动
 * Created by Admin
 * Date 2018/12/13
 */
public class ChildDeviceSportFragment extends Fragment {

    private static final String TAG = "ChildDeviceSportFragmen";

    View view;
    @BindView(R.id.list_kcluli)
    ListView listKcluli;
    @BindView(R.id.kaluli_text)
    TextView kcalTotalTv;
    @BindView(R.id.devices_run)
    ImageView deviceStatusImg;
    Unbinder unbinder;

    private DeviceSportAdapter deviceSportAdapter;
    private List<B30DevicesSport> sportList;

    boolean isDeviceSport = false;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);

    private Context mContext;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:      //手环运动数据
                    SportModelOriginHeadData sportData = (SportModelOriginHeadData) msg.obj;
                    if(sportData != null){
                        saveSportDataToDb(sportData);

                    }

                    break;
                case 1002:      //计算总卡路里数据
                    BigDecimal totalBig = (BigDecimal) msg.obj;
                    if(totalBig != null && getActivity() != null && !getActivity().isFinishing()){
                        DecimalFormat decimalFormat = new DecimalFormat("#.00");    //保留两位小数
                        kcalTotalTv.setText(""+decimalFormat.format(totalBig.doubleValue())+(mContext == null ? MyApp.getInstance().getApplicationContext(): mContext.getResources().getString(R.string.km_cal)));
                    }
                    break;
            }


        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "-------onCreate-------");
        sportList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.b30_devices_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();


        readDeviceStatus();
        readSportDataFromDevice();


        return view;
    }

    public Context getmContext() {
        return mContext==null?MyApp.getContext():mContext;
    }

    //读取设备的状态
    private void readDeviceStatus() {
        if (getActivity() == null || getActivity().isFinishing()) {
            Log.e(TAG,"----11------return掉了======");
            return;
        }

        if (MyCommandManager.DEVICENAME == null){
            Log.e(TAG,"----22------return掉了======");
            return;
        }

        //读取设备的状态
        MyApp.getInstance().getVpOperateManager().readSportModelState(iBleWriteResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                if (sportModelStateData.getDeviceStauts() == ESportModelStateStauts.DEVICE_HAD_START_BEFORE) {    //运动模式
                    isDeviceSport = true;
                    if(deviceStatusImg != null)
                        deviceStatusImg.setImageResource(R.mipmap.ic_stop_devices);
                } else { //非运动模式
                    isDeviceSport = false;
                    deviceStatusImg.setImageResource(R.mipmap.ic_play_devices);
                }
            }
        });

    }

    private void initViews() {
        deviceSportAdapter = new DeviceSportAdapter(sportList, getmContext());
        listKcluli.setAdapter(deviceSportAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();


    }



    private void readSportDataFromDevice(){
        if(MyCommandManager.DEVICENAME == null){
            findDeviceSportData();
            return;
        }


        MyApp.getInstance().getVpOperateManager().readSportModelOrigin(iBleWriteResponse, new ISportModelOriginListener() {
            @Override
            public void onReadOriginProgress(float v) {

            }

            @Override
            public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

            }

            @Override
            public void onHeadChangeListListener(SportModelOriginHeadData sportModelOriginHeadData) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = sportModelOriginHeadData;
                handler.sendMessage(message);
            }

            @Override
            public void onItemChangeListListener(List list) {

            }


            @Override
            public void onReadOriginComplete() {

            }
        });
    }


    //查询设备的运动数据
    private void findDeviceSportData() {
        try {
            sportList.clear();
            String currentDate = WatchUtils.getCurrentDate4();
            // String bleMac = (String) SharedPreferenceUtil.get(MyApp.getContext(), Commont.BLEMAC, "");
            String bleMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);
            if (WatchUtils.isEmpty(bleMac)) return;
            if (WatchUtils.isEmpty(userId))
                return;
            DaoSession daoSession = MyApp.getInstance().getDBManager().getDaoSession();
            if (daoSession == null)
                return;
            List<B30DevicesSport> b30DevicesSports = daoSession.getB30DevicesSportDao()
                    .queryBuilder().where(B30DevicesSportDao.Properties.Address.eq(bleMac),B30DevicesSportDao.Properties.Date.eq(currentDate)).list();
            if (b30DevicesSports != null && b30DevicesSports.size() > 0) {
                //计算跑步或骑行的总里程
                BigDecimal bigDecimal = new BigDecimal("0.00");
                for(B30DevicesSport bs : b30DevicesSports){
                    Log.e(TAG,"-------------bs==="+bs.getStopTime()+"----开始时间="+bs.getStartTime());
                    BigDecimal bigD = new BigDecimal(bs.getKcals());
                    bigDecimal = bigD.add(bigDecimal);
                }
                Message msg = handler.obtainMessage();
                msg.what = 1002;
                msg.obj = bigDecimal;
                handler.sendMessage(msg);
                sportList.addAll(b30DevicesSports);
                deviceSportAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "-------onDestroyView-------");
        unbinder.unbind();
    }

    @OnClick(R.id.devices_run)
    public void onClick() {
        if(MyCommandManager.DEVICENAME == null)
            return;
        if(isDeviceSport){
            isDeviceSport = false;
            deviceStatusImg.setImageResource(R.mipmap.ic_play_devices);
            endSportModel();
        }else{
            isDeviceSport = true;
            if(deviceStatusImg != null)
                deviceStatusImg.setImageResource(R.mipmap.ic_stop_devices);
            startSportModel();
        }
    }

    //开启设备运动模式
    private void startSportModel(){
        MyApp.getInstance().getVpOperateManager().startSportModel(iBleWriteResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {

            }
        });
    }

    //结束运动模式
    private void endSportModel(){
        MyApp.getInstance().getVpOperateManager().stopSportModel(iBleWriteResponse, new ISportModelStateListener() {
            @Override
            public void onSportModelStateChange(SportModelStateData sportModelStateData) {
                if(sportModelStateData.getDeviceStauts() == ESportModelStateStauts.DEVICE_FREE){
                    readSportDataFromDevice();
                }
            }
        });
    }


    //保存数据到数据库中
    private void saveSportDataToDb(SportModelOriginHeadData originHeadData){
        if(!String.valueOf(originHeadData.getKcals()).equals("0.0")){
            Log.e(TAG,"-----------保存--------");
            B30DevicesSport db = new B30DevicesSport();
            db.setDate(originHeadData.getDate());
            db.setStartTime(originHeadData.getStartTime().getYear()
                    + "-" + originHeadData.getStartTime().getMonth()
                    + "-" + originHeadData.getStartTime().getDay()
                    + " " + originHeadData.getStartTime().getHour()
                    + ":" + originHeadData.getStartTime().getMinute()
                    + ":" + originHeadData.getStartTime().getSecond());
            db.setStopTime(originHeadData.getStopTime().getDate());
            db.setSportTime(originHeadData.getSportTime());
            db.setStepCount(originHeadData.getStepCount());
            db.setSportCount(originHeadData.getSportCount());
            db.setKcals(originHeadData.getKcals());
            db.setDistance(originHeadData.getDistance());
            db.setRecordCount(originHeadData.getRecordCount());
            db.setPauseCount(originHeadData.getPauseCount());
            db.setPauseTime(originHeadData.getPauseTime());
            db.setCrc(originHeadData.getCrc());
            db.setPeisu(originHeadData.getPeisu());
            db.setOxsporttimes(originHeadData.getOxsporttimes());
            db.setAverRate(originHeadData.getAverRate());
            String bm = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);//设备mac
            db.setAddress(bm);
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);
            db.setUsername(userId);
            List<B30DevicesSport> b30DevicesSports = MyApp.getInstance().getDBManager().getDaoSession().getB30DevicesSportDao().loadAll();
            if (b30DevicesSports == null || b30DevicesSports.size() <= 0) {
                MyApp.getInstance().getDBManager().getDaoSession().getB30DevicesSportDao().insert(db);
            } else {
                for (int i = 0; i < b30DevicesSports.size(); i++) {

                    B30DevicesSport unique = MyApp.getInstance().getDBManager().getDaoSession().getB30DevicesSportDao().queryBuilder().where(B30DevicesSportDao.Properties.StartTime.eq(originHeadData.getStartTime().getYear()
                            + "-" + originHeadData.getStartTime().getMonth()
                            + "-" + originHeadData.getStartTime().getDay()
                            + " " + originHeadData.getStartTime().getHour()
                            + ":" + originHeadData.getStartTime().getMinute()
                            + ":" + originHeadData.getStartTime().getSecond())).unique();
                    if (unique == null) {
                        MyApp.getInstance().getDBManager().getDaoSession().getB30DevicesSportDao().insert(db);
                    }

                }
            }
            findDeviceSportData();
        }

    }


}
