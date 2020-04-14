package com.example.bozhilun.android.friend;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.women.WomenDetailActivity;
import com.example.bozhilun.android.b36.calview.WomenMenBean;
import com.example.bozhilun.android.friend.bean.MenstrualCycle;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.CalendarView;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

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
 * 现在女性生理期数据
 * Created by Admin
 * Date 2020/3/2
 */
public class FriendWomenActivity extends WatchBaseActivity implements CalendarView.OnCalendarSelectListener
        , CalendarView.OnMonthChangeListener {

    private static final String TAG = "FriendWomenActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.friendWomenCalendarView)
    CalendarView friendWomenCalendarView;
    @BindView(R.id.friendWomenShowChooseDateTv)
    TextView friendWomenShowChooseDateTv;
    @BindView(R.id.friendWomenShowWomenStateTv)
    TextView friendWomenShowWomenStateTv;
    @BindView(R.id.friendWomenCurrDateTv)
    TextView friendWomenCurrDateTv;

    //月经期的集合
    private List<WomenMenBean> list = new ArrayList<>();
    //保存排卵日的map
    Map<String, String> mps = new HashMap<>();
    //保存最后一次的月经期的map
    private Map<String, String> lastMenMap = new HashMap<>();

    //一天的long类型
    long oneDayLong = 86400000L;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);


    private MenstrualCycle menstrualCycle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_women_layout);
        ButterKnife.bind(this);

        initViews();

        String friendStr = getIntent().getStringExtra("friend_women");
        if (WatchUtils.isEmpty(friendStr))
            return;

        menstrualCycle = new Gson().fromJson(friendStr, MenstrualCycle.class);
        if (menstrualCycle == null)
            return;

        Log.e(TAG,"------menstrualCycle="+menstrualCycle.toString());

        initTmpData(menstrualCycle);


        friendWomenCalendarView.setOnCalendarSelectListener(this);
        friendWomenCalendarView.setOnMonthChangeListener(this);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_frend) + getResources().getString(R.string.b36_period_day));

    }


    //计算月经期
    private void initTmpData(MenstrualCycle fWomen) {
        //最后一次月经日
        String mapLastMenDate = fWomen.getLastMensesDay();

        //间隔长度
        int menesInterval = fWomen.getCycleDays();
//        //间隔长度的long类型
//        long menesLongInterval = sdf.parse(menesInterval).getTime();
        //月经长度
        int menseLength = fWomen.getDurationDays();

        Log.e(TAG, "----------月经长度=" + menseLength);

        int womenMenInter = menesInterval - menseLength;
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
                long schemeMensue = longLtMonth + oneDayLong * menseLength;
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
                    womenMenBean.setDurationDay(menseLength);

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
                    womenMenBean.setDurationDay(menseLength - wb2.getDurationDay());
                }
                womenMenBean.setCycle(28);

                list.add(womenMenBean);

                //下一次月经的经期开始日=最后一次的经期开始日+月经周期
                mapLastMenDate = WatchUtils.getLongToDate("yyyy-MM-dd", longLtMonth + menesInterval * oneDayLong);
                // Log.e(TAG,"---------下一次月经的开始日="+mapLastMenDate);


                // Log.e(TAG, "-----wb=" + womenMenBean.toString());


                //排卵日=下次月经开始往前推14天
                long valLongDate = sdf.parse(mapLastMenDate).getTime() - 14 * oneDayLong;
                String valLongDateStr = WatchUtils.getLongToDate("yyyy-MM-dd", valLongDate);
                mps.put(valLongDateStr, getResources().getString(R.string.b36_ovulation_day));  //排卵日


                count++;
                isContinue = count != 5;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //标记月经期
        Map<String, Calendar> schemeMap = new HashMap<>();

        //标记排卵日
        for (Map.Entry<String, String> mp : mps.entrySet()) {
            String keyDay = mp.getKey();
            int ovulationYear = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrYear(keyDay) : 0;
            int ovulationMonth = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrMonth(keyDay) : 0;
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
                        beforeOvulationMonth, beforeOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " " + i + " " + getResources().getString(R.string.data_report_day)).toString(),
                        getSchemeCalendar(beforeOvulationYear,
                                beforeOvulationMonth, beforeOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " " + i + " " + getResources().getString(R.string.data_report_day)));

            }


            String currDayStr2 = keyDay;
            for (int j = 7; j <= 10; j++) {     //获取排卵日后4天的时间
                currDayStr2 = WatchUtils.obtainAroundDate(currDayStr2.trim(), false, 0);
                int afterOvulationYear = !WatchUtils.isEmpty(currDayStr2) ? DateTimeUtils.getCurrYear(currDayStr2) : 0;
                int afterOvulationMonth = !WatchUtils.isEmpty(currDayStr2) ? DateTimeUtils.getCurrMonth(currDayStr2) : 0;
                int afterOvulationDay = !WatchUtils.isEmpty(currDayStr2) ? DateTimeUtils.getCurrDay(currDayStr2) : 0;

                schemeMap.put(getSchemeCalendar(afterOvulationYear,
                        afterOvulationMonth, afterOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " " + j + " " + getResources().getString(R.string.data_report_day)).toString(),
                        getSchemeCalendar(afterOvulationYear,
                                afterOvulationMonth, afterOvulationDay, Color.parseColor("#e6aaf8"), getResources().getString(R.string.b36_ovulation_period) + " " + j + " " + getResources().getString(R.string.data_report_day)));

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
            for (int i = 1; i <= menseLength; i++) {
                lastMenMap.put(WatchUtils.getLongToDate("yyyy-MM-dd", (sdf.parse(mapLastMenDate).getTime() + (i - 1) * oneDayLong)),
                        getResources().getString(R.string.b36_period) + " " + i + " " + getResources().getString(R.string.data_report_day));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        //绘制最后一次的月经期
        for (Map.Entry<String, String> ltMap : lastMenMap.entrySet()) {
            String keyDay = ltMap.getKey();
            int ltYear = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrYear(keyDay) : 0;
            int ltMonth = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrMonth(keyDay) : 0;
            int ltDay = !WatchUtils.isEmpty(keyDay) ? DateTimeUtils.getCurrDay(keyDay) : 0;
            schemeMap.put(getSchemeCalendar(ltYear, ltMonth, ltDay, Color.RED, ltMap.getValue()).toString(),
                    getSchemeCalendar(ltYear, ltMonth, ltDay, Color.RED, ltMap.getValue()));

        }


        friendWomenCalendarView.setSchemeDate(schemeMap);
        //回到当前日期
        if (friendWomenCalendarView != null)
            friendWomenCalendarView.scrollToCurrent();

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


    //月份改变事件
    @Override
    public void onMonthChange(int year, int month) {
        friendWomenCurrDateTv.setText(year + "-" + (month<9?"0"+month:month));
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    //日期点击
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        String clickDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
        //this.clickCalendarDate = clickDate;
        friendWomenShowChooseDateTv.setText(WatchUtils.getWeekData(FriendWomenActivity.this, calendar.getWeek())
                + "," + clickDate);
        switch (menstrualCycle.getCycleStatus()) {
            case 0:         //经期
            case 1:         //备孕期
            case 3:          //宝妈期
                if (WatchUtils.isEmpty(calendar.getScheme())) {
                    friendWomenShowWomenStateTv.setText(getResources().getString(R.string.b36_safety_day));
                } else {
                    String schemeStr = calendar.getScheme();
                    friendWomenShowWomenStateTv.setText(calendar.getScheme());
                }

                //保存当天的状态
                if (WatchUtils.isEquesValue(clickDate)) {
                    SharedPreferencesUtils.setParam(FriendWomenActivity.this, Commont.WOMEN_SAVE_CURRDAY_STATUS,
                            friendWomenShowWomenStateTv.getText().toString().trim());
                }

                //判断点击的日期
                //verticalOper(calendar);
                break;
            case 2:         //怀孕期
//                int statusDay = +WatchUtils.babyWeeks(startPregrantDate, calendar.getYear()
//                        + "-" + calendar.getMonth() + "-" + calendar.getDay());
//                Log.e(TAG, "------statusDay=" + statusDay);
//                if (statusDay == -1) {
//                    friendWomenShowWomenStateTv.setText("");
//                } else {
//                    if (!WatchUtils.isEmpty(calendar.getScheme()) && calendar.getScheme().equals("baby"+getResources().getString(R.string.birthday))) {
//                        friendWomenShowWomenStateTv.setText("baby"+getResources().getString(R.string.birthday));
//                    } else {
//                        friendWomenShowWomenStateTv.setText(getResources().getString(R.string.b36_pregnancy) + " " +  statusDay +" " + getResources().getString(R.string.data_report_week));
//                    }
//
//                }

                break;

        }
    }




    @OnClick({R.id.commentB30BackImg, R.id.friendWomenCurrDateLeft,
            R.id.friendWomenCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.friendWomenCurrDateLeft:
                if (friendWomenCalendarView != null)
                    friendWomenCalendarView.scrollToPre();
                break;
            case R.id.friendWomenCurrDateRight:
                if (friendWomenCalendarView != null)
                    friendWomenCalendarView.scrollToNext(true);
                break;
        }
    }
}
