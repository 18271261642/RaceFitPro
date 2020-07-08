package com.example.bozhilun.android.b31.bpoxy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.view.CusWrapListView;
import com.example.bozhilun.android.b31.bpoxy.util.VpSpo2hUtil;
import com.example.bozhilun.android.b31.hrv.GlossaryDetailActivity;
import com.example.bozhilun.android.b31.model.B31Spo2hBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Constant;
import com.example.bozhilun.android.view.DateSelectDialogView;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.util.Spo2hOriginUtil;
import org.litepal.LitePal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * B31的血氧分析页面
 * Created by Admin
 * Date 2018/12/20
 */
public class B31BpOxyAnysisActivity extends WatchBaseActivity {

    private static final String TAG = "B31BpOxyAnysisActivity";

    //血氧平均值
    @BindView(R.id.spo2AveValueTv)
    TextView spo2AveValueTv;
    //血氧最高最低值
    @BindView(R.id.spo2HighestTv)
    TextView spo2HighestTv;
    //呼吸率的平均值
    @BindView(R.id.spo2BreathRateAvgTv)
    TextView spo2BreathRateAvgTv;
    //呼吸率的最高值
    @BindView(R.id.spo2BreathRateHighestTv)
    TextView spo2BreathRateHighestTv;
    //低氧时间的平均值
    @BindView(R.id.lowO2AveTv)
    TextView lowO2AveTv;
    //低氧时间的最高值
    @BindView(R.id.lowO2HighestTv)
    TextView lowO2HighestTv;
    //心脏负荷平均值
    @BindView(R.id.spo2HeartLoadAvgTv)
    TextView spo2HeartLoadAvgTv;
    //心脏负荷的最高最低值
    @BindView(R.id.spo2HeartLoadHighestTv)
    TextView spo2HeartLoadHighestTv;
    //睡眠活动的平均值
    @BindView(R.id.spo2SleepAvgTv)
    TextView spo2SleepAvgTv;
    //睡眠活动的最高最低值
    @BindView(R.id.spo2SleepHighestTv)
    TextView spo2SleepHighestTv;
    //呼吸暂停的时间和次数
    @BindView(R.id.spo2BreathStopListView)
    CusWrapListView spo2BreathStopListView;
    @BindView(R.id.osahsStatusTv)
    TextView osahsStatusTv;
    @BindView(R.id.tmpClierTv)
    TextView tmpClierTv;
    @BindView(R.id.tmpMuilTv)
    TextView tmpMuilTv;
    @BindView(R.id.tmpMulmulTv)
    TextView tmpMulmulTv;
    @BindView(R.id.spo2DetectToggle)
    ToggleButton spo2DetectToggle;


    public static void startAndParams(Context context, String date) {
        Intent intent = new Intent(context, B31BpOxyAnysisActivity.class);
        intent.putExtra(Constant.DETAIL_DATE, date);
        context.startActivity(intent);
    }


    LineChart mChartViewSpo2h;
    LineChart mChartViewHeart;
    LineChart mChartViewSleep;
    LineChart mChartViewBreath;
    LineChart mChartViewLowspo2h;


    VpSpo2hUtil vpSpo2hUtil;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.spo2AnalyMoreTv)
    TextView spo2AnalyMoreTv;
    //分析结果 更多的布局
    @BindView(R.id.spo2AnalyMoreLin)
    LinearLayout spo2AnalyMoreLin;
    //分享结果的布局
    @BindView(R.id.spo2AnalyResultLin)
    LinearLayout spo2AnalyResultLin;
    //图表的布局
    @BindView(R.id.spo2ChartListLayout)
    ConstraintLayout spo2ChartListLayout;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.commArrowDate)
    TextView commArrowDate;


    private List<Spo2hOriginData> list = new ArrayList<>();

    //呼吸暂停的时间和次数
    private List<Map<String, Float>> breathStopList;

    //是否显示图表
    private boolean isShowChart = false;
    //分析结果中更多
    private boolean isResultMore = false;

    private Gson gson = new Gson();

    private String currDay;

    //处理血氧数据的工具类
    Spo2hOriginUtil spo2hOriginUtil;

    private BreathStopAdapter breathStopAdapter;



    private DateSelectDialogView dateSelectDialogView;




    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();

            if (msg.what == 1001) {
                initSpo2hUtil();
                vpSpo2hUtil.setData(list);
                vpSpo2hUtil.showAllChartView();
                showSpo2Desc(list);
                Log.e(TAG, "-----");

            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_bp_oxy_anay_layout);
        ButterKnife.bind(this);


        initViews();

        currDay = getIntent().getStringExtra(Constant.DETAIL_DATE);
        initTipTv();
        initChartView();
        initSpo2hUtil();
        readSpo2Data(currDay);
        readSpo2Detect();



    }

    //读取是否提醒的状态
    private void readSpo2Detect() {
        if(MyCommandManager.DEVICENAME == null){
            spo2DetectToggle.setTextOff(getResources().getString(R.string.disconnted));
            return;
        }
        MyApp.getInstance().getVpOperateManager().readCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                spo2DetectToggle.setChecked(customSettingData.getLowSpo2hRemain() == EFunctionStatus.SUPPORT_OPEN);
            }
        });

    }

    private void initViews() {
        commArrowDate.setText(WatchUtils.getCurrentDate());
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.vpspo2h_spo2h) + getResources().getString(R.string.data));
        commentB30ShareImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lin_img));
        spo2DetectToggle.setOnCheckedChangeListener(onCheckedChangeListener);
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

    //读取本地保存的数据
    private void readSpo2Data(final String currDay) {
        try {
            commArrowDate.setText(currDay);
            final String bleMac = WatchUtils.getSherpBleMac(B31BpOxyAnysisActivity.this);
            if(WatchUtils.isEmpty(bleMac))
                return;
            list.clear();
            showLoadingDialog("Loading...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String where = "bleMac = ? and dateStr = ?";
                    List<B31Spo2hBean> spo2hBeanList = LitePal.where(where, bleMac, currDay).find(B31Spo2hBean.class);
                    if (spo2hBeanList == null || spo2hBeanList.isEmpty()) {
                        Message message = handler.obtainMessage();
                        message.what = 1001;
                        message.obj = list;
                        handler.sendMessage(message);
                        return;
                    }
                    if(spo2hBeanList.size()>430){
                        spo2hBeanList = spo2hBeanList.subList(0,420);
                    }
                    //Log.e(TAG,"---22------查询数据="+currDay+spo2hBeanList.size());
                    for (B31Spo2hBean hBean : spo2hBeanList) {
                        //Log.e(TAG,"------xueyang---走到这里来了="+hBean.toString());
                        list.add(gson.fromJson(hBean.getSpo2hOriginData(), Spo2hOriginData.class));
                    }

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = list;
                    handler.sendMessage(message);

                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        mChartViewSpo2h = (LineChart) findViewById(R.id.block_chartview_spo2h);
        mChartViewHeart = (LineChart) findViewById(R.id.block_chartview_heart);
        mChartViewSleep = (LineChart) findViewById(R.id.block_chartview_sleep);
        mChartViewBreath = (LineChart) findViewById(R.id.block_chartview_breath);
        mChartViewLowspo2h = (LineChart) findViewById(R.id.block_chartview_lowspo2h);
    }

    /**
     * 设置相关属性
     */
    private void initSpo2hUtil() {
        vpSpo2hUtil = new VpSpo2hUtil();
        vpSpo2hUtil.setLinearChart(mChartViewSpo2h, mChartViewHeart,
                mChartViewSleep, mChartViewBreath, mChartViewLowspo2h);
        vpSpo2hUtil.setMarkView(getApplicationContext(), R.layout.vpspo2h_markerview);
        vpSpo2hUtil.setModelIs24(false);
    }


    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


    @OnClick({R.id.commentB30ShareImg, R.id.spo2AnalyMoreTv,
            R.id.commentB30BackImg, R.id.commArrowLeft,
            R.id.commArrowRight, R.id.spo2OsahsTv,
            R.id.spo2BreathStopTv,
            R.id.spo2Spo2Tv, R.id.spo2BreathRateTv,
            R.id.spo2LowO2Tv, R.id.spo2HeartLoadTv,
            R.id.spo2SleepActiTv, R.id.spo2LowO2ActivityTv,
            R.id.spo2CommTv, R.id.block_spo2h, R.id.block_heart,
            R.id.block_sleep, R.id.block_breath,
            R.id.block_lowspo2h, R.id.commArrowDate})
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
//                if (isResultMore) {
//                    isResultMore = false;
//                    spo2AnalyMoreTv.setText(getResources().getString(R.string.more));
//                    spo2AnalyMoreLin.setVisibility(View.GONE);
//                } else {
//                    isResultMore = true;
//                    spo2AnalyMoreTv.setText(getResources().getString(R.string.pack_up));
//                    spo2AnalyMoreLin.setVisibility(View.VISIBLE);
//
//                }
                break;
            case R.id.commArrowLeft:    //前一天
                changeCurrDay(true);
                break;
            case R.id.commArrowRight:   //后一天
                changeCurrDay(false);
                break;
            case R.id.commArrowDate:    //日期选择
                chooseDate();
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

    private void chooseDate() {
        dateSelectDialogView = new DateSelectDialogView(this);
        dateSelectDialogView.show();
        dateSelectDialogView.setOnDateSelectListener(new DateSelectDialogView.OnDateSelectListener() {
            @Override
            public void selectDateStr(String str) {
                dateSelectDialogView.dismiss();
                currDay = str;
                readSpo2Data(str);
            }
        });
    }


    private void showSpo2Desc(List<Spo2hOriginData> list) {
        breathStopList.clear();
        spo2hOriginUtil = new Spo2hOriginUtil(list);
        //OSAHS程度
        osahsStatusTv.setText(vpSpo2hUtil.getOsahs(B31BpOxyAnysisActivity.this));
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


    private void startSpo2Desc(int value) {
        // startActivity(ShowSpo2DescActivity.class, new String[]{"spo2_desc"}, new String[]{desc});
        Intent intent = new Intent(B31BpOxyAnysisActivity.this, GlossaryDetailActivity.class);
        intent.putExtra("type", value);
        startActivity(intent);

    }


    private void changeCurrDay(boolean isDay) {
        String date = WatchUtils.obtainAroundDate(currDay, isDay);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        readSpo2Data(currDay);

    }


    //显示呼吸暂停的列表数据
    class BreathStopAdapter extends BaseAdapter {

        List<Map<String, Float>> mapList;
        private LayoutInflater layoutInflater;

        public BreathStopAdapter(List<Map<String, Float>> mapList) {
            this.mapList = mapList;
            layoutInflater = LayoutInflater.from(B31BpOxyAnysisActivity.this);
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
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_spo2_breath_stop_layout, parent, false);
                holder = new ViewHolder();
                holder.timeTv = convertView.findViewById(R.id.itemBreathStopTimeTv);
                holder.timesTv = convertView.findViewById(R.id.itemBreathStopTimesTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
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



    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed())return;
            if(buttonView.getId() == R.id.spo2DetectToggle){
                setSwitchCheck(isChecked);
                spo2DetectToggle.setChecked(isChecked);
            }
        }
    };

    //开关设置
    private void setSwitchCheck(boolean isSpo2LowRemind) {
        showLoadingDialog("loading...");

        //运动过量提醒 B31不支持
        EFunctionStatus isOpenSportRemain = EFunctionStatus.UNSUPPORT;
        //血压/心率播报 B31不支持
        EFunctionStatus isOpenVoiceBpHeart = EFunctionStatus.UNSUPPORT;
        //查找手表  B31不支持
        EFunctionStatus isOpenFindPhoneUI = EFunctionStatus.UNSUPPORT;
        //秒表功能  支持
        EFunctionStatus isOpenStopWatch;
        //低压报警 支持
        EFunctionStatus isOpenSpo2hLowRemind ;//= EFunctionStatus.SUPPORT_OPEN;
        //肤色功能 支持
        EFunctionStatus isOpenWearDetectSkin = EFunctionStatus.SUPPORT_OPEN;

        //自动接听来电 不支持
        EFunctionStatus isOpenAutoInCall = EFunctionStatus.UNSUPPORT;
        //自动检测HRV 支持
        EFunctionStatus isOpenAutoHRV = EFunctionStatus.SUPPORT_OPEN;
        //断连提醒 支持
        EFunctionStatus isOpenDisconnectRemind;
        //SOS  不支持
        EFunctionStatus isOpenSOS = EFunctionStatus.UNSUPPORT;


        //保存的状态
        boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.IS24Hour, true);//是否为24小时制
        boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoHeart, true);//自动测量心率
        boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISAutoBp, true);//自动测量血压
        boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSecondwatch, true);//秒表
        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWearcheck, true);//佩戴
        boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISFindPhone, true);//查找手机
        boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);//来电
        boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, true);//断开连接提醒
        boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos


        //秒表功能
        if(isSecondwatch){
            isOpenStopWatch = EFunctionStatus.SUPPORT_OPEN;
        }else{
            isOpenStopWatch = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断连提醒
        if(isDisconn){
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_OPEN;
        }else{
            isOpenDisconnectRemind = EFunctionStatus.SUPPORT_CLOSE;
        }

        //低氧报警
        if(isSpo2LowRemind){
            isOpenSpo2hLowRemind = EFunctionStatus.SUPPORT_OPEN;
        }else{
            isOpenSpo2hLowRemind = EFunctionStatus.SUPPORT_CLOSE;
        }


        CustomSetting customSetting = new CustomSetting(true,isSystem,is24Hour,isAutomaticHeart,
                false,isOpenSportRemain,isOpenVoiceBpHeart,isOpenFindPhoneUI,isOpenStopWatch,isOpenSpo2hLowRemind,
                isOpenWearDetectSkin,isOpenAutoInCall,isOpenAutoHRV,isOpenDisconnectRemind,isOpenSOS);
        //Log.e(TAG,"--------customSetting="+customSetting.toString());

        MyApp.getInstance().getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                closeLoadingDialog();
                //Log.e(TAG,"----customSettingData="+customSettingData.toString());
            }
        }, customSetting);


    }


}
