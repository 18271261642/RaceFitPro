package com.example.bozhilun.android.b30.women;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b36.calview.WomenMenBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.CalendarView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 女性详情页 B36
 * Created by Admin
 * Date 2018/11/12
 */
public class WomenDetailActivity extends WatchBaseActivity implements CalendarView.OnCalendarSelectListener
        , CalendarView.OnMonthChangeListener {

    private static final String TAG = "WomenDetailActivity";

    //一天的long类型
    static long oneDayLong = 86400000L;

    //日历
    @BindView(R.id.womenCalendarView)
    CalendarView calendarView;
    //切换月份显示的tv
    @BindView(R.id.womenCurrDateTv)
    TextView womenCurrDateTv;
    @BindView(R.id.womenTitleTv)
    TextView womenTitleTv;
    //手环通知开关
    @BindView(R.id.womenNotToggle)
    ToggleButton womenNotToggle;
    //手环通知的栏目
    @BindView(R.id.womenNotiRel)
    RelativeLayout womenNotiRel;
    //经期开始显示tv
    @BindView(R.id.womenLastJingqiStartTv)
    TextView womenLastJingqiStartTv;
    //经期开始的开关
    @BindView(R.id.womenJinqiStartToggle)
    ToggleButton womenJinqiStartToggle;

    //是否推送开关
    @BindView(R.id.womenPushNotToggle)
    ToggleButton womenPushNotToggle;



    //显示选择的日期
    @BindView(R.id.womenShowChooseDateTv)
    TextView womenShowChooseDateTv;
    //显示当前的状态
    @BindView(R.id.womenShowWomenStateTv)
    TextView womenShowWomenStateTv;
    //状态图片
    @BindView(R.id.womenTitleRecImg)
    ImageView womenTitleRecImg;
    //怀孕期无此布局
    @BindView(R.id.jignqiNoLin)
    LinearLayout jignqiNoLin;

    @BindView(R.id.showNoOpStatusTv)
    TextView showNoOpStatusTv;
    @BindView(R.id.b36NoOperLin)
    LinearLayout b36NoOperLin;
    //经期开始和结束的开关显示的状态文字
    @BindView(R.id.womenJinQiStatusTv)
    TextView womenJinQiStatusTv;

    private java.util.Calendar sysCalendar;

    //月经期的集合
    private List<WomenMenBean> list = new ArrayList<>();

    //保存排卵日的map
    Map<String, String> mps = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    //女性周期状态
    int womenStatus = 0;
    //最后一次或推算的怀孕起始日
    String startPregrantDate = null;

    //保存最后一次的月经期的map
    private Map<String, String> lastMenMap = new HashMap<>();


    //点击的日期的日期
    String clickCalendarDate = null;


    private AlertDialog.Builder pushAlert;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initTmpData();
        }
    };





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_detail);
        ButterKnife.bind(this);

        initViews();

        String bleName = (String) SharedPreferencesUtils.readObject(WomenDetailActivity.this,Commont.BLENAME);
        if(bleName != null && bleName.equals("B16"))
            womenNotiRel.setVisibility(View.GONE);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initStatus();


        if (calendarView != null)
            calendarView.scrollToCurrent();

    }

    private void initStatus() {
        list.clear();
        mps.clear();
        lastMenMap.clear();

        //最近一次月经开始日
        String recordMenDay = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());
        womenLastJingqiStartTv.setText(recordMenDay);
        //状态
        womenStatus = (int) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_PHYSIOLOGY_STATUS, 0);
        //判断状态
        switch (womenStatus) {
            case 0:     //经期
                jignqiNoLin.setVisibility(View.VISIBLE);
                womenTitleRecImg.setBackground(getResources().getDrawable(R.drawable.register_menes_white));
                handler.sendEmptyMessage(1001);
                B36SetWomenDataServer.setB30WomenData(womenStatus);
                break;
            case 1:     //备孕期
                jignqiNoLin.setVisibility(View.VISIBLE);
                womenTitleRecImg.setBackground(getResources().getDrawable(R.drawable.register_menes_preready_white));
                handler.sendEmptyMessage(1001);
                B36SetWomenDataServer.setB30WomenData(womenStatus);
                break;
            case 2:     //怀孕期
                jignqiNoLin.setVisibility(View.GONE);
                womenTitleRecImg.setBackground(getResources().getDrawable(R.drawable.register_preing_white));
                showGravidity();
                break;
            case 3:     //宝妈期
                jignqiNoLin.setVisibility(View.VISIBLE);
                womenTitleRecImg.setBackground(getResources().getDrawable(R.drawable.register_mamami_white));
                handler.sendEmptyMessage(1001);
                B36SetWomenDataServer.setB30WomenData(womenStatus);
                break;
        }

    }


    //展示怀孕期
    private void showGravidity() {
        //预测出生日
        String babyBornDate = null;
        boolean isSmartSwitch = (boolean) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_LAST_MEN_STATUS, false);
        Log.e(TAG,"---------isSmartSwitch="+isSmartSwitch);
        if (isSmartSwitch) {      //根据最后一次的月经期预测
            //往后280天出生日
            //最后一次月经
            String lastMenDate = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());
            startPregrantDate = lastMenDate;
            //最后一次时间转long型
            try {
                long lstMenDate = sdf.parse(lastMenDate.trim()).getTime();
                //Log.e(TAG, "-----最后一次日期=" + lstMenDate);
                long babyBirtyDate = lstMenDate + 86400000L * 280;
                //Log.e(TAG, "-----出生日=" + babyBirtyDate + "-=" + WatchUtils.getLongToDate("yyyy-MM-dd", babyBirtyDate));
                babyBornDate = WatchUtils.getLongToDate("yyyy-MM-dd", babyBirtyDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            //预测出生日
            babyBornDate = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.BABY_BORN_DATE, WatchUtils.getCurrentDate());
            try {
                long longBabyD = sdf.parse(babyBornDate).getTime();
                startPregrantDate = WatchUtils.getLongToDate("yyyy-MM-dd", longBabyD - (86400000L * 280));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (WatchUtils.isEmpty(babyBornDate))
            babyBornDate = WatchUtils.getCurrentDate();

        SharedPreferencesUtils.setParam(WomenDetailActivity.this,Commont.BABY_BORN_DATE,babyBornDate);

        B36SetWomenDataServer.setB30WomenData(womenStatus);

        //计算年月日2018-11-26
        int babyYear = DateTimeUtils.getCurrYear(babyBornDate);//Integer.valueOf(babyBornDate.substring(0, 4).trim());
        //月
        int babyMonth = DateTimeUtils.getCurrMonth(babyBornDate);//Integer.valueOf(babyBornDate.substring(5, 7).trim());
        //日
        int babyDay = DateTimeUtils.getCurrDay(babyBornDate);//Integer.valueOf(babyBornDate.substring(8, babyBornDate.length()));
        Map<String, Calendar> babyBornMap = new HashMap<>();
        babyBornMap.put(getSchemeCalendar(babyYear, babyMonth, babyDay, Color.parseColor("#3f1256"), "baby"+getResources().getString(R.string.birthday)).toString(),
                getSchemeCalendar(babyYear, babyMonth, babyDay, Color.parseColor("#3f1256"), "baby"+getResources().getString(R.string.birthday)));
        calendarView.setSchemeDate(babyBornMap);


    }

    //计算月经期
    private void initTmpData() {
        String mapLastMenDate;
        //保存的最后一次经期日期
        String lastMenDate = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());
        if (WatchUtils.isEmpty(lastMenDate))
            lastMenDate = WatchUtils.getCurrentDate();
        mapLastMenDate = lastMenDate;

        //间隔长度
        String menesInterval = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_MEN_INTERVAL, "28");
        if (WatchUtils.isEmpty(menesInterval))
            menesInterval = "28";
//        //间隔长度的long类型
//        long menesLongInterval = sdf.parse(menesInterval).getTime();
        //月经长度
        String menseLength = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_MEN_LENGTH, "5");
        if (WatchUtils.isEmpty(menseLength))
            menseLength = "5";

        Log.e(TAG,"----------月经长度="+menseLength);

        int womenMenInter = Integer.valueOf(menesInterval.trim()) - Integer.valueOf(menseLength.trim());
        Log.e(TAG, "------经期长度=" + menseLength + "-=间隔周期=" + womenMenInter);


        int count = 0;
        boolean isContinue = true;
        try {
            while (isContinue) {
                //Log.e(TAG, "------最后一次经期开始日=" + mapLastMenDate);
                WomenMenBean womenMenBean = new WomenMenBean();
                //计算年月日2018-11-26
                int lastYear = DateTimeUtils.getCurrYear(mapLastMenDate);
                //月
                int lastMonth = DateTimeUtils.getCurrMonth(mapLastMenDate);
                //日
                int lastDay = DateTimeUtils.getCurrDay(mapLastMenDate);

                //Log.e(TAG, "------最后一次经期年月日=" + lastYear + "-" + lastMonth + "-" + lastDay);
                //最后一次经期的日期，转换成long类型
                long longLtMonth = sdf.parse(mapLastMenDate).getTime();
                //月经的持续长度long类型=最后一次经期的开始日+加上经期长度
                long schemeMensue = longLtMonth + oneDayLong * Integer.valueOf(menseLength.trim());
                //转换日期 即结束日期
                String retDate = WatchUtils.getLongToDate("yyyy-MM-dd", schemeMensue);
                //当月最大天数
                int lastMonthDay = CalendarUtil.getMonthDaysCount(lastYear, lastMonth);
                // Log.e(TAG, "-----结束日期=" + retDate + "---月份：=" + lastMonth + "当月最大天数:" + lastMonthDay);
                //当月的最大日期
                String reptDate = lastYear + "-" + lastMonth + "-" + lastMonthDay;
                //Log.e(TAG, "-----当月的最大日期=" + reptDate);

                //当月的最后日期 eg:2018-11-30
                long currLastDayLong = sdf.parse(reptDate).getTime();

                //差值--当月的最后一天-（月经开始的日期+月经周期）
                long diffDay = currLastDayLong - schemeMensue;
                //Log.e(TAG, "-------差值=" + diffDay + "--=" + diffDay / oneDayLong);
                if (diffDay >= 0) {    //当月 全部都在当月
                    womenMenBean.setYear(lastYear);
                    womenMenBean.setDate(lastMonth);
                    womenMenBean.setBeginTime(lastDay);
                    womenMenBean.setDurationDay(Integer.valueOf(menseLength.trim()));

                } else {      //下个月
                    //标记当月的,当月的部分
                    WomenMenBean wb2 = new WomenMenBean();
                    wb2.setYear(lastYear);
                    wb2.setDate(lastMonth);
                    wb2.setBeginTime(lastDay);
                    int currRemDayNum = (int) ((currLastDayLong - longLtMonth) / oneDayLong) + 1;
                    wb2.setDurationDay(currRemDayNum);
                    wb2.setCycle(28);
                    list.add(wb2);

                    if (lastMonth + 1 > 12) {     //超过了12月
                        womenMenBean.setYear(lastYear + 1);
                        womenMenBean.setDate(lastMonth + 1 - 12);
                    } else {  //没有超过12月
                        womenMenBean.setYear(lastYear);
                        womenMenBean.setDate(lastMonth + 1);
                    }
                    womenMenBean.setBeginTime(1);
                    //Log.e(TAG,"----------持续时长="+(Integer.valueOf(menseLength.trim()) - wb2.getDurationDay()));
                    womenMenBean.setDurationDay(Integer.valueOf(menseLength.trim()) - wb2.getDurationDay());
                }
                womenMenBean.setCycle(28);

                list.add(womenMenBean);

                //下一次月经的经期开始日=最后一次的经期开始日+月经周期
                mapLastMenDate = WatchUtils.getLongToDate("yyyy-MM-dd", longLtMonth + Integer.valueOf(menesInterval)  * oneDayLong);
               // Log.e(TAG,"---------下一次月经的开始日="+mapLastMenDate);


               // Log.e(TAG, "-----wb=" + womenMenBean.toString());


                //排卵日=下次月经开始往前推14天
                long valLongDate = sdf.parse(mapLastMenDate).getTime() - 14 * oneDayLong;
                String valLongDateStr = WatchUtils.getLongToDate("yyyy-MM-dd",valLongDate);
                mps.put(valLongDateStr, getResources().getString(R.string.b36_ovulation_day));  //排卵日


                count++;
                isContinue = count != 15;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //标记月经期
        Map<String, Calendar> schemeMap = new HashMap<>();




        //标记排卵日
        for (Map.Entry<String, String> mp : mps.entrySet()) {
            String keyDay = mp.getKey();
            int ovulationYear =!WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrYear(keyDay) : 0;
            int ovulationMonth = !WatchUtils.isEmpty(keyDay) ?DateTimeUtils.getCurrMonth(keyDay) : 0;
            int ovulationDay = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrDay(keyDay) : 0;

            schemeMap.put(getSchemeCalendar(ovulationYear,
                    ovulationMonth, ovulationDay, Color.WHITE, getResources().getString(R.string.b36_ovulation_day)).toString(),
                    getSchemeCalendar(ovulationYear,
                            ovulationMonth, ovulationDay, Color.WHITE, getResources().getString(R.string.b36_ovulation_day)));

            String currDayStr = keyDay;
            for (int i = 5; i >= 1; i--) {     //获取排卵期前5天的时间
                currDayStr = WatchUtils.obtainAroundDate(currDayStr, true, 0);
                int beforeOvulationYear = !WatchUtils.isEmpty(currDayStr) ? DateTimeUtils.getCurrYear(currDayStr) : 0;
                int beforeOvulationMonth = !WatchUtils.isEmpty(currDayStr) ? DateTimeUtils.getCurrMonth(currDayStr) : 0;
                int beforeOvulationDay = !WatchUtils.isEmpty(currDayStr) ? DateTimeUtils.getCurrDay(currDayStr) : 0;

                schemeMap.put(getSchemeCalendar(beforeOvulationYear,
                        beforeOvulationMonth, beforeOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) +" "+ i +" "+ getResources().getString(R.string.data_report_day)).toString(),
                        getSchemeCalendar(beforeOvulationYear,
                                beforeOvulationMonth, beforeOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " "+i +" "+ getResources().getString(R.string.data_report_day)));

            }


            String currDayStr2 = keyDay;
            for (int j = 7; j <= 10; j++) {     //获取排卵日后4天的时间
                currDayStr2 = WatchUtils.obtainAroundDate(currDayStr2.trim(), false, 0);
                int afterOvulationYear = !WatchUtils.isEmpty(currDayStr2) ? DateTimeUtils.getCurrYear(currDayStr2) : 0;
                int afterOvulationMonth = !WatchUtils.isEmpty(currDayStr2) ? DateTimeUtils.getCurrMonth(currDayStr2) : 0;
                int afterOvulationDay = !WatchUtils.isEmpty(currDayStr2) ? DateTimeUtils.getCurrDay(currDayStr2) : 0;

                schemeMap.put(getSchemeCalendar(afterOvulationYear,
                        afterOvulationMonth, afterOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " "+j +" "+ getResources().getString(R.string.data_report_day)).toString(),
                        getSchemeCalendar(afterOvulationYear,
                                afterOvulationMonth, afterOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " "+ j+" " + getResources().getString(R.string.data_report_day)));

            }


        }



        //绘制月经期
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getDurationDay(); j++) {
                schemeMap.put(getSchemeCalendar(list.get(i).getYear(), list.get(i).getDate(), list.get(i).getBeginTime() + j, Color.RED, getResources().getString(R.string.b36_predict_period)).toString(),
                        getSchemeCalendar(list.get(i).getYear(), list.get(i).getDate(), list.get(i).getBeginTime() + j, Color.RED, getResources().getString(R.string.b36_predict_period)));

            }

        }

        lastMenMap.clear();
        try {
            //保存最后一次的月经及月经长度
            for (int i = 1; i <= Integer.valueOf(menseLength.trim()); i++) {
                lastMenMap.put(WatchUtils.getLongToDate("yyyy-MM-dd", (sdf.parse(lastMenDate).getTime() + (i - 1) * oneDayLong)),
                        getResources().getString(R.string.b36_period) + " " + i+" " + getResources().getString(R.string.data_report_day));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        //绘制最后一次的月经期
        for (Map.Entry<String, String> ltMap : lastMenMap.entrySet()) {
            String keyDay = ltMap.getKey();
            int ltYear = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrYear(keyDay): 0;
            int ltMonth =!WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrMonth(keyDay) : 0;
            int ltDay = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrDay(keyDay) : 0;
            schemeMap.put(getSchemeCalendar(ltYear, ltMonth, ltDay, Color.RED, ltMap.getValue()).toString(),
                    getSchemeCalendar(ltYear, ltMonth, ltDay, Color.RED, ltMap.getValue()));

        }




        calendarView.setSchemeDate(schemeMap);
        //回到当前日期
        if (calendarView != null)
            calendarView.scrollToCurrent();

    }


    private void initViews() {
        womenTitleTv.setText(getResources().getString(R.string.b36_femal_pages));
        sysCalendar = java.util.Calendar.getInstance();
        calendarView.setOnCalendarSelectListener(this);
        calendarView.setOnMonthChangeListener(this);
        womenCurrDateTv.setText(calendarView.getCurYear() + "-" + calendarView.getCurMonth());
        womenNotToggle.setOnCheckedChangeListener(onCheckedChangeListener);
        womenJinqiStartToggle.setOnCheckedChangeListener(onCheckedChangeListener);
        womenPushNotToggle.setOnCheckedChangeListener(onCheckedChangeListener);


        boolean isB36Noti = (boolean) SharedPreferencesUtils.getParam(WomenDetailActivity.this,Commont.IS_B36_JINGQI_NOTI,false);
        womenNotToggle.setChecked(isB36Noti);

        //是否开通推送开关
        boolean isWomenPush = (boolean) SharedPreferencesUtils.getParam(WomenDetailActivity.this,Commont.IS_WOMEN_PUSH,false);
        womenPushNotToggle.setChecked(isWomenPush);
    }

    @OnClick({R.id.womenTitleBackImg, R.id.womenTitleRecImg,
            R.id.womenCurrDateLeft, R.id.womenCurrDateRight,
            R.id.womenTitleCurrDayImg, R.id.lastJingqiRel,
            R.id.backCurrDayBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.womenTitleBackImg:    //返回
                finish();
                break;
            case R.id.womenTitleRecImg:   //记录
                startActivity(WomenPersonalActivity.class);
                break;
            case R.id.womenTitleCurrDayImg: //回到当前月
            case R.id.backCurrDayBtn:
                if (calendarView != null)
                    calendarView.scrollToCurrent();
                break;
            case R.id.womenCurrDateLeft:    //上一个月
                if (calendarView != null)
                    calendarView.scrollToPre();
                break;
            case R.id.womenCurrDateRight:   //下一个月
                if (calendarView != null)
                    calendarView.scrollToNext(true);
                break;
            case R.id.lastJingqiRel:    //最近一次月经
                chooseLastJingqiDate();
                break;

        }
    }


    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    //日期点击选择
    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        Log.e(TAG, "-----选择-=" + calendar.getScheme() + "-=" + womenStatus);
        String clickDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
        this.clickCalendarDate = clickDate;
        womenShowChooseDateTv.setText(WatchUtils.getWeekData(WomenDetailActivity.this, calendar.getWeek())
                + "," + clickDate);
        switch (womenStatus) {
            case 0:         //经期
            case 1:         //备孕期
            case 3:          //宝妈期
                if (WatchUtils.isEmpty(calendar.getScheme())) {
                    womenShowWomenStateTv.setText(getResources().getString(R.string.b36_safety_day));
                } else {
                    String schemeStr = calendar.getScheme();
                    womenShowWomenStateTv.setText(calendar.getScheme());
                }

                //保存当天的状态
                if (WatchUtils.isEquesValue(clickDate)) {
                    SharedPreferencesUtils.setParam(WomenDetailActivity.this, Commont.WOMEN_SAVE_CURRDAY_STATUS,
                            womenShowWomenStateTv.getText().toString().trim());
                }

                //判断点击的日期
                verticalOper(calendar);
                break;
            case 2:         //怀孕期
                int statusDay = +WatchUtils.babyWeeks(startPregrantDate, calendar.getYear()
                        + "-" + calendar.getMonth() + "-" + calendar.getDay());
                Log.e(TAG, "------statusDay=" + statusDay);
                if (statusDay == -1) {
                    womenShowWomenStateTv.setText("");
                } else {
                    if (!WatchUtils.isEmpty(calendar.getScheme()) && calendar.getScheme().equals("baby"+getResources().getString(R.string.birthday))) {
                        womenShowWomenStateTv.setText("baby"+getResources().getString(R.string.birthday));
                    } else {
                        womenShowWomenStateTv.setText(getResources().getString(R.string.b36_pregnancy) + " " +  statusDay +" " + getResources().getString(R.string.data_report_week));
                    }

                }

                break;

        }


    }

    //月份改变事件
    @Override
    public void onMonthChange(int year, int month) {
        womenCurrDateTv.setText(year + "-" + (month<9?"0"+month:month));
    }


    //判断是否可操作 大于当天不可操作
    private void verticalOper(Calendar calendar) {
        //点击的日期的文字信息
        String schTxt = womenShowWomenStateTv.getText().toString().trim();

        //判断是否大于当天
        try {
            //最近一次经期的日期
            String ltMenDate = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());

            //判断点击的日期是否在范围内,开始日期：最后一次经期；结束日期：当前日
            boolean isDateRange = CalendarUtil.isCalendarInRange(calendar, DateTimeUtils.getCurrYear(ltMenDate), DateTimeUtils.getCurrMonth(ltMenDate), DateTimeUtils.getCurrDay(ltMenDate),
                    DateTimeUtils.getCurrYear(), DateTimeUtils.getCurrMonth(), DateTimeUtils.getCurrDay());
            if (isDateRange) {
                b36NoOperLin.setVisibility(View.GONE);
                jignqiNoLin.setVisibility(View.VISIBLE);

                if(schTxt.equals(getResources().getString(R.string.b36_period)+" "+1+" "+getResources().getString(R.string.data_report_day))){
                    womenJinQiStatusTv.setText(getResources().getString(R.string.b36_period_start));
                    womenJinqiStartToggle.setChecked(true);

                }else{
                    womenJinQiStatusTv.setText(getResources().getString(R.string.b36_period_end));
                    womenJinqiStartToggle.setChecked(false);
                    //经期的第二天+经期长度+5天
                    String secondLtMen = WatchUtils.obtainAroundDate(ltMenDate,false,0);    //第一次经期的后一天
                    //月经长度
                    String menseLength = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this, Commont.WOMEN_MEN_LENGTH, "5");
                    if(schTxt.equals(getResources().getString(R.string.b36_period)+" "+Integer.valueOf(menseLength)+" "+getResources().getString(R.string.data_report_day))){
                        womenJinqiStartToggle.setChecked(true);
                    }



                    long isEndMenRang = sdf.parse(secondLtMen).getTime() + (Integer.valueOf(menseLength.trim())-2) * oneDayLong + 5 * oneDayLong;
                    //转换成string类型
                    String isEndMenRngStr = WatchUtils.getLongToDate("yyyy-MM-dd",isEndMenRang);
                    Log.e(TAG,"------结束状态日期="+isEndMenRngStr);
                    //判断点击的日期是否在范围内
                    boolean isClickRang =  CalendarUtil.isCalendarInRange(calendar, DateTimeUtils.getCurrYear(secondLtMen), DateTimeUtils.getCurrMonth(secondLtMen), DateTimeUtils.getCurrDay(secondLtMen),
                            DateTimeUtils.getCurrYear(isEndMenRngStr), DateTimeUtils.getCurrMonth(isEndMenRngStr), DateTimeUtils.getCurrDay(isEndMenRngStr));
                    Log.e(TAG,"-----isClickRang="+isClickRang);

                    if(isClickRang){
                        womenJinQiStatusTv.setText(getResources().getString(R.string.b36_period_end));
                    }else{
                        womenJinQiStatusTv.setText(getResources().getString(R.string.b36_period_start));
                    }
                }



            } else {
                b36NoOperLin.setVisibility(View.VISIBLE);
                jignqiNoLin.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //选择最后一次经期的时间
    private void chooseLastJingqiDate() {
        DatePick pickerPopWin = new DatePick.Builder(WomenDetailActivity.this, new DatePick.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                if (WatchUtils.comPariDate(WatchUtils.getCurrentDate(), dateDesc)) {
                    ToastUtil.showToast(WomenDetailActivity.this, "未来不可操作!");
                    return;
                }
                Log.e(TAG,"---------选择最后一次月经="+dateDesc);
                womenLastJingqiStartTv.setText(dateDesc);
                //保存最近一次月经日期
                SharedPreferencesUtils.setParam(WomenDetailActivity.this, Commont.WOMEN_LAST_MENSTRUATION_DATE, dateDesc);

                initStatus();

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1920) //min year in loop
                .maxYear(sysCalendar.get(java.util.Calendar.YEAR)) // max year in loop
                .dateChose(sysCalendar.get(java.util.Calendar.YEAR) + "-" + sysCalendar.get(java.util.Calendar.MONTH)+1 + "-" + sysCalendar.get(java.util.Calendar.DAY_OF_MONTH) + "") // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(WomenDetailActivity.this);
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed())
                return;
            switch (buttonView.getId()) {
                case R.id.womenNotToggle:   //手环通知
                    b36SettingNoti(isChecked);
                    break;
                case R.id.womenJinqiStartToggle:    //经期开始或经期结束
                    operMenStartOrEndStatus(isChecked);
                    break;
                case R.id.womenPushNotToggle:   //是否推送开关
                    setPushNoti(isChecked);
                    break;
            }

        }
    };

    //是否推送开关提醒
    private void setPushNoti(final boolean isCheck){
        pushAlert = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.prompt))
                .setMessage("开启推送:将自己的生理期状态信息通过消息推送给设置的特殊好友."+"\n"+"关闭推送:不将自己的生理期状态信息通过消息推送给设置的特殊好友.")
                .setPositiveButton(getResources().getText(R.string.complete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferencesUtils.setParam(WomenDetailActivity.this,Commont.IS_WOMEN_PUSH,isCheck);
                        B36SetWomenDataServer.setB30WomenData(womenStatus);
                    }
                });
        pushAlert.create().show();
    }



    //操作经期开始和经期结束
    private void operMenStartOrEndStatus(boolean isClick) {
        Log.e(TAG,"--------isClick="+isClick);
        //判断当前是什么状态
        String toggStatus = womenJinQiStatusTv.getText().toString().trim();
        //最近一次经期的日期
        String ltMenDate = (String) SharedPreferencesUtils.getParam(WomenDetailActivity.this,
                Commont.WOMEN_LAST_MENSTRUATION_DATE, WatchUtils.getCurrentDate());
        try {
            if(toggStatus.equals(getResources().getString(R.string.b36_period_end))){
                Log.e(TAG,"---11-----isClick="+isClick);
                if(isClick){
                    womenJinqiStartToggle.setChecked(true);
                    //计算经期长度，开始日期不变
                    //结束日期
                    long endMenDate = sdf.parse(clickCalendarDate).getTime();
                    long difStartEnd = endMenDate - sdf.parse(ltMenDate).getTime();
                    //长度
                    int womenLength = (int) (difStartEnd / oneDayLong) + 1;
                    Log.e(TAG,"---------长度="+womenLength);
                    SharedPreferencesUtils.setParam(WomenDetailActivity.this, Commont.WOMEN_MEN_LENGTH, womenLength+"");

                    initStatus();

                }

            }

            if(toggStatus.equals(getResources().getString(R.string.b36_period_start))){
                Log.e(TAG,"----22----isClick="+isClick);
                if(isClick){
                    womenJinqiStartToggle.setChecked(true);
                    //最后一次的月经开始日期
                    SharedPreferencesUtils.setParam(WomenDetailActivity.this,Commont.WOMEN_LAST_MENSTRUATION_DATE,clickCalendarDate);
                    initStatus();

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //B36手环的通知
    private void b36SettingNoti(boolean isChecked) {
        SharedPreferencesUtils.setParam(WomenDetailActivity.this,Commont.IS_B36_JINGQI_NOTI,isChecked);
    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

}
