package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.view.CusWrapListView;
import com.example.bozhilun.android.b31.bpoxy.ShowSpo2DetailActivity;
import com.example.bozhilun.android.b31.bpoxy.uploadSpo2.UploadSpo2Bean;
import com.example.bozhilun.android.b31.bpoxy.util.VpSpo2hUtil;
import com.example.bozhilun.android.b31.hrv.GlossaryDetailActivity;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.example.bozhilun.android.util.OkHttpTool;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.util.Spo2hOriginUtil;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.BREATH;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.BREATHBREAK;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.HEART;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.LOWOXGEN;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.LOWREAMIN;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.OSHAHS;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.OXGEN;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.SLEEP;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.SLEEPBREATHBREAKTIP;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BREATH;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HEART;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_LOWSPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SLEEP;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;

/**
 * 查看好友详细血氧页面
 * Created by Admin
 * Date 2019/8/22
 */
public class FriendSpo2DetailActivity extends WatchBaseActivity {

    private static final String TAG = "FriendSpo2DetailActivit";

    //返回
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    //标题
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    //标题右侧图片
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    //日期
    @BindView(R.id.commArrowDate)
    TextView commArrowDate;


    //血氧图表布局
    @BindView(R.id.block_spo2h)
    LinearLayout blockSpo2h;

    @BindView(R.id.spo2OsahsTv)
    TextView spo2OsahsTv;
    //OSAHS的值
    @BindView(R.id.osahsStatusTv)
    TextView osahsStatusTv;
    //更多，收起
    @BindView(R.id.spo2AnalyMoreTv)
    TextView spo2AnalyMoreTv;

    @BindView(R.id.spo2BreathStopTv)
    TextView spo2BreathStopTv;
    //呼吸暂停的频现时间和次数
    @BindView(R.id.spo2BreathStopListView)
    CusWrapListView spo2BreathStopListView;



    @BindView(R.id.spo2Spo2Tv)
    TextView spo2Spo2Tv;

    //血氧的平均值
    @BindView(R.id.spo2AveValueTv)
    TextView spo2AveValueTv;
    //最高/最低值
    @BindView(R.id.spo2HighestTv)
    TextView spo2HighestTv;

    @BindView(R.id.spo2BreathRateTv)
    TextView spo2BreathRateTv;

    //呼吸率的平均值
    @BindView(R.id.spo2BreathRateAvgTv)
    TextView spo2BreathRateAvgTv;

    //最高，最低值
    @BindView(R.id.spo2BreathRateHighestTv)
    TextView spo2BreathRateHighestTv;

    @BindView(R.id.spo2LowO2Tv)
    TextView spo2LowO2Tv;

    //低氧时间的平均值
    @BindView(R.id.lowO2AveTv)
    TextView lowO2AveTv;

    //低氧时间的最高，最低值
    @BindView(R.id.lowO2HighestTv)
    TextView lowO2HighestTv;

    @BindView(R.id.spo2HeartLoadTv)
    TextView spo2HeartLoadTv;

    //新增负荷平均值
    @BindView(R.id.spo2HeartLoadAvgTv)
    TextView spo2HeartLoadAvgTv;

    //心脏负荷最高最低值
    @BindView(R.id.spo2HeartLoadHighestTv)
    TextView spo2HeartLoadHighestTv;


    @BindView(R.id.spo2SleepActiTv)
    TextView spo2SleepActiTv;

    //睡眠活动的平均值
    @BindView(R.id.spo2SleepAvgTv)
    TextView spo2SleepAvgTv;

    //睡眠活动的最高最低值
    @BindView(R.id.spo2SleepHighestTv)
    TextView spo2SleepHighestTv;

    //《=20安静
    @BindView(R.id.tmpClierTv)
    TextView tmpClierTv;
    //20-50多动
    @BindView(R.id.tmpMuilTv)
    TextView tmpMuilTv;
    //50-80躁动
    @BindView(R.id.tmpMulmulTv)
    TextView tmpMulmulTv;


    @BindView(R.id.spo2AnalyMoreLin)
    LinearLayout spo2AnalyMoreLin;

    @BindView(R.id.spo2LowO2ActivityTv)
    TextView spo2LowO2ActivityTv;

    //低氧唤醒的开关
    @BindView(R.id.spo2DetectToggle)
    ToggleButton spo2DetectToggle;

    @BindView(R.id.spo2CommTv)
    TextView spo2CommTv;

    //图表的布局
    @BindView(R.id.spo2ChartListLayout)
    ConstraintLayout spo2ChartListLayout;
    @BindView(R.id.spo2AnalyResultLin)
    LinearLayout spo2AnalyResultLin;


    //血氧的图表
    @BindView(R.id.block_chartview_spo2h)
    LineChart blockChartviewSpo2h;
    //心脏负荷的图表
    @BindView(R.id.block_chartview_heart)
    LineChart blockChartviewHeart;
    //睡眠活动的图表
    @BindView(R.id.block_chartview_sleep)
    LineChart blockChartviewSleep;
    //呼吸率的图表
    @BindView(R.id.block_chartview_breath)
    LineChart blockChartviewBreath;
    //低氧时间的图表
    @BindView(R.id.block_chartview_lowspo2h)
    LineChart blockChartviewLowspo2h;


    //呼吸暂停的时间和次数
    private List<Map<String, Float>> breathStopList;

    //是否显示图表
    private boolean isShowChart = false;
    //分析结果中更多
    private boolean isResultMore = false;
    private Gson gson = new Gson();

    private String currDay = WatchUtils.getCurrentDate();

    VpSpo2hUtil vpSpo2hUtil;

    //处理血氧数据的工具类
    Spo2hOriginUtil spo2hOriginUtil;

    private BreathStopAdapter breathStopAdapter;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
    List<Spo2hOriginData> spo2List = new ArrayList<>();



    String applicant = null;
    String bleMac = null;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            List<Spo2hOriginData> list = (List<Spo2hOriginData>) msg.obj;
            initSpo2hUtil();
            vpSpo2hUtil.setData(list);
            vpSpo2hUtil.showAllChartView();
            showSpo2Desc(list);

        }
    };





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_bp_oxy_anay_layout);
        ButterKnife.bind(this);


        initViews();

        initTipTv();

        initChartView();


        initSpo2hUtil();

        applicant = getIntent().getStringExtra("applicant");
        bleMac  = getIntent().getStringExtra("friendBleMac");

        Log.e(TAG,"----------applicatint="+applicant+"---="+bleMac);
        findSpo2Data(currDay);

    }



    //获取数据
    private void findSpo2Data(String currDay) {
        commArrowDate.setText(currDay);
        showLoadingDialog("Loading...");
        if(applicant == null || bleMac == null){
            closeLoadingDialog();
            return;
        }

        Map<String,String> pm = new HashMap<>();
        pm.put("userId",applicant);
        pm.put("deviceCode",bleMac);
        pm.put("day",currDay);
        Log.e(TAG,"------pm="+pm.toString());
        OkHttpTool.getInstance().doRequest(SyncDbUrls.downloadBloodOxyUrl(), new Gson().toJson(pm), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"-------获取spo2="+result);
                if(WatchUtils.isEmpty(result))
                    return;
                analysisSpo2Data(result);
            }
        });

    }

    //解析数据
    private void analysisSpo2Data(final String result) {
        spo2List.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(!jsonObject.has("day"))
                        return;
                    String dayStr = jsonObject.getString("day");
                    if(dayStr == null || dayStr.equals("[]")){
                        Message message = handler.obtainMessage();
                        message.what = 1001;
                        message.obj = spo2List;
                        handler.sendMessage(message);
                        return;
                    }

                    List<UploadSpo2Bean> uploadSpo2BeanList = gson.fromJson(dayStr,new TypeToken<List<UploadSpo2Bean>>(){}.getType());
                    for(UploadSpo2Bean ub : uploadSpo2BeanList){

                        Spo2hOriginData spo2hOriginData = new Spo2hOriginData();
                        spo2hOriginData.setAllPackNumner(ub.getAllPackNumner());
                        spo2hOriginData.setCurrentPackNumber(ub.getCurrentPackNumber());
                        spo2hOriginData.setDate(ub.getDate());
                        String timeStr = ub.getMTime();
                        int year = DateTimeUtils.getCurrYear();
                        int month = DateTimeUtils.getCurrMonth(timeStr,"yyyy-MM-dd HH:mm:ss");
                        int day = DateTimeUtils.getCurrDay(timeStr,"yyyy-MM-dd HH:mm:ss");
                        int hour = DateTimeUtils.getStrDate(timeStr,"yyyy-MM-dd HH:mm:ss",0);
                        int mine = DateTimeUtils.getStrDate(timeStr,"yyyy-MM-dd HH:mm:ss",1);
                        int second = DateTimeUtils.getStrDate(timeStr,"yyyy-MM-dd HH:mm:ss",2);
                        TimeData timeData = new TimeData(year,month,day,hour,mine,second,0);
                        spo2hOriginData.setmTime(timeData);
                        spo2hOriginData.setHeartValue(ub.getHeartValue());
                        spo2hOriginData.setSportValue(ub.getSportValue());
                        spo2hOriginData.setOxygenValue(ub.getOxygenValue());
                        spo2hOriginData.setApneaResult(ub.getApneaResult());
                        spo2hOriginData.setHypopnea(ub.getHypopnea());
                        spo2hOriginData.setHypoxiaTime(ub.getHypoxiaTime());
                        spo2hOriginData.setCardiacLoad(ub.getCardiacLoad());
                        spo2hOriginData.sethRVariation(ub.getHrVariation());
                        spo2hOriginData.setStepValue(ub.getStepValue());
                        spo2hOriginData.setRespirationRate(ub.getRespirationRate());
                        spo2hOriginData.setTemp1(ub.getTemp1());

                        spo2List.add(spo2hOriginData);

                    }

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = spo2List;
                    handler.sendMessage(message);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        commArrowDate.setText(WatchUtils.getCurrentDate());
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.data));
        commentB30ShareImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lin_img));
        spo2DetectToggle.setVisibility(View.INVISIBLE);

        //≤20(安静)
        tmpClierTv.setText("≤20(" + getResources().getString(R.string.vpspo2h_state_calm) + ")");
        //20-50(多动)
        tmpMuilTv.setText("20-50(" + getResources().getString(R.string.vpspo2h_state_mulsport) + ")");
        //50-80(躁动)
        tmpMulmulTv.setText("50-80(" + getResources().getString(R.string.vpspo2h_state_mulmulsport) + ")");

        //默认显示分析结果的布局
        spo2AnalyResultLin.setVisibility(View.VISIBLE);
        spo2ChartListLayout.setVisibility(View.GONE);

        breathStopList = new ArrayList<>();
        breathStopAdapter = new BreathStopAdapter(breathStopList);
        spo2BreathStopListView.setAdapter(breathStopAdapter);

    }



    private void showSpo2Desc(List<Spo2hOriginData> list) {
        breathStopList.clear();
        spo2hOriginUtil = new Spo2hOriginUtil(list);
        //OSAHS程度
        osahsStatusTv.setText(vpSpo2hUtil.getOsahs(FriendSpo2DetailActivity.this));
        List<Map<String, Float>> tmpLt = spo2hOriginUtil.getApneaList();
        if (tmpLt == null || tmpLt.isEmpty()) {
            tmpLt = new ArrayList<>();
            Map<String, Float> tmpMap = new HashMap<>();
            tmpMap.put("time", 0f);
            tmpMap.put("value", 0f);
            breathStopList.add(tmpMap);
        }
        breathStopList.addAll(tmpLt);
        breathStopAdapter.notifyDataSetChanged();


        //获取低氧数据[最大，最小，平均]       *参考：取低氧最大值，大于20，则显示偏高，其他显示正常
        int[] onedayDataArrLowSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_LOWSPO2H);
        //Log.e(TAG, "showLowSpo2h [最大，最小，平均]: " + Arrays.toString(onedayDataArrLowSpo2h));
        lowO2AveTv.setText(onedayDataArrLowSpo2h[2] + "");
        lowO2HighestTv.setText(onedayDataArrLowSpo2h[0] + "");


        //获取呼吸率数据[最大，最小，平均]     *参考：取呼吸率最大值，低于18，则显示偏低，其他显示正常
        int[] onedayDataArrLowBreath = spo2hOriginUtil.getOnedayDataArr(TYPE_BREATH);
        //Log.e(TAG, "showBreath [最大，最小，平均]: " + Arrays.toString(onedayDataArrLowBreath));
        spo2BreathRateAvgTv.setText(onedayDataArrLowBreath[2] + "");
        spo2BreathRateHighestTv.setText(onedayDataArrLowBreath[0] + "");


        //获取睡眠数据[最大，最小，平均]       *参考：取睡眠活动荷最大值，大于70，则显示偏高，其他显示正常
        int[] onedayDataArrSleep = spo2hOriginUtil.getOnedayDataArr(TYPE_SLEEP);
        //Log.e(TAG, "showSleep [最大，最小，平均]: " + Arrays.toString(onedayDataArrSleep));
        spo2SleepHighestTv.setText(onedayDataArrSleep[0] + "");
        spo2SleepAvgTv.setText(onedayDataArrSleep[2] + "");


        //获取心脏负荷数据[最大，最小，平均]   *参考：取心脏负荷最大值，大于40，则显示偏高，其他显示正常
        int[] onedayDataArrHeart = spo2hOriginUtil.getOnedayDataArr(TYPE_HEART);
        //Log.e(TAG, "showHeartView [最大，最小，平均]: " + Arrays.toString(onedayDataArrHeart));
        spo2HeartLoadAvgTv.setText(onedayDataArrHeart[2] + "");
        spo2HeartLoadHighestTv.setText(onedayDataArrHeart[0] + "");


        //获取血氧数据[最大，最小，平均]       *参考：取血氧最小值，低于95，则显示偏低，其他显示正常
        int[] onedayDataArrSpo2h = spo2hOriginUtil.getOnedayDataArr(TYPE_SPO2H);
        //Log.e(TAG, "showSpo2hView [最大，最小，平均]: " + Arrays.toString(onedayDataArrSpo2h));
        spo2AveValueTv.setText(onedayDataArrSpo2h[2] + "");
        spo2HighestTv.setText(onedayDataArrSpo2h[0] + "");

    }


    private void initTipTv() {
        TextView tipSpo2h = (TextView) findViewById(R.id.block_bottom_tip_spo2h);
        TextView tipHeart = (TextView) findViewById(R.id.block_bottom_tip_heart);
        TextView tipSleep = (TextView) findViewById(R.id.block_bottom_tip_sleep);
        TextView tipBeath = (TextView) findViewById(R.id.block_bottom_tip_beath);
        TextView tipLowsp = (TextView) findViewById(R.id.block_bottom_tip_lowspo2h);

        String stateNormal = getResources().getString(R.string.vpspo2h_state_normal);
        String stateLittle = getResources().getString(R.string.vpspo2h_state_little);
        String stateCalm = getResources().getString(R.string.vpspo2h_state_calm);
        String stateError = getResources().getString(R.string.vpspo2h_state_error);
        String stateMulSport = getResources().getString(R.string.vpspo2h_state_mulsport);
        String stateMulMulSport = getResources().getString(R.string.vpspo2h_state_mulmulsport);

        tipSpo2h.setText("[95-99]" + stateNormal);
        tipHeart.setText("[0-20]" + stateLittle + "\t\t[21-40]" + stateNormal + "\t\t[≥41]" + stateError);
        tipSleep.setText("[0-20]" + stateCalm + "\t\t[21-50]" + stateMulSport + "\t\t[51-80]" + stateMulMulSport);
        tipBeath.setText("[0-26]" + stateNormal + "\t\t[27-50]" + stateError);
        tipLowsp.setText("[0-20]" + stateNormal + "\t\t[21-300]" + stateError);
    }


    private void initChartView() {
        blockChartviewSpo2h = (LineChart) findViewById(R.id.block_chartview_spo2h);
        blockChartviewHeart = (LineChart) findViewById(R.id.block_chartview_heart);
        blockChartviewSleep = (LineChart) findViewById(R.id.block_chartview_sleep);
        blockChartviewBreath = (LineChart) findViewById(R.id.block_chartview_breath);
        blockChartviewLowspo2h = (LineChart) findViewById(R.id.block_chartview_lowspo2h);
    }


    /**
     * 设置相关属性
     */
    private void initSpo2hUtil() {
        vpSpo2hUtil = new VpSpo2hUtil();
        vpSpo2hUtil.setLinearChart(blockChartviewSpo2h, blockChartviewHeart,
                blockChartviewSleep, blockChartviewBreath, blockChartviewLowspo2h);
        vpSpo2hUtil.setMarkView(getApplicationContext(), R.layout.vpspo2h_markerview);
        vpSpo2hUtil.setModelIs24(false);
    }


    @OnClick({R.id.commentB30ShareImg, R.id.spo2AnalyMoreTv,
            R.id.commentB30BackImg, R.id.commArrowLeft,
            R.id.commArrowRight, R.id.spo2OsahsTv,
            R.id.spo2BreathStopTv,
            R.id.spo2Spo2Tv, R.id.spo2BreathRateTv,
            R.id.spo2LowO2Tv, R.id.spo2HeartLoadTv,
            R.id.spo2SleepActiTv, R.id.spo2LowO2ActivityTv,
            R.id.spo2CommTv, R.id.block_spo2h, R.id.block_heart,
            R.id.block_sleep, R.id.block_breath, R.id.block_lowspo2h})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:  //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                if (isShowChart) {
                    isShowChart = false;
                    commentB30ShareImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lin_img));
                    spo2AnalyResultLin.setVisibility(View.VISIBLE);
                    spo2ChartListLayout.setVisibility(View.GONE);
                } else {
                    isShowChart = true;
                    commentB30ShareImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_chart_img));
                    spo2AnalyResultLin.setVisibility(View.GONE);
                    spo2ChartListLayout.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.spo2AnalyMoreTv:  //更多的
                if (isResultMore) {
                    isResultMore = false;
                    spo2AnalyMoreTv.setText(getResources().getString(R.string.more));
                    spo2AnalyMoreLin.setVisibility(View.GONE);
                } else {
                    isResultMore = true;
                    spo2AnalyMoreTv.setText(getResources().getString(R.string.pack_up));
                    spo2AnalyMoreLin.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.commArrowLeft:    //前一天
                changeCurrDay(true);
                break;
            case R.id.commArrowRight:   //后一天
                changeCurrDay(false);
                break;
            case R.id.spo2OsahsTv:  //OSAHS
                startSpo2Desc(OSHAHS.getValue());
                break;
            case R.id.spo2BreathStopTv: //呼吸暂停
                startSpo2Desc(BREATHBREAK.getValue());
                break;
            case R.id.spo2Spo2Tv:   //血氧
                startSpo2Desc(OXGEN.getValue());
                break;
            case R.id.spo2BreathRateTv: //呼吸率
                startSpo2Desc(BREATH.getValue());
                break;
            case R.id.spo2LowO2Tv:  //低氧时间
                startSpo2Desc(LOWOXGEN.getValue());
                break;
            case R.id.spo2HeartLoadTv:  //心脏负荷
                startSpo2Desc(HEART.getValue());
                break;
            case R.id.spo2SleepActiTv:   //睡眠活动
                startSpo2Desc(SLEEP.getValue());
                break;
            case R.id.spo2LowO2ActivityTv:  //低氧唤醒
                startSpo2Desc(LOWREAMIN.getValue());
                break;
            case R.id.spo2CommTv:   //暂停呼吸常识
                startSpo2Desc(SLEEPBREATHBREAKTIP.getValue());
                break;

            case R.id.block_spo2h:  //血氧
                startActivity(ShowSpo2DetailActivity.class, new String[]{"spo2_tag", "title", Constant.DETAIL_DATE},
                        new String[]{"0", getResources().getString(R.string.vpspo2h_spo2h), currDay});
                break;
            case R.id.block_heart:  //心脏负荷
                startActivity(ShowSpo2DetailActivity.class, new String[]{"spo2_tag", "title", Constant.DETAIL_DATE},
                        new String[]{"1", getResources().getString(R.string.vpspo2h_toptitle_heart), currDay});
                break;
            case R.id.block_sleep:      //睡眠活动
                startActivity(ShowSpo2DetailActivity.class, new String[]{"spo2_tag", "title", Constant.DETAIL_DATE},
                        new String[]{"2", getResources().getString(R.string.vpspo2h_toptitle_sleep), currDay});
                break;
            case R.id.block_breath:     //呼吸率
                startActivity(ShowSpo2DetailActivity.class, new String[]{"spo2_tag", "title", Constant.DETAIL_DATE},
                        new String[]{"3", getResources().getString(R.string.vpspo2h_toptitle_breath), currDay});
                break;
            case R.id.block_lowspo2h:   //低氧时间
                startActivity(ShowSpo2DetailActivity.class, new String[]{"spo2_tag", "title", Constant.DETAIL_DATE},
                        new String[]{"4", getResources().getString(R.string.vpspo2h_toptitle_lowspo2h), currDay});
                break;
        }
    }



    private void changeCurrDay(boolean isDay) {
        String date = WatchUtils.obtainAroundDate(currDay, isDay);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        findSpo2Data(currDay);
    }



    private void startSpo2Desc(int value) {
        Intent intent = new Intent(FriendSpo2DetailActivity.this, GlossaryDetailActivity.class);
        intent.putExtra("type", value);
        startActivity(intent);

    }


    //显示呼吸暂停的列表数据
    class BreathStopAdapter extends BaseAdapter {

        List<Map<String, Float>> mapList;
        private LayoutInflater layoutInflater;

        public BreathStopAdapter(List<Map<String, Float>> mapList) {
            this.mapList = mapList;
            layoutInflater = LayoutInflater.from(FriendSpo2DetailActivity.this);
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int position) {
            return mapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BreathStopAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_spo2_breath_stop_layout, parent, false);
                holder = new BreathStopAdapter.ViewHolder();
                holder.timeTv = convertView.findViewById(R.id.itemBreathStopTimeTv);
                holder.timesTv = convertView.findViewById(R.id.itemBreathStopTimesTv);
                convertView.setTag(holder);
            } else {
                holder = (BreathStopAdapter.ViewHolder) convertView.getTag();
            }

            float time = mapList.get(position).get("time");
            float value = mapList.get(position).get("value");
            int intTime = (int) time;
            if (intTime == 0) {
                holder.timeTv.setText("--");
                holder.timesTv.setText("--");
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#");    //保留两位小数
                //小时
                int hour = (int) Math.floor(intTime / 60);
                //分钟
                int mine = intTime % 60;
                holder.timeTv.setText("0" + hour + ":" + (mine==0?"0"+mine:(mine<10?"0"+mine:mine)));
                holder.timesTv.setText(decimalFormat.format(value) + "");
            }

            return convertView;
        }


        class ViewHolder {
            TextView timeTv, timesTv;
        }
    }
}
