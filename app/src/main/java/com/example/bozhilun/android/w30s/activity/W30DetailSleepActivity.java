package com.example.bozhilun.android.w30s.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.commdbserver.CommConstant;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.views.W30CusSleepChartView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * W30睡眠详情页面
 * Created by Admin
 * Date 2019/3/20
 */
public class W30DetailSleepActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.detailCusSleepView)
    W30CusSleepChartView detailCusSleepView;
    @BindView(R.id.sleepSeekBar)
    SeekBar sleepSeekBar;
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
    @BindView(R.id.sleepCurrDateTv)
    TextView sleepCurrDateTv;

    String currDay = WatchUtils.getCurrentDate();
    private Gson gson = new Gson();


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what ){
                case 1001:
                    Map<String,String> allSleepMap = (Map<String, String>) msg.obj;
                    if(allSleepMap == null || allSleepMap.isEmpty()){
                        detailCusSleepView.setNoDataColor(Color.WHITE);
                        detailCusSleepView.setSleepResultList(new ArrayList<Integer>());
                        //总睡眠时间
                        detailAllSleepTv.setText("--");
                        //入睡时间
                        detailStartSleepTv.setText("--");
                        //清醒时间
                        detailAwakeTimeTv.setText("--");
                        //深睡时长
                        detailDeepTv.setText("--");
                        //浅睡时长
                        detailHightSleepTv.setText("--");
                        //苏醒次数
                        detailAwakeNumTv.setText("--");
                        return;
                    }
                    //睡觉表示的字符串
                    String sleepStr = allSleepMap.get(CommConstant.W30_SLEEP_RESULT_SHOW);
                    List<Integer> sleepList = gson.fromJson(sleepStr,new TypeToken<List<Integer>>(){}.getType());
                    detailCusSleepView.setSeekBarShow(false);
                    detailCusSleepView.setSleepResultList(sleepList);


                    //总睡眠时间
                    int countSleep = Integer.valueOf(allSleepMap.get(CommConstant.W30_SLEEP_COUNT_ALL_TIME));

                    detailAllSleepTv.setText((countSleep/60)+"H"+(countSleep % 60) +"m");
                    //入睡时间
                    String startSleepTime = allSleepMap.get(CommConstant.W30_SLEEP_START_DATE);
                    detailStartSleepTv.setText(startSleepTime);
                    //清醒时间
                    detailAwakeTimeTv.setText(allSleepMap.get(CommConstant.W30_SLEEP_END_DATE));
                    //深睡时长
                    int deepTime = Integer.valueOf(allSleepMap.get(CommConstant.W30_SLEEP_DEEP_COUNT_TIME));
                    detailDeepTv.setText((deepTime / 60)+"H" + (deepTime % 60)+"m");
                    //浅睡时长
                    int lowTime = Integer.valueOf(allSleepMap.get(CommConstant.W30_SLEEP_LOW_COUNT_TIME));
                    detailHightSleepTv.setText((lowTime/60)+"H"+(lowTime%60)+"m");
                    //苏醒次数
                    detailAwakeNumTv.setText(allSleepMap.get(CommConstant.W30_SLEEP_AWAKE_COUNT)+"");
                    showSeekBar(detailCusSleepView,sleepList,startSleepTime);
                    break;
            }

        }
    };




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30_detail_sleep_layout);
        ButterKnife.bind(this);

        initViews();


        findSleepFromDb(currDay);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));

    }


    //睡眠滑动取值
    private void showSeekBar(final W30CusSleepChartView detailCusSleepView, final List<Integer> list, final String startSleepTime) {
        sleepSeekBar.setMax(list.size());
        sleepSeekBar.setProgress(-2);
        sleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //入睡的时间的开始小时
                int startHour = Integer.valueOf(StringUtils.substringBefore(startSleepTime,":")) * 60; //转换为分钟
                //分钟
                int startMine = Integer.valueOf(StringUtils.substringAfter(startSleepTime,":"));

                int startCount = startHour + startMine;
                int currD = startCount+((progress==0?0:progress-1) );   //当前的分钟
                //转换成时：分
                int hour = (int) Math.floor(currD/60);
                if(hour >= 24)
                    hour = hour-24;
                int mine = currD % 60;

                detailCusSleepView.setChooseTxt((hour==0?"00":(hour<10?"0"+hour:hour))+":"+(mine==0?"00":(mine<10?"0"+mine:mine))+"");
                detailCusSleepView.setSeekBarSchdue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                detailCusSleepView.setSeekBarShow(true,0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @OnClick({R.id.commentB30BackImg,R.id.commentB30ShareImg,
            R.id.sleepCurrDateLeft, R.id.sleepCurrDateRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享

                break;
            case R.id.sleepCurrDateLeft:    //上一天
                changeDayData(true);
                break;
            case R.id.sleepCurrDateRight:   //下一天
                changeDayData(false);
                break;
        }
    }

    /**
     * 根据日期切换数据
     */
    private void changeDayData(boolean left) {

        String date = WatchUtils.obtainAroundDate(currDay, left);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        findSleepFromDb(currDay);
    }

    //从数据库中查询睡眠数据
    private void findSleepFromDb(final String currDay) {
        sleepCurrDateTv.setText(currDay);
        final String bleMac = WatchUtils.getSherpBleMac(this);
        if(WatchUtils.isEmpty(bleMac))
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,String> tmpMap = new HashMap<>();
                List<B30HalfHourDB> w30SleepList = B30HalfHourDao.getInstance().findW30SleepDetail(currDay, bleMac,B30HalfHourDao.TYPE_SLEEP);
                if(w30SleepList == null){
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = tmpMap;
                    handler.sendMessage(message);
                    return;
                }
                //解析所需数据
                String sleepStr = w30SleepList.get(0).getOriginData();
                Map<String,String> allMap = gson.fromJson(sleepStr,Map.class);
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = allMap;
                handler.sendMessage(message);

            }
        }).start();
    }

}
