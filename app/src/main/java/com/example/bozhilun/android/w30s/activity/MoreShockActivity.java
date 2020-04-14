package com.example.bozhilun.android.w30s.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.utils.W30BasicUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/13 09:11
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class MoreShockActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.sw_sedentary_remind)
    ToggleButton swSedentaryRemind;
    @BindView(R.id.sw_dring_remind)
    ToggleButton swDringRemind;
    @BindView(R.id.sw_medicine_remind)
    ToggleButton swMedicineRemind;
    @BindView(R.id.sw_metting_remind)
    ToggleButton swMettingRemind;
    @BindView(R.id.bar_titles)
    TextView barTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_shock);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSharedP();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListenter();
    }

    private void getSharedP() {
        boolean w30sSedentaryRemind = (boolean) SharedPreferencesUtils.getParam(MoreShockActivity.this, "w30sSedentaryRemind", false);
        boolean w30sDringRemind = (boolean) SharedPreferencesUtils.getParam(MoreShockActivity.this, "w30sDringRemind", false);
        boolean w30sMedicineRemind = (boolean) SharedPreferencesUtils.getParam(MoreShockActivity.this, "w30sMedicineRemind", false);
        boolean w30sMedicineRemind1 = (boolean) SharedPreferencesUtils.getParam(MoreShockActivity.this, "w30sMettingRemind", false);
        swSedentaryRemind.setChecked(w30sSedentaryRemind);
        swDringRemind.setChecked(w30sDringRemind);
        swMedicineRemind.setChecked(w30sMedicineRemind);
        swMettingRemind.setChecked(w30sMedicineRemind1);
    }

    private void initListenter() {
        barTitles.setText(getResources().getString(R.string.string_equipment_reminding));
        swSedentaryRemind.setOnCheckedChangeListener(this);
        swDringRemind.setOnCheckedChangeListener(this);
        swMedicineRemind.setOnCheckedChangeListener(this);
        swMettingRemind.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("------------", requestCode + "===" + resultCode);
            if (requestCode == 1) {
                switch (resultCode) {
                    case 1:
                        String mShockTimeS = data.getStringExtra("ShockTime");
                        String mStartShockTimeS = data.getStringExtra("StartShockTime");
                        String mStopShockTimeS = data.getStringExtra("StopShockTime");
                        Log.d("====1====", mShockTimeS + "===" + mStartShockTimeS + "--" + mStopShockTimeS);

                        String substringS = mShockTimeS.substring(0, mShockTimeS.length() - 1);
                        String[] startimeS = mStartShockTimeS.split("[:]");
                        String[] endtimeS = mStopShockTimeS.split("[:]");


                        MyApp.getInstance().getmW30SBLEManage().setSitNotification(Integer.valueOf(startimeS[0]),
                                Integer.valueOf(startimeS[1]), Integer.valueOf(endtimeS[0]),
                                Integer.valueOf(endtimeS[1]), Integer.valueOf(substringS), true);
                        //久坐
                        SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sSedentaryRemind", true);
                        swSedentaryRemind.setChecked(true);
                        swSedentaryRemind.postInvalidate();
                        break;
                    case 2:
                        String mShockTimeD = data.getStringExtra("ShockTime");
                        String mStartShockTimeD = data.getStringExtra("StartShockTime");
                        String mStopShockTimeD = data.getStringExtra("StopShockTime");
                        Log.d("====2====", mShockTimeD + "===" + mStartShockTimeD + "--" + mStopShockTimeD);
                        String substringD = mShockTimeD.substring(0, mShockTimeD.length() - 1);
                        String[] startimeD = mStartShockTimeD.split("[:]");
                        String[] endtimeD = mStopShockTimeD.split("[:]");

                        MyApp.getInstance().getmW30SBLEManage().setDrinkingNotification(Integer.valueOf(startimeD[0]),
                                Integer.valueOf(startimeD[1]), Integer.valueOf(endtimeD[0]),
                                Integer.valueOf(endtimeD[1]), Integer.valueOf(substringD), true);

                        //喝水
                        SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sDringRemind", true);
                        swDringRemind.setChecked(true);
                        swDringRemind.postInvalidate();
                        break;
                    case 3:
                        String mShockTimeM = data.getStringExtra("ShockTime");
                        String mStartShockTimeM = data.getStringExtra("StartShockTime");
                        String mStopShockTimeM = data.getStringExtra("StopShockTime");
                        Log.d("====3====", mShockTimeM + "===" + mStartShockTimeM + "--" + mStopShockTimeM);
                        String substringM = mShockTimeM.substring(0, mShockTimeM.length() - 1);
                        String[] startimeM = mStartShockTimeM.split("[:]");
                        String[] endtimeM = mStopShockTimeM.split("[:]");

                        MyApp.getInstance().getmW30SBLEManage().setMedicalNotification(Integer.valueOf(startimeM[0]),
                                Integer.valueOf(startimeM[1]), Integer.valueOf(endtimeM[0]),
                                Integer.valueOf(endtimeM[1]),
                                Integer.valueOf(substringM), true);
                        //吃药
                        SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sMedicineRemind", true);
                        swMedicineRemind.setChecked(true);
                        swMedicineRemind.postInvalidate();
                        break;
                    case 4:
                        String mDataShockTime = data.getStringExtra("DataShockTime");
                        String mTimeShockTime = data.getStringExtra("TimeShockTime");
                        Log.d("====4====", mDataShockTime + "===" + mTimeShockTime);
                        String[] year = mDataShockTime.split("[-]");
                        String[] time = mTimeShockTime.split("[:]");
                        int yearM = (int) Integer.valueOf(year[0].substring(2, 4));
                        int motohM = (int) Integer.valueOf(year[1]);
                        int dayM = (int) Integer.valueOf(year[2]);
                        int hourM = (int) Integer.valueOf(time[0]);
                        int minM = (int) Integer.valueOf(time[1]);
                        Log.d("=====会议设置时间=返回==", yearM + "-" + motohM + "-" + dayM + " " + hourM + ":" + minM);
                        MyApp.getInstance().getmW30SBLEManage().setMeetingNotification(yearM,
                                motohM, dayM, hourM, minM, true);
                        //会议
                        SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sMettingRemind", true);
                        swMettingRemind.setChecked(true);
                        swMettingRemind.postInvalidate();
                        break;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    @OnClick({R.id.image_back, R.id.line_sedentary_remind, R.id.line_dring_remind, R.id.line_medicine_remind, R.id.line_metting_remind})
    public void onViewClicked(View view) {
        Intent intent = new Intent(MoreShockActivity.this, ShockDetailedActivity.class);
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.line_sedentary_remind:
                intent.putExtra("type", "sedentary");
                startActivityForResult(intent, 1);
                break;
            case R.id.line_dring_remind:
                intent.putExtra("type", "dring");
                startActivityForResult(intent, 1);
                break;
            case R.id.line_medicine_remind:
                intent.putExtra("type", "medicine");
                startActivityForResult(intent, 1);
                break;
            case R.id.line_metting_remind:
                intent.putExtra("type", "metting");
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_sedentary_remind:
                /**
                 * 久坐提醒
                 *
                 * @param startHour 开始-小时
                 * @param startMin  开始-分钟
                 * @param endHour   结束-小时
                 * @param endMin    结束-分钟
                 * @param period    间隔时间小时 = （固定写入1,2,3,4）
                 * @param enable    开关
                 * @return
                 */

                String intervalTime_sedentary = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "IntervalTime_sedentary", "1H");
                String starTime_sedentary = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "starTime_sedentary", "06:00");
                String endTime_sedentary = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "endTime_sedentary", "22:00");
                String substring2 = intervalTime_sedentary.substring(0, intervalTime_sedentary.length() - 1);
                String[] splitSs = starTime_sedentary.split("[:]");
                String[] splitEs = endTime_sedentary.split("[:]");
                SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sSedentaryRemind", isChecked);

                Log.e("=========", "久坐提醒设置  -- " +
                        "\n开始时间： " + splitSs[0] + "时" + splitSs[1] + "分 -- " + splitEs[0] + "时" + splitEs[1] + "分 -- 间隔：" + substring2 + "时  " + (isChecked ? "打开" : "关闭"));
//                MyApp.getInstance().getmW30SBLEManage().setSitNotification(Integer.valueOf(splitSs[0]), Integer.valueOf(splitSs[1]),
//                        Integer.valueOf(splitEs[0]), Integer.valueOf(splitEs[1]),
//                        Integer.valueOf(substring2), isChecked);
                W37DataAnalysis.getW37DataAnalysis().setLongDownSitNotiMsg(Integer.valueOf(splitSs[0]), Integer.valueOf(splitSs[1]),
                        Integer.valueOf(splitEs[0]), Integer.valueOf(splitEs[1]),
                        Integer.valueOf(substring2), isChecked);
                break;
            case R.id.sw_dring_remind:
                /**
                 * 喝水提醒
                 *
                 * @param startHour 开始-小时
                 * @param startMin  开始-分钟
                 * @param endHour   结束-小时
                 * @param endMin    结束-分钟
                 * @param period    间隔时间小时 = （固定写入1,2,3,4）
                 * @param enable    开关
                 * @return
                 */
                String intervalTime_dring = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "IntervalTime_dring", "1H");
                String starTime_dring = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "starTime_dring", "06:00");
                String endTime_dring = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "endTime_dring", "22:00");

                String substring1 = intervalTime_dring.substring(0, intervalTime_dring.length() - 1);
                String[] splitSd = starTime_dring.split("[:]");
                String[] splitEd = endTime_dring.split("[:]");
                SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sDringRemind", isChecked);

                Log.e("=========", "喝水提醒设置  -- " +
                        "\n开始时间： " + splitSd[0] + "时" + splitSd[1] + "分 -- " + splitEd[0] + "时" + splitEd[1] + "分 -- 间隔：" + substring1 + "时  " + (isChecked ? "打开" : "关闭"));
//                MyApp.getInstance().getmW30SBLEManage().setDrinkingNotification(Integer.valueOf(splitSd[0]), Integer.valueOf(splitSd[1]),
//                        Integer.valueOf(splitEd[0]), Integer.valueOf(splitEd[1]), Integer.valueOf(substring1), isChecked);

                W37DataAnalysis.getW37DataAnalysis().setDringNotiMsg(Integer.valueOf(splitSd[0]), Integer.valueOf(splitSd[1]),
                        Integer.valueOf(splitEd[0]), Integer.valueOf(splitEd[1]), Integer.valueOf(substring1), isChecked);
                break;
            case R.id.sw_medicine_remind:
                /**
                 * 吃药提醒
                 *
                 * @param startHour 开始-小时
                 * @param startMin  开始-分钟
                 * @param endHour   结束-小时
                 * @param endMin    结束-分钟
                 * @param period    间隔时间小时  = （固定写入4,6,8,12）
                 * @param enable    开关
                 * @return
                 */
                String intervalTime_medicine = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "IntervalTime_medicine", "4H");
                String starTime_medicine = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "starTime_medicine", "06:00");
                String endTime_medicine = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "endTime_medicine", "22:00");

                String substring = intervalTime_medicine.substring(0, intervalTime_medicine.length() - 1);
                String[] splitSm = starTime_medicine.split("[:]");
                String[] splitEm = endTime_medicine.split("[:]");
                SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sMedicineRemind", isChecked);

                Log.e("=========", "吃药提醒设置  -- " +
                        "\n开始时间： " + splitSm[0] + "时" + splitSm[1] + "分 -- " + splitEm[0] + "时" + splitEm[1] + "分 -- 间隔：" + substring + "时  " + (isChecked ? "打开" : "关闭"));
//                MyApp.getInstance().getmW30SBLEManage().setMedicalNotification(Integer.valueOf(splitSm[0]), Integer.valueOf(splitSm[1]),
//                        Integer.valueOf(splitEm[0]), Integer.valueOf(splitEm[1]), Integer.valueOf(substring), isChecked);


                W37DataAnalysis.getW37DataAnalysis().setMealcalNotiMsg(Integer.valueOf(splitSm[0]), Integer.valueOf(splitSm[1]),
                        Integer.valueOf(splitEm[0]), Integer.valueOf(splitEm[1]), Integer.valueOf(substring), isChecked);

                break;
            case R.id.sw_metting_remind:
                /**
                 * 会议设置
                 *
                 * @param year   年
                 * @param month  月
                 * @param day    日
                 * @param hour   小时
                 * @param min    分钟
                 * @param enable 开关
                 * @return
                 */
                String dateStr2 = W30BasicUtils.getDateStr2(W30BasicUtils.getCurrentDate2(), 1);
                String datas = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "IntervalTime_metting_data", dateStr2);
                String times = (String) SharedPreferencesUtils.getParam(MoreShockActivity.this, "IntervalTime_metting_time", W30BasicUtils.getCurrentDate3().substring(0, W30BasicUtils.getCurrentDate3().length() - 3));

                String[] year = datas.split("[-]");
                String[] time = times.split("[:]");
//                Log.d("-会议设置-year--", year[0].substring(2, 4) + "=- 年--=" + year[0] + "==月==" + year[1] + "==日==" + year[2]);
//                Log.d("-会议设置-time--", time[0] + "=" + time[1]);
                int yearM = (int) Integer.valueOf(year[0].substring(2, 4));
                int motohM = (int) Integer.valueOf(year[1]);
                int dayM = (int) Integer.valueOf(year[2]);
                int hourM = (int) Integer.valueOf(time[0]);
                int minM = (int) Integer.valueOf(time[1]);
                Log.e("=========", "会议提醒设置  -- " +
                        "\n开始时间： " + yearM + "年" + motohM + "月" + dayM + "日 " + hourM + "时" + minM+"分   " + (isChecked ? "打开" : "关闭"));
                SharedPreferencesUtils.setParam(MoreShockActivity.this, "w30sMettingRemind", isChecked);
               // MyApp.getInstance().getmW30SBLEManage().setMeetingNotification(yearM, motohM, dayM, hourM, minM, isChecked);

                W37DataAnalysis.getW37DataAnalysis().setMeetingNotiMsg(yearM, motohM, dayM, hourM, minM, isChecked);
                break;
        }
    }
}
