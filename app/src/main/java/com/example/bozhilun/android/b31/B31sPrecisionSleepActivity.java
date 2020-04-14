package com.example.bozhilun.android.b31;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30CusSleepView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPSleepPrecisionData;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31s精准睡眠详细页面
 * Created by Admin
 * Date 2019/9/6
 */
public class B31sPrecisionSleepActivity extends WatchBaseActivity {

    private static final String TAG = "B31sPrecisionSleepActiv";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b31sDetailCusSleepView)
    B30CusSleepView b31sDetailCusSleepView;
    @BindView(R.id.b31sSleepSeekBar)
    SeekBar b31sSleepSeekBar;

    @BindView(R.id.sleepCurrDateTv)
    TextView sleepCurrDateTv;

    //睡眠开始时间
    @BindView(R.id.b31sStartSleepTimeTv)
    TextView b31sStartSleepTimeTv;
    //睡眠结束时间
    @BindView(R.id.b31sEndSleepTimeTv)
    TextView b31sEndSleepTimeTv;
    //睡眠时间长
    @BindView(R.id.b31sDetailAllSleepTv)
    TextView b31sDetailAllSleepTv;
    //苏醒
    @BindView(R.id.b31sDetailAwakeTimesTv)
    TextView b31sDetailAwakeTimesTv;
    //失眠
    @BindView(R.id.detailInsomniaSleepTv)
    TextView detailInsomniaSleepTv;

    //快速眼动
    @BindView(R.id.detailAwakeHeightTv)
    TextView detailAwakeHeightTv;

    //深睡时长
    @BindView(R.id.b31sDetailDeepTv)
    TextView b31sDetailDeepTv;
    @BindView(R.id.b31sSleepLengthResultTv)
    TextView b31sSleepLengthResultTv;


    //浅睡时长
    @BindView(R.id.b31sDetailHightSleepTv)
    TextView b31sDetailHightSleepTv;

    //苏醒次数
    @BindView(R.id.b31sAwakeNumbersTv)
    TextView b31sAwakeNumbersTv;

    //入睡效率
    @BindView(R.id.b31sSleepInEfficiencyScoreTv)
    TextView b31sSleepInEfficiencyScoreTv;
    //睡眠效率
    @BindView(R.id.b31sSleepEffectivenessTv)
    TextView b31sSleepEffectivenessTv;
    @BindView(R.id.commB31TitleLayout)
    Toolbar commB31TitleLayout;
    //睡眠质量表示星星
    @BindView(R.id.b31sPercisionSleepQualityRatingBar)
    AppCompatRatingBar b31sPercisionSleepQualityRatingBar;
    //苏醒百分比
    @BindView(R.id.b31sAwawkPercentTv)
    TextView b31sAwawkPercentTv;
    //苏醒状态
    @BindView(R.id.b31sSleepAwakeResultTv)
    TextView b31sSleepAwakeResultTv;
    //失眠百分比
    @BindView(R.id.b31sSleepInsomniaPercentTv)
    TextView b31sSleepInsomniaPercentTv;
    //失眠状态
    @BindView(R.id.b31sSleepInsomniaResultTv)
    TextView b31sSleepInsomniaResultTv;
    //快速眼动百分比
    @BindView(R.id.b31sSleepEayPercentTv)
    TextView b31sSleepEayPercentTv;
    //快速演的状态
    @BindView(R.id.b31sSleepEayResultTv)
    TextView b31sSleepEayResultTv;
    //深睡百分比
    @BindView(R.id.b31sSleepDeepPercentTv)
    TextView b31sSleepDeepPercentTv;
    //深睡状态
    @BindView(R.id.b31sSleepDeepResultTv)
    TextView b31sSleepDeepResultTv;
    //浅睡百分比
    @BindView(R.id.b31sSleepLowPercentTv)
    TextView b31sSleepLowPercentTv;
    //浅睡状态
    @BindView(R.id.b31sSleepLowResultTv)
    TextView b31sSleepLowResultTv;

    private List<Integer> listValue;

    String currDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31s_precision_sleep);
        ButterKnife.bind(this);


        initViews();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
        if (WatchUtils.isEmpty(currDay))
            currDay = WatchUtils.getCurrentDate();
        initData(currDay);


    }

    private void initData(String dayStr) {
        sleepCurrDateTv.setText(dayStr);
        listValue.clear();
        try {
            String mac = MyApp.getInstance().getMacAddress();
            if (WatchUtils.isEmpty(mac))
                return;
            String sleepStr = B30HalfHourDao.getInstance().findOriginData(mac, dayStr, B30HalfHourDao.TYPE_PRECISION_SLEEP);
           // Log.e(TAG, "------sleepStr=" + sleepStr);
            CusVPSleepPrecisionData cusVPSleepPrecisionData = new Gson().fromJson(sleepStr, CusVPSleepPrecisionData.class);
             //Log.e(TAG,"-------sl="+cusVPSleepPrecisionData.toString());
            showSleepChartView(cusVPSleepPrecisionData);
            showSleepDetail(cusVPSleepPrecisionData);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void showSleepDetail(CusVPSleepPrecisionData cusVPSleepPrecisionData) {
        //睡眠质量
        int sleepQuality = cusVPSleepPrecisionData == null ? 1 : cusVPSleepPrecisionData.getSleepQulity();
        b31sPercisionSleepQualityRatingBar.setMax(5);
        b31sPercisionSleepQualityRatingBar.setNumStars(sleepQuality);

        //入睡时间
        b31sStartSleepTimeTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getSleepDown().getColck());
        //起床时间
        b31sEndSleepTimeTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getSleepUp().getColck());
        //睡眠时长
        if (cusVPSleepPrecisionData == null) {
            b31sDetailAllSleepTv.setText("0h0m");
            b31sSleepLengthResultTv.setText("--");
        } else {
            int allTime = cusVPSleepPrecisionData.getAllSleepTime();
            b31sDetailAllSleepTv.setText(allTime / 60 + "h" + allTime % 60 + "m");

            //7-9小时之间为正常
            b31sSleepLengthResultTv.setText(allTime <420 ? "偏低" : ( allTime >540 ? "偏高" : "正常"));


        }


        //失眠时长
        detailInsomniaSleepTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getInsomniaLength() / 60 + "h" + cusVPSleepPrecisionData.getInsomniaLength() % 60 + "m");
        //快速眼动时长
        detailAwakeHeightTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getOtherDuration() / 60 + "h" + cusVPSleepPrecisionData.getOtherDuration() % 60 + "m");
        //深度睡眠时长
        b31sDetailDeepTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getDeepSleepTime() / 60 + "h" + cusVPSleepPrecisionData.getDeepSleepTime() % 60 + "m");
        //浅度睡眠时长
        b31sDetailHightSleepTv.setText(cusVPSleepPrecisionData == null ? "0h0m" : cusVPSleepPrecisionData.getLowSleepTime() / 60 + "h" + cusVPSleepPrecisionData.getLowSleepTime() % 60 + "m");
        //苏醒次数
        b31sAwakeNumbersTv.setText(cusVPSleepPrecisionData == null ? "--" : cusVPSleepPrecisionData.getWakeCount() + getResources().getString(R.string.cishu));
        //入睡效率
        b31sSleepInEfficiencyScoreTv.setText(cusVPSleepPrecisionData == null ? "--" : cusVPSleepPrecisionData.getFirstDeepDuration() + getResources().getString(R.string.minute));
        //睡眠效率得分
        b31sSleepEffectivenessTv.setText(cusVPSleepPrecisionData == null ? "--" : cusVPSleepPrecisionData.getSleepEfficiencyScore() + getResources().getString(R.string.signle_minute));
        if(cusVPSleepPrecisionData == null){
            return;
        }
        //总的睡眠时长
        float countSleepTime = cusVPSleepPrecisionData.getAllSleepTime();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");


        //失眠
        float insomniaDurationTime = cusVPSleepPrecisionData.getInsomniaDuration();
        if(insomniaDurationTime == 0){
            b31sSleepInsomniaPercentTv.setText("0%");
            b31sSleepInsomniaResultTv.setText("正常");
        }else{
            float insomniaPercent = insomniaDurationTime / countSleepTime;
            String insomniPercentV = StringUtils.substringAfter(decimalFormat.format(insomniaPercent), ".");
            b31sSleepInsomniaPercentTv.setText(insomniPercentV + "%");
            int tmpInsomniV = Integer.valueOf(insomniPercentV);
            b31sSleepInsomniaResultTv.setText(tmpInsomniV == 0 ? "正常" : "严重");
        }



        //深睡
        float deepTime = cusVPSleepPrecisionData.getDeepSleepTime();
        float deepPercent = deepTime / countSleepTime;
        //Log.e(TAG,"--------深睡="+deepTime+"--="+deepPercent);
        String tmpPer = decimalFormat.format(deepPercent);
        if(tmpPer.equals("0")){
            b31sSleepDeepPercentTv.setText("0%");
            b31sSleepDeepResultTv.setText("偏低");
        }else{
            String deepPercentV = StringUtils.substringAfter(tmpPer,".");
            b31sSleepDeepPercentTv.setText(deepPercentV+"%");
            int tmpDeepV = Integer.valueOf(deepPercentV);
            b31sSleepDeepResultTv.setText(tmpDeepV>=21?"正常":"偏低");
        }




        //浅睡
        float lowTime = cusVPSleepPrecisionData.getLowSleepTime();
        float lowPercent = lowTime / countSleepTime;
        String lowPercentV = StringUtils.substringAfter(decimalFormat.format(lowPercent),".");
        b31sSleepLowPercentTv.setText(lowPercentV+"%");
        int tmpLowV = Integer.valueOf(lowPercentV);
        b31sSleepLowResultTv.setText((0<=tmpLowV && tmpLowV<=59)?"正常":"偏低");



        //快速眼动
        float otherTime = cusVPSleepPrecisionData.getOtherDuration();
        float otherPercent = otherTime / countSleepTime;
        float formV = Float.valueOf(decimalFormat.format(otherPercent));
        formV = formV * 100;
        String otherPercentV = StringUtils.substringBefore(decimalFormat.format(formV),".");
        b31sSleepEayPercentTv.setText(otherPercentV +"%");
        int tmpOhterV = Integer.valueOf(otherPercentV);
        b31sSleepEayResultTv.setText((tmpOhterV >=10 && tmpOhterV <=30)?"正常":"偏低");


        //苏醒百分比，
        //苏醒时长=总时长-深睡-浅睡-快速眼动
        float awakeTime = countSleepTime - deepTime - lowTime - otherTime;
        if(awakeTime == 0){
            b31sAwawkPercentTv.setText("0%");
            b31sSleepAwakeResultTv.setText( "正常");
            b31sDetailAwakeTimesTv.setText( "0h0m");
        }else{
            float awakePercent = awakeTime / countSleepTime;
            String formStr = StringUtils.substringAfter(decimalFormat.format(awakePercent),".");
            b31sAwawkPercentTv.setText(Integer.valueOf(formStr.trim())+"%");
            int tmpAwakeV = Integer.valueOf(formStr);
            b31sSleepAwakeResultTv.setText(tmpAwakeV <= 1 ? "正常" : "严重");
            //苏醒时长
            b31sDetailAwakeTimesTv.setText(((int)awakeTime) / 60 + "h" + ((int)awakeTime) % 60 + "m");
        }



    }

    //展示睡眠图表
    private void showSleepChartView(final CusVPSleepPrecisionData cusVPSleepPrecisionData) {
        if (cusVPSleepPrecisionData == null) {
            listValue.clear();
            b31sDetailCusSleepView.setPrecisionSleep(true);
            b31sDetailCusSleepView.setSleepList(listValue);
            return;
        }
        String sleepLin = cusVPSleepPrecisionData.getSleepLine();
        Log.e(TAG, "------精准睡眠字符串=" + sleepLin + "\n" + sleepLin.length());
        for (int i = 0; i < sleepLin.length(); i++) {
            if (i <= sleepLin.length() - 1) {
                int subStr = Integer.valueOf(sleepLin.substring(i, i + 1));
                listValue.add(subStr);
            }
        }

        Log.e(TAG, "---------size=" + listValue.size());
        b31sDetailCusSleepView.setPrecisionSleep(true);
        b31sDetailCusSleepView.setSleepList(listValue);

        b31sDetailCusSleepView.setPrecisionSleepLin(false);
        b31sSleepSeekBar.setMax(listValue.size());
        b31sSleepSeekBar.setProgress(-1);
        //开始时间
        final int startTime = cusVPSleepPrecisionData.getSleepDown().getHMValue();
        //Log.e(TAG, "-------开始时间=" + startTime + "---起床时间=" + cusVPSleepPrecisionData.getSleepUp().getHMValue());
        b31sSleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == listValue.size())
                    return;
                //Log.e(TAG,"-------progress="+progress);

                int seekTime = startTime + progress;
                //Log.e(TAG,"-----seekTime="+seekTime);

                int resultHour = seekTime / 60;
                int resultMine = seekTime % 60;

                //精准睡眠，睡眠曲线是一组由0,1,2，3,4组成的字符串，每一个字符代表时长为1分钟，
                // 其中0表示深睡，1表示浅睡，2表示快速眼动,3表示失眠,4表示苏醒。
                int sleepType = listValue.get(progress);
                String sleetTypeStr = null;
                switch (sleepType) {
                    case 0:     //深睡
                        sleetTypeStr = "深睡";
                        break;
                    case 1:     //浅睡
                        sleetTypeStr = "浅睡";
                        break;
                    case 2:     //快速眼动
                        sleetTypeStr = "快速眼动";
                        break;
                    case 3:     //失眠
                        sleetTypeStr = "失眠";
                        break;
                    case 4:     //苏醒
                        sleetTypeStr = "苏醒";
                        break;
                }
                String hourS = resultHour < 10 ? ("0" + resultHour) :resultHour>=24 ? ("0"+(resultHour-24)) : resultHour+"";

                b31sDetailCusSleepView.setSleepDateTxt(sleetTypeStr + (hourS + ":" + (resultMine < 10 ? ("0" + resultMine) : resultMine)));
                b31sDetailCusSleepView.setPreicisionLinSchdue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                b31sDetailCusSleepView.setPrecisionSleepLin(true, 0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
        commB31TitleLayout.setBackgroundColor(Color.parseColor("#20806F"));
        listValue = new ArrayList<>();
    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;
        }
        currDay = date;
        initData(currDay);
    }

    @OnClick({R.id.sleepLengthConLin, R.id.sleepAwakeConLin,
            R.id.sleepInsomniaConLin, R.id.sleepEayConLin,
            R.id.sleepDeepConLin, R.id.sleepLowConLin,
            R.id.commentB30BackImg,
            R.id.sleepCurrDateLeft, R.id.sleepCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.sleepCurrDateRight:   //后一天
                changeDayData(false);
                break;
            case R.id.sleepCurrDateLeft:    //前一天
                changeDayData(true);
                break;
            case R.id.sleepLengthConLin:    //睡眠时长
                String sleepLengthV = b31sDetailAllSleepTv.getText().toString().trim();
                String sleepLengthStatus = b31sSleepLengthResultTv.getText().toString();
                verticalSleepType("睡眠时长",sleepLengthV,sleepLengthStatus,1);
                break;
            case R.id.sleepAwakeConLin:     //苏醒
                String sleepAwakeConLinV = b31sAwawkPercentTv.getText().toString();
                String b31sSleepAwakeResultTvV = b31sSleepAwakeResultTv.getText().toString();
                verticalSleepType("苏醒",sleepAwakeConLinV,b31sSleepAwakeResultTvV,2);
                break;
            case R.id.sleepInsomniaConLin:  //失眠
                String b31sSleepInsomniaPercentTvV = b31sSleepInsomniaPercentTv.getText().toString();
                String b31sSleepInsomniaStatus = b31sSleepInsomniaResultTv.getText().toString().trim();
                verticalSleepType("失眠",b31sSleepInsomniaPercentTvV,b31sSleepInsomniaStatus,3);
                break;
            case R.id.sleepEayConLin:       //快速眼动
                String b31sSleepEayPercentTvV = b31sSleepEayPercentTv.getText().toString();
                String b31sSleepEayResultTvV = b31sSleepEayResultTv.getText().toString();
                verticalSleepType("快速眼动",b31sSleepEayPercentTvV,b31sSleepEayResultTvV,4);
                break;
            case R.id.sleepDeepConLin:  //深睡
                String b31sSleepDeepPercentTvV = b31sSleepDeepPercentTv.getText().toString();
                String b31sSleepDeepResultTvV = b31sSleepDeepResultTv.getText().toString();

                verticalSleepType("深睡",b31sSleepDeepPercentTvV,b31sSleepDeepResultTvV,5);

                break;
            case R.id.sleepLowConLin:   //浅睡
                String b31sSleepLowPercentTvV = b31sSleepLowPercentTv.getText().toString();
                String b31sSleepLowResultTvV = b31sSleepLowResultTv.getText().toString();

                verticalSleepType("浅睡",b31sSleepLowPercentTvV,b31sSleepLowResultTvV,6);
                break;
        }
    }


    private void verticalSleepType(String sleepDesc,String valueStr ,String status,int code){
        startActivity(B31sPercisionSleepDescActivity.class,new String[]{"sleepDesc","valueStr","status","code"},new String[]{sleepDesc,valueStr,status,String.valueOf(code)});

    }


}
