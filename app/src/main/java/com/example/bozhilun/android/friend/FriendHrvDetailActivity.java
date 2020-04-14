package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.bpoxy.Spo2SecondDialogView;
import com.example.bozhilun.android.b31.bpoxy.markview.SPMarkerView;
import com.example.bozhilun.android.b31.bpoxy.uploadSpo2.UploadHrvBean;
import com.example.bozhilun.android.b31.bpoxy.util.ChartViewUtil;
import com.example.bozhilun.android.b31.bpoxy.util.HrvDescripterUtil;
import com.example.bozhilun.android.b31.hrv.B31HrvDetailActivity;
import com.example.bozhilun.android.b31.hrv.HrvDescDialogView;
import com.example.bozhilun.android.b31.hrv.HrvListDataAdapter;
import com.example.bozhilun.android.commdbserver.SyncDbUrls;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.OkHttpTool;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.util.HrvScoreUtil;
import com.veepoo.protocol.view.LorenzChartView;
import com.vp.cso.hrvreport.JNIChange;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MAX_HRV;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIDDLE_HRV;
import static com.example.bozhilun.android.b31.bpoxy.enums.Constants.CHART_MIN_HRV;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HRV;

/**
 * 查看好友详细的HRV数据
 * Created by Admin
 * Date 2019/8/22
 */
public class FriendHrvDetailActivity extends WatchBaseActivity {

    private static final String TAG = "FriendHrvDetailActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commArrowDate)
    TextView commArrowDate;
    @BindView(R.id.b31HrvDetailTopChart)
    LineChart b31HrvDetailTopChart;
    @BindView(R.id.hrvDetailHeartSocreTv)
    TextView hrvDetailHeartSocreTv;
    @BindView(R.id.hrvReferBackImg)
    ImageView hrvReferBackImg;
    @BindView(R.id.hrvReferMarkviewImg)
    ImageView hrvReferMarkviewImg;
    @BindView(R.id.herLerzeoTv)
    TextView herLerzeoTv;
    @BindView(R.id.herDataTv)
    TextView herDataTv;
    @BindView(R.id.lorezChartView)
    LorenzChartView lorezChartView;
    @BindView(R.id.hrvNoLeroDescTv)
    TextView hrvNoLeroDescTv;
    @BindView(R.id.lorenz_list_descripe)
    ListView lorenzListDescripe;
    @BindView(R.id.hrvLerozenLin)
    LinearLayout hrvLerozenLin;
    @BindView(R.id.hrvDataRrecyclerView)
    RecyclerView hrvDataRrecyclerView;
    @BindView(R.id.hrvListDataConLy)
    ConstraintLayout hrvListDataConLy;
    @BindView(R.id.commB31TitleLayout)
    Toolbar relaLayoutTitle;

    //markview
    SPMarkerView mMarkviewHrv;

    //适配器
    private HrvListDataAdapter hrvListDataAdapter;

    private HrvDescDialogView hrvDescDialogView;
    private List<Map<String, Float>> listMap;

    JNIChange mJNIChange;

    private Spo2SecondDialogView spo2SecondDialogView;
    HRVOriginUtil mHrvOriginUtil;

    private String currDay = WatchUtils.getCurrentDate();
    private List<HRVOriginData> resultHrvList = new ArrayList<>();


    String friendUserId = null;
    String friendBleMac = null;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            switch (msg.what) {
                case 1001:
                    List<HRVOriginData> resultHrv = (List<HRVOriginData>) msg.obj;
                    initLinChartData(resultHrv);
                    lorezChartView.updateData(resultHrv);
                    //分析报告界面
                    showResult(resultHrv);
                    break;
                case 1002:
                    initLinChartData(resultHrvList);
                    lorezChartView.updateData(resultHrvList);
                    listMap.clear();
                    hrvListDataAdapter.notifyDataSetChanged();
                    //分析报告界面
                    showResult(resultHrvList);
                    break;
            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b31_hrv_detail);
        ButterKnife.bind(this);


        initViews();

        initAdapter();

        friendUserId = getIntent().getStringExtra("applicant");
        friendBleMac = getIntent().getStringExtra("friendBleMac");

        requestServerData(currDay);

    }


    //获取数据
    private void requestServerData(String day) {
        commArrowDate.setText(day);
        showLoadingDialog("Loading...");
        if(friendUserId == null || friendBleMac == null){
            closeLoadingDialog();
            return;
        }

        Map<String,String> pm = new HashMap<>();
        pm.put("userId",friendUserId);
        pm.put("deviceCode",friendBleMac);
        pm.put("day",day);
        Log.e(TAG,"------pm="+pm.toString());
        OkHttpTool.getInstance().doRequest(SyncDbUrls.downloadHrvDetailUrl(), new Gson().toJson(pm), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"-------获取hrv="+result);
                if(WatchUtils.isEmpty(result))
                    return;
                analysisHrvData(result);

            }
        });
    }




    private void analysisHrvData(String result) {
        resultHrvList.clear();
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(!jsonObject.has("list")){
                handler.sendEmptyMessage(1002);
                return;
            }

            String listStr = jsonObject.getString("list");
            if(WatchUtils.isEmpty(listStr) || listStr.equals("[]")){
                handler.sendEmptyMessage(1002);
                return;
            }

            List<UploadHrvBean> upLt = new Gson().fromJson(listStr,new TypeToken<List<UploadHrvBean>>(){}.getType());
            for(UploadHrvBean ub : upLt){
                HRVOriginData hrvOriginData = new HRVOriginData();
                hrvOriginData.setDate(ub.getDate());
                hrvOriginData.setAllCurrentPackNumber(ub.getAllCurrentPackNumber());
                hrvOriginData.setCurrentPackNumber(ub.getCurrentPackNumber());
                hrvOriginData.setTempOne(Integer.valueOf(ub.getTemp1()));
                hrvOriginData.setHrvType(1);
                hrvOriginData.setRate(ub.getHearts().replace(":",","));
                hrvOriginData.setHrvValue(Integer.valueOf(ub.getHrvValue()));
                String timeStr = ub.getMTime();
                int year = DateTimeUtils.getCurrYear();
                int month = DateTimeUtils.getCurrMonth(timeStr,"yyyy-MM-dd HH:mm:ss");
                int day = DateTimeUtils.getCurrDay(timeStr,"yyyy-MM-dd HH:mm:ss");
                int hour = DateTimeUtils.getStrDate(timeStr,"yyyy-MM-dd HH:mm:ss",0);
                int mine = DateTimeUtils.getStrDate(timeStr,"yyyy-MM-dd HH:mm:ss",1);
                int second = DateTimeUtils.getStrDate(timeStr,"yyyy-MM-dd HH:mm:ss",2);
                TimeData timeData = new TimeData(year,month,day,hour,mine,second,0);
                hrvOriginData.setmTime(timeData);
                resultHrvList.add(hrvOriginData);
            }

            Message message = handler.obtainMessage();
            message.obj = resultHrvList;
            message.what = 1001;
            handler.sendMessage(message);


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("HRV");
        relaLayoutTitle.setBackgroundColor(Color.parseColor("#ECA83D"));

        mMarkviewHrv = new SPMarkerView(getApplicationContext(), R.layout.vpspo2h_markerview,
                true, CHART_MIDDLE_HRV, TYPE_HRV);
        clearHrvStyle(0);
        initLorezView();

        updateList(new int[]{0, 11, 32, 23, 14, 0});

    }



    private void showResult(List<HRVOriginData> originHRVList) {
        if (originHRVList == null || originHRVList.isEmpty()) {
            lorenzListDescripe.setVisibility(View.GONE);
            hrvNoLeroDescTv.setVisibility(View.VISIBLE);
            return;
        }
        lorenzListDescripe.setVisibility(View.VISIBLE);
        hrvNoLeroDescTv.setVisibility(View.GONE);

        HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
        double[] lorenData = hrvScoreUtil.getLorenData(originHRVList);
        if (lorenData == null || lorenData.length < 1500) {
            return;
        }
        int[] bufferdata = new int[lorenData.length];
        for (int i = 0; i < bufferdata.length; i++) {
            bufferdata[i] = (int) lorenData[i];
        }
        int[] result = null;
        try {
            result = mJNIChange.hrvAnalysisReport(bufferdata, bufferdata.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            return;
        }
        updateList(result);
    }



    private void initLinChartData(List<HRVOriginData> originHRVList) {
        closeLoadingDialog();
        listMap.clear();

        //心脏健康指数
        HrvScoreUtil hrvScoreUtil = new HrvScoreUtil();
        int heartSocre = hrvScoreUtil.getSocre(originHRVList);
        transHrvMarkImg(heartSocre);
        hrvDetailHeartSocreTv.setText(heartSocre + "");
        //折线图的
        List<HRVOriginData> data0to8 = getMoringData(originHRVList);
        mHrvOriginUtil = new HRVOriginUtil(data0to8);

        List<Map<String, Float>> tenMinuteData = mHrvOriginUtil.getTenMinuteData();
        ChartViewUtil chartViewUtil = new ChartViewUtil(b31HrvDetailTopChart, mMarkviewHrv, true,
                CHART_MAX_HRV, CHART_MIN_HRV, "No Data", TYPE_HRV);
        chartViewUtil.updateChartView(tenMinuteData);
        mMarkviewHrv.setData(tenMinuteData);

        listMap.addAll(tenMinuteData);
        hrvListDataAdapter.notifyDataSetChanged();

    }


    private void transHrvMarkImg(int value) {
        int backImgWidth = hrvReferBackImg.getWidth();//总长度
        int currV = backImgWidth / 10;
        TranslateAnimation translateAnimation = new TranslateAnimation(0,
                value == 0 ? 0 : backImgWidth - 40 - (100 - value) / 10 * currV + (hrvReferMarkviewImg.getWidth() / 2),
                Animation.ABSOLUTE,
                Animation.ABSOLUTE);
        translateAnimation.setDuration(3 * 1000);
        translateAnimation.setFillAfter(true);
        hrvReferMarkviewImg.startAnimation(translateAnimation);


    }

    /**
     * 获取0点-8点之间的数据
     *
     * @param originSpo2hList
     * @return
     */
    @NonNull
    private List<HRVOriginData> getMoringData(List<HRVOriginData> originSpo2hList) {
        List<HRVOriginData> moringData = new ArrayList<>();
        if (originSpo2hList == null || originSpo2hList.isEmpty())
            return moringData;
        for (HRVOriginData HRVOriginData : originSpo2hList) {
            if (HRVOriginData.getmTime().getHMValue() < 8 * 60) {
                moringData.add(HRVOriginData);
            }
        }
        return moringData;
    }

    private void updateList(int[] result) {
        getRepoListData(result).clear();
        setListViewHeightBasedOnChildren(lorenzListDescripe);
        List<Map<String, Object>> repoListData = getRepoListData(result);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, repoListData,
                R.layout.vpspo2h_item_lorendes, new String[]{"title", "info", "level"},
                new int[]{R.id.loren_descripe_title, R.id.loren_descripe_info, R.id.loren_descripe_level});
        lorenzListDescripe.setAdapter(simpleAdapter);
        lorenzListDescripe.setOnItemClickListener(null);
        lorenzListDescripe.setFocusable(false);
    }


    private void initAdapter() {
        mJNIChange = new JNIChange();
        listMap = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hrvDataRrecyclerView.setNestedScrollingEnabled(false);
        hrvDataRrecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        hrvDataRrecyclerView.setLayoutManager(linearLayoutManager);
        hrvListDataAdapter = new HrvListDataAdapter(listMap, FriendHrvDetailActivity.this);
        hrvDataRrecyclerView.setAdapter(hrvListDataAdapter);
        hrvListDataAdapter.setHrvItemClickListener(hrvItemClickListener);

    }


    private void initLorezView() {
        lorezChartView.setTextSize(80);
        lorezChartView.setTextColor(Color.RED);
        lorezChartView.setDotColor(Color.RED);
        lorezChartView.setDotSize(5);
        lorezChartView.setLineWidth(8);
        lorezChartView.setLineColor(Color.RED);
    }



    private void clearHrvStyle(int code) {
        clearAll();
        switch (code) {
            case 0:
                herLerzeoTv.setBackgroundColor(Color.parseColor("#ECA83D"));
                herLerzeoTv.setTextColor(Color.WHITE);
                hrvLerozenLin.setVisibility(View.VISIBLE);
                hrvListDataConLy.setVisibility(View.GONE);
                break;
            case 1:
                herDataTv.setBackgroundColor(Color.parseColor("#ECA83D"));
                herDataTv.setTextColor(Color.WHITE);
                hrvLerozenLin.setVisibility(View.GONE);
                hrvListDataConLy.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void clearAll() {
        herLerzeoTv.setBackgroundColor(Color.WHITE);
        herLerzeoTv.setTextColor(getResources().getColor(R.color.contents_text));
        herDataTv.setBackgroundColor(Color.WHITE);
        herDataTv.setTextColor(getResources().getColor(R.color.contents_text));

    }


    @OnClick({R.id.commentB30BackImg, R.id.commArrowLeft,
            R.id.commArrowRight, R.id.herLerzeoTv,
            R.id.herDataTv, R.id.hrvType1,
            R.id.hrvType2, R.id.hrvType3,
            R.id.hrvType4, R.id.hrvType5,
            R.id.hrvType6, R.id.hrvType7,
            R.id.hrvType8, R.id.hrvType9,
            })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commArrowLeft:    //前一天
                changeCurrDay(true);
                break;
            case R.id.commArrowRight:   //后一天
                changeCurrDay(false);
                break;
            case R.id.herLerzeoTv:
                clearHrvStyle(0);
                break;
            case R.id.herDataTv:
                clearHrvStyle(1);
                break;
            case R.id.hrvType1:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_1),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_1), R.drawable.hrv_gradivew_1_big);
                break;
            case R.id.hrvType2:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_2),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_2), R.drawable.hrv_gradivew_2_big);
                break;
            case R.id.hrvType3:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_3),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_3), R.drawable.hrv_gradivew_3_big);
                break;
            case R.id.hrvType4:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_4),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_4), R.drawable.hrv_gradivew_4_big);
                break;
            case R.id.hrvType5:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_5),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_5), R.drawable.hrv_gradivew_5_big);
                break;
            case R.id.hrvType6:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_6),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_6), R.drawable.hrv_gradivew_6_big);
                break;
            case R.id.hrvType7:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_7),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_7), R.drawable.hrv_gradivew_7_big);
                break;
            case R.id.hrvType8:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_8),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_8), R.drawable.hrv_gradivew_8_big);
                break;
            case R.id.hrvType9:
                showHrvDescDialog(getResources().getString(R.string.vphrv_lorentz_chart_9),
                        getResources().getString(R.string.vphrv_lorentz_chart_des_9), R.drawable.hrv_gradivew_9_big);
                break;
        }
    }


    private void changeCurrDay(boolean isDay) {
        String date = WatchUtils.obtainAroundDate(currDay, isDay);
        if (date.equals(currDay) || date.isEmpty()) {
            return;// 空数据,或者大于今天的数据就别切了
        }
        currDay = date;
        requestServerData(currDay);
    }



    private void showHrvDescDialog(String titleId, String descTxt, int drawable) {
        if (hrvDescDialogView == null) {
            hrvDescDialogView = new HrvDescDialogView(FriendHrvDetailActivity.this);
        } else {
            hrvDescDialogView.cancel();
            hrvDescDialogView = new HrvDescDialogView(FriendHrvDetailActivity.this);
        }

        hrvDescDialogView.show();
        hrvDescDialogView.setHrvDescTitleTxt(titleId);
        hrvDescDialogView.setHrvDescContent(descTxt);
        hrvDescDialogView.setHrvDescImg(getResources().getDrawable(drawable));
        hrvDescDialogView.setHrvDescDialogListener(new HrvDescDialogView.HrvDescDialogListener() {
            @Override
            public void cancleDialog() {
                hrvDescDialogView.dismiss();
            }
        });

    }


    private HrvListDataAdapter.HrvItemClickListener hrvItemClickListener = new HrvListDataAdapter.HrvItemClickListener() {
        @Override
        public void hrvItemClick(int position) {
            if (spo2SecondDialogView == null) {
                spo2SecondDialogView = new Spo2SecondDialogView(FriendHrvDetailActivity.this);
            }
            List<Map<String, Float>> lt = mHrvOriginUtil.getDetailList(listMap.size() - position - 1);
            if (lt == null || lt.size() == 0)
                return;
            spo2SecondDialogView.show();
            spo2SecondDialogView.setSpo2Type(555);
            spo2SecondDialogView.setMapList(lt);
            //spo2SecondDialogView.setHRVUtils(mHrvOriginUtil,listMap.size()-position-1);
        }
    };

    private List<Map<String, Object>> getRepoListData(int[] data) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (data == null || data.length == 0) {
            return list;
        }
        HrvDescripterUtil hrv = new HrvDescripterUtil(getApplicationContext());
        String[] repoTitle = hrv.getRepoTitle();
        for (int i = 1; i < data.length - 1; i++) {
            Map<String, Object> map = new HashMap<>();
            int mapInt = (i + 1) * 100 + +data[i];
            map.put("title", repoTitle[i]);
            map.put("info", hrv.getRepoInfo(mapInt));
            map.put("level", hrv.getLevel(mapInt));
            list.add(map);
        }
        return list;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter;
        listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
