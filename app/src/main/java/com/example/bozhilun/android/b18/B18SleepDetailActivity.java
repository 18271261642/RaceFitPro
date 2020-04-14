package com.example.bozhilun.android.b18;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18CountSleepBean;
import com.example.bozhilun.android.b18.modle.B18SleepChartBean;
import com.example.bozhilun.android.b30.bean.B30HalfHourDao;
import com.example.bozhilun.android.friend.views.CusFriendBean;
import com.example.bozhilun.android.friend.views.CusFriendSleepView;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2019/11/13
 */
public class B18SleepDetailActivity extends WatchBaseActivity {

    private static final String TAG = "B18SleepDetailActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @BindView(R.id.cusFriendSleepView)
    CusFriendSleepView cusFriendSleepView;

    @BindView(R.id.friendSleepSeekBar)
    SeekBar friendSleepSeekBar;

    @BindView(R.id.friendStartSleepTv)
    TextView friendStartSleepTv;

    @BindView(R.id.friendEndSleepTv)
    TextView friendEndSleepTv;


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

    private String currDay = WatchUtils.getCurrentDate();

    List<CusFriendBean> cusFriendBeanList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frend_sleep_activity);
        ButterKnife.bind(this);


        initViews();

        switchDaySleep(currDay);

    }

    //选择日期
    private void switchDaySleep(String dayStr) {
        sleepCurrDateTv.setText(dayStr);
        String bleMac = MyApp.getInstance().getMacAddress();
        if (bleMac == null)
            return;
        try {

            String sleepStr = B30HalfHourDao.getInstance().findOriginData(bleMac, dayStr, B30HalfHourDao.B16_DETAIL_SLEEP);
            Log.e(TAG, "--------sleepStr=" + sleepStr);
            if (sleepStr == null) {
                cusFriendSleepView.setShowSeekLin(false);
                cusFriendSleepView.setSleepList(new ArrayList<CusFriendBean>());
                showNoDataTv();
                return;
            }
            String countSleepStr = B30HalfHourDao.getInstance().findOriginData(bleMac, dayStr, B30HalfHourDao.B18_COUNT_SLEEP);
            //Log.e(TAG,"--------countSleepStr="+countSleepStr);
            if (countSleepStr == null)
                return;
            B18CountSleepBean b18CountSleepBean = new Gson().fromJson(countSleepStr, B18CountSleepBean.class);

            B18SleepChartBean b18SleepChartBean = new Gson().fromJson(sleepStr, B18SleepChartBean.class);
            showB18SleepView(b18CountSleepBean, b18SleepChartBean);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showB18SleepView(B18CountSleepBean b18CountSleepBean, B18SleepChartBean b18SleepChartBean) {
        try {
            cusFriendBeanList.clear();
            //入睡时间
            friendStartSleepTv.setText((b18CountSleepBean.getStartSleepHour() <= 9 ? 0 + "" + b18CountSleepBean.getStartSleepHour() : b18CountSleepBean.getStartSleepHour()) + ":" + (b18CountSleepBean.getStartSleepMinute() <= 9 ? 0 + "" + b18CountSleepBean.getStartSleepMinute() : b18CountSleepBean.getStartSleepMinute()));
            //
            //睡眠时长  //总的睡眠时间；深睡+浅睡+清醒
            int countTime = b18CountSleepBean.getDeepSleepTime() + b18CountSleepBean.getShallowSleepTime() + b18CountSleepBean.getAwakeTime();
            int sleepHour = countTime / 60;
            int sleepMine = countTime % 60;
            /**
             * 清醒时间
             */

            int sleepDownMine = b18CountSleepBean.getStartSleepHour() * 60 + b18CountSleepBean.getStartSleepMinute();

            //入睡时间+总的睡眠时长，判断是否大于24小时，大于24小时就跨天，否则反之
            int tmpResultMine = countTime + sleepDownMine;

            int sleepUpMine = 1440 >= tmpResultMine ? tmpResultMine : 1440 - tmpResultMine;

            int tmpSleepUpHour = sleepUpMine / 60;

            tmpSleepUpHour = Math.abs(tmpSleepUpHour);

            int tmpSleepUpMine = sleepUpMine % 60;
            tmpSleepUpMine = Math.abs(tmpSleepUpMine);

            String sleepUpTime = (tmpSleepUpHour <= 9 ? "0" + tmpSleepUpHour : tmpSleepUpHour) + ":" + (tmpSleepUpMine <= 9 ? 0 + "" + tmpSleepUpMine : tmpSleepUpMine);

            friendEndSleepTv.setText(sleepUpTime);

            //深睡时长
            detailAllSleepTv.setText(sleepHour + "h" + (sleepMine <= 9 ? 0 + "" + sleepMine : sleepMine + "m"));
            //苏醒次数
            detailAwakeNumTv.setText(b18CountSleepBean.getAwakeCount() + "");
            //入睡时间
            detailStartSleepTv.setText((b18CountSleepBean.getStartSleepHour() <= 9 ? 0 + "" + b18CountSleepBean.getStartSleepHour() : b18CountSleepBean.getStartSleepHour()) + ":" + (b18CountSleepBean.getStartSleepMinute() <= 9 ? 0 + "" + b18CountSleepBean.getStartSleepMinute() : b18CountSleepBean.getStartSleepMinute()));
            //清醒时间
            detailAwakeTimeTv.setText(sleepUpTime);
            //深度睡眠
            int deepTime = b18CountSleepBean.getDeepSleepTime();
            int deepHour = deepTime / 60;
            int deepMine = deepTime % 60;
            detailDeepTv.setText(deepHour + "h" + (deepMine <= 9 ? 0 + "" + deepMine : deepMine + "m"));
            //浅度睡眠
            int lowTime = b18CountSleepBean.getShallowSleepTime();
            int lowHour = lowTime / 60;
            int lowMine = lowTime % 60;
            detailHightSleepTv.setText(lowHour + "h" + (lowMine <= 9 ? 0 + "" + lowMine : lowMine + "m"));

            cusFriendSleepView.setAllSleepTime(b18CountSleepBean.getDeepSleepTime() + b18CountSleepBean.getShallowSleepTime() + b18CountSleepBean.getAwakeTime());

            for (B18SleepChartBean.DetailDataBean dataBean : b18SleepChartBean.getDetailData()) {
                cusFriendBeanList.add(new CusFriendBean(dataBean.getSleepType(), dataBean.getSleepTime()));
            }
            cusFriendBeanList.add(0,new CusFriendBean(1,3));
            cusFriendBeanList.add(new CusFriendBean(1,3));
            cusFriendSleepView.setSleepList(cusFriendBeanList);
            showSeekBarData(sleepDownMine, countTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showSeekBarData(final int sleepDownMine, final int countTime) {
        friendSleepSeekBar.setMax(countTime);
        friendSleepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int currD = sleepDownMine + progress;   //当前的分钟
                //转换成时：分
                int hour = (int) Math.floor(currD / 60);
                if (hour >= 24)
                    hour = hour - 24;
                int mine = currD % 60;

                cusFriendSleepView.setTimeTxt((hour == 0 ? "00" : (hour < 10 ? "0" + hour : hour)) + ":" + (mine == 0 ? "00" : (mine < 10 ? "0" + mine : mine)) + "");
                cusFriendSleepView.setSeekX(progress);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                cusFriendSleepView.setShowSeekLin(true, 0);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void showNoDataTv() {
        friendStartSleepTv.setText("--");

        friendEndSleepTv.setText("--");

        //深睡时长
        detailAllSleepTv.setText("--");
        //苏醒次数
        detailAwakeNumTv.setText("--");
        //入睡时间
        detailStartSleepTv.setText("--");
        //清醒时间
        detailAwakeTimeTv.setText("--");
        detailDeepTv.setText("--");
        detailHightSleepTv.setText("--");
    }


    private void initViews() {


        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
        cusFriendBeanList = new ArrayList<>();
    }


    @OnClick({R.id.sleepCurrDateLeft, R.id.sleepCurrDateRight,
            R.id.commentB30BackImg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.sleepCurrDateLeft:
                changeDayData(true);
                break;
            case R.id.sleepCurrDateRight:
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
        switchDaySleep(currDay);
    }
}
