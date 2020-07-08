package com.example.bozhilun.android.b30;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30CusSleepView;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.b30.model.CusVPSleepData;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.example.bozhilun.android.view.CusScheduleView;
import com.example.bozhilun.android.view.DateSelectDialogView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 睡眠详情
 */
public class B30SleepDetailActivity extends WatchBaseActivity {

    private static final String TAG = "B30SleepDetailActivity";
    @BindView(R.id.startSleepTimeTv)
    TextView startSleepTimeTv;
    @BindView(R.id.endSleepTimeTv)
    TextView endSleepTimeTv;
    @BindView(R.id.sleepScheduleView)
    CusScheduleView sleepScheduleView;
    @BindView(R.id.sleepPercentTv)
    TextView sleepPercentTv;
    @BindView(R.id.sleepDetailAllTv)
    TextView sleepDetailAllTv;
    @BindView(R.id.sleepDetailGoalTv)
    TextView sleepDetailGoalTv;
    @BindView(R.id.commDateLin)
    LinearLayout commDateLin;

    /**
     * 跳转到B30SleepDetailActivity,并附带参数
     *
     * @param context 启动源上下文
     * @param date    附带的参数:日期
     */
    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B30SleepDetailActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.detailSleepQuitRatingBar)
    RatingBar detailSleepQuitRatingBar;
    @BindView(R.id.detailCusSleepView)
    B30CusSleepView detailCusSleepView;
    @BindView(R.id.detailAllSleepTv)
    TextView detailAllSleepTv;
    @BindView(R.id.detailAwakeNumTv)
    TextView detailAwakeNumTv;
    @BindView(R.id.detailStartSleepTv)
    TextView detailStartSleepTv;
    @BindView(R.id.detailAwakeTimeTv)
    TextView detailAwakeTimeTv;
    @BindView(R.id.detailDeepTv)
    TextView detailDeepTv;
    @BindView(R.id.detailHightSleepTv)
    TextView detailHightSleepTv;

    @BindView(R.id.commArrowDate)
    TextView sleepCurrDateTv;
    @BindView(R.id.sleepSeekBar)
    SeekBar sleepSeekBar;

    private List<Integer> listValue;

    /**
     * 当前显示的日期(数据根据日期加载)
     */
    private String currDay;
    /**
     * Json工具类
     */
    private Gson gson;

    private DateSelectDialogView dateSelectDialogView;

    private DecimalFormat decimalFormat = new DecimalFormat("##");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_sleep_detail_layout);
        ButterKnife.bind(this);
        initViews();
        initData();
    }

    private void initViews() {
        commentB30ShareImg.setVisibility(View.VISIBLE);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
        commDateLin.setBackgroundColor(Color.parseColor("#6174C0"));
//        commentB30ShareImg.setVisibility(View.VISIBLE);
        detailSleepQuitRatingBar.setMax(5);
        detailSleepQuitRatingBar.setNumStars(1);
        listValue = new ArrayList<>();
        gson = new Gson();
        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);

    }

    private void initData() {
        try {
            sleepCurrDateTv.setText(currDay);
            String mac = MyApp.getInstance().getMacAddress();
            if (WatchUtils.isEmpty(mac)){
                sleepDetailAllTv.setText("--");
                sleepDetailGoalTv.setText("8h");
                sleepScheduleView.setAllScheduleValue(8);
                sleepScheduleView.setCurrScheduleValue(0);
                return;
            }

            String sleep = B30HalfHourDao.getInstance().findOriginData(mac, currDay, B30HalfHourDao
                    .TYPE_SLEEP);
            CusVPSleepData sleepData = gson.fromJson(sleep, CusVPSleepData.class);
            showSleepChartView(sleepData);
            int sleepQulity = sleepData == null ? 0 : sleepData.getSleepQulity();
            detailSleepQuitRatingBar.setMax(5);
            detailSleepQuitRatingBar.setNumStars(sleepQulity);
            detailSleepQuitRatingBar.setVisibility(sleepQulity == 0 ? View.INVISIBLE : View.VISIBLE);
            //detailSleepQuitRatingBar.setEnabled(false);

            String time = sleepData == null ? "--" : (sleepData.getAllSleepTime() / 60) + "H" +
                    (sleepData.getAllSleepTime() % 60) + "m";
            detailAllSleepTv.setText(time);//睡眠时长

            String b30SleepGoal = (String) SharedPreferencesUtils.getParam(B30SleepDetailActivity.this, "b30SleepGoal", "");
            if (sleepData == null) {
                sleepScheduleView.setAllScheduleValue(Float.valueOf(b30SleepGoal));
                sleepScheduleView.setCurrScheduleValue(0f);
                sleepPercentTv.setText("0%");
                sleepDetailAllTv.setText("--");
                sleepDetailGoalTv.setText(b30SleepGoal + "h");
            } else {
                int allCount = sleepData.getAllSleepTime();
                int timpHour = allCount / 60;
                float currTime = timpHour + Float.valueOf(String.valueOf(WatchUtils.div(allCount % 60, 60, 2)));
                sleepScheduleView.setAllScheduleValue(Float.valueOf(b30SleepGoal));
                sleepScheduleView.setCurrScheduleValue(currTime);

                float tmpGoal = Float.valueOf(b30SleepGoal) * 60;
                double sleepPre = WatchUtils.div(allCount, tmpGoal, 2);
                double tmpSleepPre = sleepPre * 100;
                String formatStr = decimalFormat.format(tmpSleepPre);
                sleepPercentTv.setText(allCount >= tmpGoal ? "100%" : formatStr + "%");

                sleepDetailAllTv.setText(time);
                sleepDetailGoalTv.setText(b30SleepGoal + "h");
            }

            String count = sleepData == null ? "--" : "" + sleepData.getWakeCount();
            detailAwakeNumTv.setText(count);//苏醒次数
            String down = sleepData == null ? "--" : sleepData.getSleepDown().getDateForSleepshow();
            detailStartSleepTv.setText(down);//入睡时间
            startSleepTimeTv.setText(sleepData == null ? "" : sleepData.getSleepDown().getColck());
            String up = sleepData == null ? "--" : sleepData.getSleepUp().getDateForSleepshow();
            detailAwakeTimeTv.setText(up);//苏醒时间
            endSleepTimeTv.setText(sleepData == null ? "" : sleepData.getSleepUp().getColck());
            String deep = sleepData == null ? "--" : sleepData.getDeepSleepTime() / 60 + "H" +
                    (sleepData.getDeepSleepTime() % 60) + "m";
            detailDeepTv.setText(deep);//深度睡眠
            String low = sleepData == null ? "--" : sleepData.getLowSleepTime() / 60 + "H" +
                    (sleepData.getLowSleepTime() % 60) + "m";
            detailHightSleepTv.setText(low);// 浅度睡眠
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showSleepChartView(final CusVPSleepData sleepData) {
        listValue.clear();
        if (sleepData != null) {
            String slleepLin = sleepData.getSleepLine();
            for (int i = 0; i < slleepLin.length(); i++) {
                if (i <= slleepLin.length() - 1) {
                    int subStr = Integer.valueOf(slleepLin.substring(i, i + 1));
                    listValue.add(subStr);
                }
            }
            listValue.add(0, 2);
            listValue.add(0);
            listValue.add(2);
        }
        if (listValue.size() > 0) {
            detailCusSleepView.setSeekBarShow(false);
            detailCusSleepView.setSleepList(listValue);
            sleepSeekBar.setMax(listValue.size());
            sleepSeekBar.setProgress(-2);
            sleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if (progress == listValue.size())
                            return;
                        //Log.e(TAG,"-------progress="+progress+"--="+sleepData.getSleepDown().getDateAndClockForSleep());
                        int sleepHour = sleepData.getSleepDown().getHour() * 60;
                        int sleepMine = sleepData.getSleepDown().getMinute();
                        //入睡时间 分钟
                        int sleepDownT = sleepHour + sleepMine;
                        int currD = sleepDownT + ((progress == 0 ? -1 : progress - 1) * 5);   //当前的分钟
                        //转换成时：分
                        int hour = (int) Math.floor(currD / 60);
                        if (hour >= 24)
                            hour = hour - 24;
                        int mine = currD % 60;

                        detailCusSleepView.setSleepDateTxt((hour == 0 ? "00" : (hour < 10 ? "0" + hour : hour)) + ":" + (mine == 0 ? "00" : (mine < 10 ? "0" + mine : mine)) + "");
                        detailCusSleepView.setSeekBarSchdue(progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    detailCusSleepView.setSeekBarShow(true, 0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } else {
            detailCusSleepView.setSleepList(new ArrayList<Integer>());
        }
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg,
            R.id.commArrowLeft, R.id.commArrowRight,
            R.id.commArrowDate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B30SleepDetailActivity.this);
                break;
            case R.id.commArrowLeft:   //切换上一天数据
                changeDayData(true);
                break;
            case R.id.commArrowRight:   //切换下一天数据
                changeDayData(false);
                break;
            case R.id.commArrowDate:
                chooseDate();
                break;
        }
    }

    private void chooseDate() {
        dateSelectDialogView = new DateSelectDialogView(this);
        dateSelectDialogView.show();
        dateSelectDialogView.setOnDateSelectListener(new DateSelectDialogView.OnDateSelectListener() {
            @Override
            public void selectDateStr(String str) {
                dateSelectDialogView.dismiss();
                currDay = str;
                initData();
            }
        });
    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {
        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(WatchUtils.getCurrentDate()) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        initData();
    }

}
