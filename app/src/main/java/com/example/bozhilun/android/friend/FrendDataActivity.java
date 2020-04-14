package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.CommBean;
import com.example.bozhilun.android.friend.bean.FriendDetailHomeBean;
import com.example.bozhilun.android.friend.bean.MenstrualCycle;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看好友数据面板页面
 */
public class FrendDataActivity extends WatchBaseActivity implements RequestView {

    private static final String TAG = "FrendDataActivity";

    @BindView(R.id.frend_step_number)
    TextView frendStepNumber;
    @BindView(R.id.frend_step_dis)
    TextView frendStepDis;
    @BindView(R.id.frend_step_kcl)
    TextView frendStepKcl;
    @BindView(R.id.frend_slee_deep)
    TextView frendSleeDeep;
    @BindView(R.id.frend_sleep_shallow)
    TextView frendSleepShallow;
    @BindView(R.id.frend_sleep_time)
    TextView frendSleepTime;
    @BindView(R.id.frend_hrart_max)
    TextView frendHrartMax;
    @BindView(R.id.frend_heart_min)
    TextView frendHeartMin;
    @BindView(R.id.frend_hreat_average)
    TextView frendHreatAverage;


    @BindView(R.id.rela_bp)
    RelativeLayout rela_bp;
    @BindView(R.id.frend_bp_max)
    TextView frendBpMax;
    @BindView(R.id.frend_bp_min)
    TextView frendBpMin;
    @BindView(R.id.frend_bp_average)
    TextView frendBpAverage;

    //血氧的最高值
    @BindView(R.id.spo2MaxTv)
    TextView spo2MaxTv;
    //血氧的最低值
    @BindView(R.id.spo2MinTv)
    TextView spo2MinTv;
    //血氧的平均值
    @BindView(R.id.spo2AvgTv)
    TextView spo2AvgTv;


    //HRV心脏健康指数
    @BindView(R.id.friendHomeHrvSourceTv)
    TextView friendHomeHrvSourceTv;


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    //血氧的布局，普通好友无权查看
    @BindView(R.id.friendSpo2Lin)
    RelativeLayout friendSpo2Lin;
    //HRV的布局，普通好友无权查看
    @BindView(R.id.friendHrvLin)
    RelativeLayout friendHrvLin;

    //女性生理期的布局
    @BindView(R.id.friendWomenLin)
    RelativeLayout friendWomenLin;


    @BindView(R.id.friendHomeWomenStatusTv)
    TextView friendHomeWomenStatusTv;

    private RequestPressent requestPressent;
    String applicant = null;
    String StepNumber = "0";
    private int FrendSeeToMeStep = 0;
    private int FrendSeeToMeHeart = 0;
    private int FrendSeeToMeSleep = 0;
    private int FrendSeeToMeBlood = 0;
    Intent intent = null;
    String stringJson = "";
    //好友的设备地址
    private String friendBleMac = null;


    private BottomSheetDialog bottomSheetDialog;
    private AlertDialog.Builder descAlert;

    //女性生理期数据
    private String womenStr = null;


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_data_activity);
        ButterKnife.bind(this);

        initViews();

        init();

        intent = getIntent();
        if (intent == null) return;
        applicant = intent.getStringExtra("applicant");
        StepNumber = intent.getStringExtra("stepNumber");//步数
        Log.e(TAG, "------好友ID=" + applicant);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_frend_datas));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(applicant)) {
            getFrendlatDdayData(applicant);
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        //设置标题
        //barTitles.setText(getResources().getString(R.string.string_frend_datas));


        frendStepNumber.setText(getResources().getString(R.string.step) + "(STEP)：--");
        frendStepDis.setText(getResources().getString(R.string.mileage) + "(KM)：--");
        //卡路里
        frendStepKcl.setText(getResources().getString(R.string.calories) + "(KCAL): --");


        //深睡
        frendSleeDeep.setText(getResources().getString(R.string.sleep_deep) + "(HOUR)：0.0");
        //浅睡
        frendSleepShallow.setText(getResources().getString(R.string.sleep_light) + "(HOUR)：0.0");
        //总睡眠时长
        frendSleepTime.setText(getResources().getString(R.string.long_when) + "(HOUR)：0.0");

        //最高心率
        frendHrartMax.setText(getResources().getString(R.string.zuigaoxinlv) + "(BPM）：0");
        //最低心率
        frendHeartMin.setText(getResources().getString(R.string.zuidixinlv) + "(BPM）：0");
        //平均心率
        frendHreatAverage.setText(getResources().getString(R.string.pinjunxin) + "(BPM）：0");


        frendBpMax.setText(getResources().getString(R.string.string_systolic) + "(mmHg)：--");
        frendBpMin.setText(getResources().getString(R.string.string_diastolic) + "(mmHg)：--");


        spo2MaxTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.max_value) + "：--");
        spo2MinTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.min_value) + "：--");
        spo2AvgTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.ave_value) + "：--");


        friendHomeHrvSourceTv.setText(getResources().getString(R.string.heart_health_sorce) + "：--");

        friendHomeWomenStatusTv.setText(getResources().getString(R.string.b36_period_day)+" : --");

    }


    //好友等级操作
    private void showFriendOperate() {
        bottomSheetDialog = new BottomSheetDialog(FrendDataActivity.this);
        View view = getLayoutInflater().inflate(R.layout.friend_setting_star_layout, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        TextView descTv = view.findViewById(R.id.friendPermissDesc);
        TextView specialTv = view.findViewById(R.id.starSpecialTv);
        TextView starTv = view.findViewById(R.id.starStarTv);
        TextView normalTv = view.findViewById(R.id.starNormalTv);
        TextView cancleTv = view.findViewById(R.id.starCancleTv);
        descTv.setOnClickListener(onClickListener);
        specialTv.setOnClickListener(onClickListener);
        starTv.setOnClickListener(onClickListener);
        cancleTv.setOnClickListener(onClickListener);
        normalTv.setOnClickListener(onClickListener);

    }

    //设置星标，特殊好友
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.friendPermissDesc:    //好友权限说明
                    showFriendPermissDesc();
                    break;
                case R.id.starSpecialTv:    //设置为特殊好友
                    settingFriendLevel(10);
                    break;
                case R.id.starStarTv:   //设置为星标好友
                    settingFriendLevel(5);
                    break;
                case R.id.starNormalTv: //普通好友
                    settingFriendLevel(1);
                    break;
                case R.id.starCancleTv:
                    bottomSheetDialog.dismiss();
                    break;

            }
        }
    };


    private void showFriendPermissDesc(){
        descAlert = new AlertDialog.Builder(FrendDataActivity.this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage("特殊好友:只允许设置一位，拥有查看所有数据的权限，设置特殊好友后需对方同意申请;"+"\n\n"
                        +"星标好友:可以互相查看除生理期外的所有数据，设置星标好友无需好友同意;"+"\n\n"
                        +"普通好友:可以互相查看步数、睡眠、心率、血压数据,申请好友同意后默认为普通好友;"+"\n\n"
                        ).setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        descAlert.create().show();
    }




    //设置好友等级
    private void settingFriendLevel(int level) {
        bottomSheetDialog.dismiss();
        String url = Commont.FRIEND_BASE_URL + (level == 1 ? Commont.SETTING_FRIEND_NORMAL : Commont.SETTING_FRIEND_LEVEL);
        String userId = (String) SharedPreferencesUtils.readObject(FrendDataActivity.this, Commont.USER_ID_DATA);
        if (applicant == null)
            return;
        if (userId == null)
            return;
        if (requestPressent != null) {
            Map<String, Object> maps = new HashMap<>();
            maps.put("userId", userId);
            maps.put("friendId", applicant);
            if (level != 1) {
                maps.put("level", level);
            }
            requestPressent.getRequestJSONObject(0x02, url, FrendDataActivity.this, new Gson().toJson(maps), 1);

        }

    }


    /**
     * 好友首页：昨日的睡眠，心率，步数
     *
     * @param applicant
     */
    public void getFrendlatDdayData(String applicant) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FrendLastData;
        JSONObject sleepJson = new JSONObject();
        try {
            if (WatchUtils.isEmpty(applicant)) applicant = intent.getStringExtra("applicant");
            String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
            if (!TextUtils.isEmpty(userId)) sleepJson.put("userId", userId);
            sleepJson.put("applicant", applicant);
            Log.e("-----------朋友--", " 好友首页：昨日的睡眠，心率，步数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FrendDataActivity.this, sleepJson.toString(), 0);
        }
    }


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object == null || WatchUtils.isEmpty(object + "") || object.toString().contains("<html>"))
            return;
        Log.d("-----------朋友--", object.toString());
        if (what == 0x01) {   //解析好友返回
            analysisFriendDetail(object.toString());
        }
        if (what == 0x02) {   //设置星标好友返回
            CommBean commBean = new Gson().fromJson(object.toString(), CommBean.class);
            ToastUtil.showToast(FrendDataActivity.this, commBean.getCode() == 200 ? commBean.getData() : commBean.getMsg());

        }


    }


    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    //解析数据
    @SuppressLint("SetTextI18n")
    private void analysisFriendDetail(String str) {
        FriendDetailHomeBean friendDetailHomeBean = new Gson().fromJson(str, FriendDetailHomeBean.class);
        if (friendDetailHomeBean == null)
            return;
        if (friendDetailHomeBean.getCode() == 200) {
            //好友等级
            FriendDetailHomeBean.DataBean.FriendInfoBean friendInfoBean = friendDetailHomeBean.getData().getFriendInfo();
            int friendLevel = friendInfoBean.getLevel();
            if (friendLevel == 10) {  //特殊好友
                commentB30ShareImg.setImageResource(R.mipmap.icon_star_special);
                friendSpo2Lin.setEnabled(true);
                friendHrvLin.setEnabled(true);
                friendWomenLin.setEnabled(true);
            } else if (friendLevel == 5) { //星标好友
                commentB30ShareImg.setImageResource(R.mipmap.icon_star_star);
                friendSpo2Lin.setEnabled(true);
                friendHrvLin.setEnabled(true);
                friendWomenLin.setEnabled(false);
            } else {
                friendSpo2Lin.setEnabled(false);
                friendHrvLin.setEnabled(false);
                friendWomenLin.setEnabled(false);
                commentB30ShareImg.setImageResource(R.mipmap.ic_close_frend);
            }


            //步数数据
            FriendDetailHomeBean.DataBean.SportDayBean sportDayBean = friendDetailHomeBean.getData().getSportDay();
            if (sportDayBean != null) {   //有数据
                if (sportDayBean.getDevicecode() != null)
                    friendBleMac = sportDayBean.getDevicecode();
                //步数
                frendStepNumber.setText(getResources().getString(R.string.step) + "(STEP)：" + sportDayBean.getStepnumber());
                //距离
                DecimalFormat decimalFormat = new DecimalFormat("#.00");

                frendStepDis.setText(getResources().getString(R.string.mileage) + "(KM)：" + decimalFormat.format(sportDayBean.getDistance()));
                //卡路里
                frendStepKcl.setText(getResources().getString(R.string.calories) + "(KCAL):" + (sportDayBean.getCalorie() == null ? "0.0" : sportDayBean.getCalorie()) + "");

            }


            //睡眠数据
            FriendDetailHomeBean.DataBean.SleepDayBean sleepDayBean = friendDetailHomeBean.getData().getSleepDay();
            if (sleepDayBean != null) {
                Log.e(TAG, "-------好友睡眠=" + sleepDayBean.toString());
                int deepTimeStr = sleepDayBean.getDeepsleep();
                if (sleepDayBean.getDevicecode() != null)
                    friendBleMac = sleepDayBean.getDevicecode();
                frendSleeDeep.setText(getResources().getString(R.string.sleep_deep) + "(HOUR)：" + deepTimeStr / 60 + "H" + deepTimeStr % 60 + "mine");
                //浅睡
                int lowTimeStr = sleepDayBean.getShallowsleep();
                frendSleepShallow.setText(getResources().getString(R.string.sleep_light) + "(HOUR)：" + lowTimeStr / 60 + "H" + lowTimeStr % 60 + "mine");
                //总睡眠时长
                int countTimeStr = sleepDayBean.getSleeplen();
                frendSleepTime.setText(getResources().getString(R.string.long_when) + "(HOUR)：" + (deepTimeStr + lowTimeStr) / 60 + "H" + (deepTimeStr + lowTimeStr) % 60 + "mine");
            }

            //心率数据
            FriendDetailHomeBean.DataBean.HeartRateDayBean heartRateDayBean = friendDetailHomeBean.getData().getHeartRateDay();
            if (heartRateDayBean != null) {
                if (heartRateDayBean.getDevicecode() != null)
                    friendBleMac = heartRateDayBean.getDevicecode();
                Log.e(TAG, "-------心率数据=" + heartRateDayBean.toString());
                //最高心率
                frendHrartMax.setText(getResources().getString(R.string.zuigaoxinlv) + "(BPM）：" + heartRateDayBean.getMaxheartrate());
                //最低心率
                frendHeartMin.setText(getResources().getString(R.string.zuidixinlv) + "(BPM）：" + heartRateDayBean.getMinheartrate());
                //平均心率
                frendHreatAverage.setText(getResources().getString(R.string.pinjunxin) + "(BPM）：" + heartRateDayBean.getAvgheartrate());


            }


            //血压数据
            FriendDetailHomeBean.DataBean.BloodPressureDayBean bloodPressureDayBean = friendDetailHomeBean.getData().getBloodPressureDay();
            if (bloodPressureDayBean != null) {
                Log.e(TAG, "-------血压数据=" + bloodPressureDayBean.toString());
                if (bloodPressureDayBean.getDevicecode() != null)
                    friendBleMac = bloodPressureDayBean.getDevicecode();
                frendBpMax.setText(getResources().getString(R.string.string_systolic) + "(mmHg)：" + bloodPressureDayBean.getAvgsystolic());
                frendBpMin.setText(getResources().getString(R.string.string_diastolic) + "(mmHg)：" + bloodPressureDayBean.getAvgdiastolic());
                frendBpAverage.setText("参考结果");
            }


            //血氧
            FriendDetailHomeBean.DataBean.BloodOxygenDay bloodOxygenDay = friendDetailHomeBean.getData().getBloodOxygenDay();
            if (bloodOxygenDay != null) {
                if (bloodOxygenDay.getDevicecode() != null)
                    friendBleMac = bloodOxygenDay.getDevicecode();
                spo2MaxTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.max_value) + ": " + (friendLevel == 1 ? "**" : bloodOxygenDay.getMaxbloodoxygen()));
                spo2MinTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.min_value) + ": " + (friendLevel == 1 ? "**" : bloodOxygenDay.getMinbloodoxygen()));
                spo2AvgTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.ave_value) + ": " + (friendLevel == 1 ? "**" : bloodOxygenDay.getAvgbloodoxygen()));


            }

            //HRV
            FriendDetailHomeBean.DataBean.HrvDay hrvDay = friendDetailHomeBean.getData().getHrvDay();
            if (hrvDay != null) {
                friendHomeHrvSourceTv.setText(getResources().getString(R.string.heart_health_sorce) + "：" + hrvDay.getHeartSocre());
            }


            //女性生理期
            MenstrualCycle menstrualCycle = friendDetailHomeBean.getData().getMenstrualCycle();
            if(menstrualCycle != null){
                womenStr = new Gson().toJson(menstrualCycle);
            }

        }


    }


    @OnClick({R.id.rela_step, R.id.rela_sleep,
            R.id.rela_heart, R.id.rela_bp,
            R.id.friendSpo2Lin, R.id.friendHrvLin,
            R.id.commentB30BackImg, R.id.commentB30ShareImg,
            R.id.friendWomenLin})
    public void onViewClicked(View view) {
        if (WatchUtils.isEmpty(applicant)) applicant = intent.getStringExtra("applicant");
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //设置星标
                showFriendOperate();
                break;
            case R.id.rela_step:
                Log.d("-------AA-", "FrendSeeToMeStep:" + FrendSeeToMeStep + "");
                startActivity(FrendStepActivity.class, new String[]{"applicant"},
                        new String[]{applicant});
                break;
            case R.id.rela_sleep:
                Log.d("-------AA-", "FrendSeeToMeSleep:" + FrendSeeToMeSleep + "");
                //if (FrendSeeToMeSleep == 0) return;
                startActivity(NewFriendSleepActivity.class, new String[]{"applicant", "friendBleMac"},
                        new String[]{applicant, friendBleMac});
                break;
            case R.id.rela_heart:
                Log.d("-------AA-", "FrendSeeToMeHeart:" + FrendSeeToMeHeart + "");
                //if (FrendSeeToMeHeart == 0) return;
                startActivity(FrendHeartActivity.class, new String[]{"applicant"},
                        new String[]{applicant});
                break;
            case R.id.rela_bp:
                //if (FrendSeeToMeBlood == 0) return;
                startActivity(NewFriendBpActivity.class, new String[]{"applicant"},
                        new String[]{applicant});
                break;
            case R.id.friendSpo2Lin:    //血氧
                startActivity(FriendSpo2DetailActivity.class, new String[]{"applicant", "friendBleMac"}, new String[]{applicant, friendBleMac});
                break;
            case R.id.friendHrvLin: //HRV
                startActivity(FriendHrvDetailActivity.class, new String[]{"applicant", "friendBleMac"}, new String[]{applicant, friendBleMac});
                break;
            case R.id.friendWomenLin:   //生理期
                startActivity(FriendWomenActivity.class,new String[]{"friend_women"},new String[]{womenStr});
                break;
        }

    }

}
