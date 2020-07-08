package com.example.bozhilun.android.b15p.activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.L4Command;
import com.tjdL4.tjdmain.contr.TimeUnitSet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimesAndUntiSettingActivity extends WatchBaseActivity {
    private static final String TAG = "TimesAndUntiSettingActi";
    @BindView(R.id.b15p_unti_text)
    TextView b15pUntiText;
    @BindView(R.id.b15p_times_text)
    TextView b15pTimesText;
    ArrayList<String> stringListUnti;
    ArrayList<String> stringListTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_and_unti_activity);
        ButterKnife.bind(this);

        init();

        initDatas();
    }

    private void init() {
        stringListUnti = new ArrayList<>();
        stringListUnti.add(getResources().getString(R.string.setkm));
        stringListUnti.add(getResources().getString(R.string.setmi));

        stringListTime = new ArrayList<>();
        stringListTime.add("12H");
        stringListTime.add("24H");
    }


    private String INLB = "IN LB";
    private String CMKG = "CM KG";
    private int userHeight = 170;
    private int userWeight = 60;
    private double numIN = 0.0328084;//1 cm = 0.0328084 ft
    private double numLB = 2.2046226;//1 kg = 2.2046226 ft

    private void initDatas() {
        if (MyCommandManager.DEVICENAME == null)
            return;
        if (!this.isFinishing()) showLoadingDialog(getResources().getString(R.string.dlog));

        //监听
        L4Command.Brlt_LANGGet(btResultListenr);

        userHeight = Integer.valueOf((String) SharedPreferencesUtils.getParam(TimesAndUntiSettingActivity.this, Commont.USER_HEIGHT, "170"));
        userWeight = Integer.valueOf((String) SharedPreferencesUtils.getParam(TimesAndUntiSettingActivity.this, Commont.USER_WEIGHT, "60"));


    }

    L4M.BTResultListenr btResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
            final String tTypeInfo = TypeInfo;
            final String TempStr = StrData;
            final Object TempObj = DataObj;
            closeLoadingDialog();
            Log.e(TAG, "inTempStr:" + TempStr);
            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tTypeInfo.equals(L4M.SetLANG) && TempStr.equals(L4M.OK)) {
                        L4Command.Brlt_LANGGet(null);
                    } else if (tTypeInfo.equals(L4M.GetLANG) && TempStr.equals(L4M.OK)) {
                        Log.e(TAG,"======获取单位设置");
                        //时间制
                        String sTime = L4M.GetUser_TimeMode();
                        Log.e(TAG, "时间制 " + sTime);
                        if (sTime.equals("1")) {
                            b15pTimesText.setText("12H");
                        } else {
                            b15pTimesText.setText("24H");
                        }

                        //单位制
                        String sUnit = L4M.GetUser_Unit();
                        Log.e(TAG, "单位制 " + sUnit);
                        if (sUnit.equals(CMKG)) {
                            b15pUntiText.setText(getResources().getString(R.string.setkm));
                        } else if (sUnit.equals(INLB)) {
                            b15pUntiText.setText(getResources().getString(R.string.setmi));
                        }
                    }

                }

            });

        }
    };


    @OnClick({R.id.b15p_unti_rel, R.id.b15p_times_rel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.b15p_unti_rel:
                ProfessionPick professionPopWin = new ProfessionPick.Builder(TimesAndUntiSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
//                        heightTv.setText(profession);
//                        mUserInfo.height = profession.trim().substring(0, 3);// 记录一下提交要用
//                        flag = "height";
////                                    heightTv.setText(profession);
////                                    height = profession.substring(0, 3);
//                        String uHeight = profession.substring(0, 3).trim();
//                        modifyPersonData(uHeight);
                        b15pUntiText.setText(profession);
                        Log.e("======", "--公英制--选中的是  " + profession);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle))
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(stringListUnti) //min year in loop
                        .dateChose(stringListUnti.get(0)) // date chose when init popwindow
                        .build();
                professionPopWin.showPopWin(TimesAndUntiSettingActivity.this);
                break;
            case R.id.b15p_times_rel:
                ProfessionPick professionPopWinTime = new ProfessionPick.Builder(TimesAndUntiSettingActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
//                        heightTv.setText(profession);
//                        mUserInfo.height = profession.trim().substring(0, 3);// 记录一下提交要用
//                        flag = "height";
////                                    heightTv.setText(profession);
////                                    height = profession.substring(0, 3);
//                        String uHeight = profession.substring(0, 3).trim();
//                        modifyPersonData(uHeight);
                        b15pTimesText.setText(profession);


                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle))
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(stringListTime) //min year in loop
                        .dateChose(stringListTime.get(0)) // date chose when init popwindow
                        .build();
                professionPopWinTime.showPopWin(TimesAndUntiSettingActivity.this);
                break;
        }
    }


    /**
     * 设置单位或者时间制度
     *
     * @param ss
     * @param hh
     * @param mm
     * @return
     */
    boolean TimeUnitSet(int ss, int hh, int mm) {
        Log.e(TAG, "开始设置单位或者时间制度");
        TimeUnitSet.TimeUnitSetData myTimeUnitSetData = new TimeUnitSet.TimeUnitSetData();
        myTimeUnitSetData.Sett1 = ss;
        myTimeUnitSetData.Sett2 = hh;
        myTimeUnitSetData.Sett3 = mm;
        String ret = L4Command.Brlt_LANGSet(myTimeUnitSetData);
        if (ret.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }


}
