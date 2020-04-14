package com.example.bozhilun.android.siswatch.data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.H8ShareActivity;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.record.RecordHistoryActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.BaseFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by sunjianhua on 2017/11/6.
 */

public class WatchH8DataFragment extends BaseFragment {

    private static final String TAG = "-------WatchH8DataFragment";

    View h8View;
    @BindView(R.id.watch_newh8dataweekChar)
    LineChart watchNewh8dataweekChar;
    Unbinder unbinder;
    List<WatchDataDatyBean> watchDataList;
    //身高
    @BindView(R.id.h8UserHeightTv)
    TextView h8UserHeightTv;
    //体重
    @BindView(R.id.h8UserWeighttTv)
    TextView h8UserWeighttTv;
    //年龄
    @BindView(R.id.h8UserAgetTv)
    TextView h8UserAgetTv;
    //步数显示
    @BindView(R.id.watch_newh8_dataStepTv)
    TextView watchNewh8DataStepTv;
    //里程
    @BindView(R.id.watch_newh8_dataDisTv)
    TextView watchNewh8DataDisTv;
    //卡路里
    @BindView(R.id.watch_newh8_dataKcalTv)
    TextView watchNewh8DataKcalTv;
    //周
    @BindView(R.id.week_newtv1)
    TextView weekNewtv1;
    //月
    @BindView(R.id.month_newtv2)
    TextView monthNewtv2;
    //年
    @BindView(R.id.year_newtv3)
    TextView yearNewtv3;
    @BindView(R.id.week_newview1)
    View weekNewview1;
    @BindView(R.id.month_newview2)
    View monthNewview2;
    @BindView(R.id.year_newview3)
    View yearNewview3;
    @BindView(R.id.watch_newh8_datadatelTv)
    TextView watchNewh8DatadatelTv;
    @BindView(R.id.xTv1)
    TextView xTv1;
    @BindView(R.id.xTv2)
    TextView xTv2;
    @BindView(R.id.xTv3)
    TextView xTv3;
    @BindView(R.id.xTv4)
    TextView xTv4;
    @BindView(R.id.xTv5)
    TextView xTv5;
    @BindView(R.id.xTv6)
    TextView xTv6;
    @BindView(R.id.xTv7)
    TextView xTv7;
    @BindView(R.id.h8UserSexTv)
    TextView h8UserSexTv;
    //x轴的textView布局
    @BindView(R.id.h8_x_lin)
    LinearLayout h8XLin;
    @BindView(R.id.xRb1)
    RadioButton xRb1;
    @BindView(R.id.xRb2)
    RadioButton xRb2;
    @BindView(R.id.xRb3)
    RadioButton xRb3;
    @BindView(R.id.xRb4)
    RadioButton xRb4;
    @BindView(R.id.xRb5)
    RadioButton xRb5;
    @BindView(R.id.xRb6)
    RadioButton xRb6;
    @BindView(R.id.xRb7)
    RadioButton xRb7;
    @BindView(R.id.h8_teset_radio_lin)
    LinearLayout h8TesetRadioLin;
    @BindView(R.id.h8xRg)
    RadioGroup h8xRg;
    @BindView(R.id.newH8XView)
    View newH8XView;

    //标题
    @BindView(R.id.h8_data_titleTv)
    TextView h8DataTitleTv;

    private String weekTag = "week";
    Map<String, Integer> sumMap;
    //日期
    private List<String> dtLis;


    //折线图的数据
    List<Entry> entryList;


    //公制转英制常量
    private double unitCons = 0.6214;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferencesUtils.setParam(getActivity(),"saveH8Time",System.currentTimeMillis() / 1000 + "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        h8View = inflater.inflate(R.layout.fragment_new_h8_data, container, false);
        unbinder = ButterKnife.bind(this, h8View);

        initViews();
        //showTodaySportData();   //显示当天的数据
        watchNewh8DatadatelTv.setText(WatchUtils.getCurrentDate().substring(5, WatchUtils.getCurrentDate().length()));
        //默认显示周的数据
        clearViewBack();
        weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
        return h8View;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //视图显示不显示
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(isVisible){
            getLindataFromServer("week");
        }
    }

    //显示当天的数据
    private void showTodaySportData() {
        //显示今天的步数
        String todayStep = ((String) SharedPreferencesUtils.getParam(getActivity(), "stepsnum", "")).trim();
        String kal = ((String) SharedPreferencesUtils.getParam(getActivity(), "watchkcal", "")).trim();
        String discan = (String) SharedPreferencesUtils.getParam(getActivity(), "watchstepsdistants", "");


        if (!WatchUtils.isEmpty(kal)) {
            watchNewh8DataKcalTv.setText(StringUtils.substringBefore(kal, ".") + getResources().getString(R.string.km_cal));
        }


        watchNewh8DatadatelTv.setText(WatchUtils.getCurrentDate().substring(5, WatchUtils.getCurrentDate().length()));
        //默认显示周的数据
        clearViewBack();
        weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
        weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));

    }

    private void initViews() {
        dtLis = new ArrayList<>();
        watchDataList = new ArrayList<>();
        h8DataTitleTv.setText(getResources().getString(R.string.data));

        entryList = new ArrayList<>();

        h8XLin.setVisibility(View.GONE);
        newH8XView.setVisibility(View.VISIBLE);
        h8TesetRadioLin.setVisibility(View.VISIBLE);
        getUserInfoData();

    }

    //获取用户信息
    private void getUserInfoData() {

        h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
        h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");
        h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");
        h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");


        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener userSub = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                if(WatchUtils.isEmpty(result))
                    return;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("resultCode").equals("001")) {
                        JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                        if (myInfoJsonObject != null) {
                            //身高
                            String uHeight =  myInfoJsonObject.getString("height");
                            //体重
                            String uWeight = myInfoJsonObject.getString("weight");
                            if(WatchUtils.judgeUnit(MyApp.getContext())){
                                h8UserHeightTv.setText(getResources().getString(R.string.height)+":"+uHeight+"cm");
                                h8UserWeighttTv.setText(getResources().getString(R.string.weight)+":"+uWeight+"kg");
                            }else{
                                h8UserHeightTv.setText(getResources().getString(R.string.height)+":"+WatchUtils.obtainHeight(WatchUtils.userHeightConver(uHeight)+""));
                                h8UserWeighttTv.setText(getResources().getString(R.string.weight)+":"+WatchUtils.obtainWeight(uWeight+""));
                            }

                            String birtday = myInfoJsonObject.getString("birthday"); //生日
                            if (!WatchUtils.isEmpty(birtday)) {
                                h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + WatchUtils.getAgeFromBirthTime(birtday));
                            }
                            String sex = myInfoJsonObject.getString("sex");
                            if (sex.equals("M")) {
                                h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + getResources().getString(R.string.sex_nan));
                            } else {
                                h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + getResources().getString(R.string.sex_nv));
                            }

                        } else {
                            h8UserHeightTv.setText(getResources().getString(R.string.height) + ":" + "-");
                            h8UserWeighttTv.setText(getResources().getString(R.string.weight) + ":" + "-");
                            h8UserAgetTv.setText(getResources().getString(R.string.age) + ":" + "-");
                            h8UserSexTv.setText(getResources().getString(R.string.sex) + ":" + "-");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        CommonSubscriber commonSubscriber2 = new CommonSubscriber(userSub, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, url, jsonObj.toString());
    }


    private void getLindataFromServer(final String datatag) {
        showLoadingDialog("loading...");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            jsonParams.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), Commont.BLEMAC));

            if (datatag.equals("week")) { //周
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 7)));
                //结束时间
                jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));
            } else if (datatag.equals("month")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 30)));
                //结束时间
                jsonParams.put("endDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 1)));
            } else if (datatag.equals("year")) {
                //开始时间
                jsonParams.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), 364)));
                //结束时间
                jsonParams.put("endDate", WatchUtils.getCurrentDate());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onNext(String result) {
                closeLoadingDialog();
                //Log.e(TAG, "-----result--11--" + result);
                if (getActivity() != null && !getActivity().isFinishing() && !WatchUtils.isEmpty(result) ) {
                    try {
                        JSONObject jso = new JSONObject(result);
                        String daydata = jso.getString("day");
                        watchDataList.clear();
                        watchDataList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                        }.getType());

                        //Log.e(TAG, "-----watchDataList.seze----" + watchDataList.size() + weekTag);
                        if (datatag.equals("year")) {
                            sumMap = new HashMap<>();
                            int sum = 0;
                            for (int i = 0; i < watchDataList.size(); i++) { //2018-10-17
                                String strDate = watchDataList.get(i).getRtc().substring(2, 7);
                                if (sumMap.get(strDate) != null) {
                                    sum += watchDataList.get(i).getStepNumber();
                                }else{
                                    sum = watchDataList.get(i).getStepNumber();
                                }
                                sumMap.put(strDate, sum);

                            }
                            dtLis.clear();
                            //Log.e(TAG,"------map---"+sumMap.toString());
                            //遍历map
                            for (Map.Entry<String, Integer> entry : sumMap.entrySet()) {
                                dtLis.add(entry.getKey().trim());
                            }
                            //升序排列
                            Collections.sort(dtLis, new Comparator<String>() {
                                @Override
                                public int compare(String s, String t1) {
                                    return s.compareTo(t1);
                                }
                            });
                            showLoadingDialog("loading....");
                            LineChart(watchDataList,12,sumMap);
                        } else if (datatag.equals("week")) {
                            dtLis.clear();
                            Collections.sort(watchDataList, new Comparator<WatchDataDatyBean>() {
                                @Override
                                public int compare(WatchDataDatyBean o1, WatchDataDatyBean o2) {
                                    return o1.getRtc().compareTo(o2.getRtc());
                                }
                            });
                            xRb1.setText(watchDataList.get(0).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb2.setText(watchDataList.get(1).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb3.setText(watchDataList.get(2).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb4.setText(watchDataList.get(3).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb5.setText(watchDataList.get(4).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb6.setText(watchDataList.get(5).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            xRb7.setText(watchDataList.get(6).getRtc().substring(8, watchDataList.get(0).getRtc().length()));
                            //设置昨天默认选中
                            xRb7.setChecked(true);
                            showTopData(6);
                            h8xRg.setOnCheckedChangeListener(new weekTvClickListener());

                            LineChart(watchDataList,watchDataList.size(),new HashMap<String, Integer>());

                        } else { //月
                            dtLis.clear();
                            showLoadingDialog("loading....");
                            LineChart(watchDataList,watchDataList.size(),new HashMap<String, Integer>());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        //Log.e("新数据", "-----maps----" + jsonParams.toString() + "--" + url);
        CommonSubscriber commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonParams.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.week_newtv1, R.id.month_newtv2, R.id.year_newtv3,
            R.id.newh8dataUserLi,
            R.id.h8_dataLinChartImg, R.id.h8_dataShareImg, R.id.h8_data_titleLinImg})
    public void onViewClicked(View view) {
        if(getActivity() == null || getActivity().isFinishing())
            return;
        long currentTime = System.currentTimeMillis() / 1000;
        //Log.e("1111","----currTIme="+currentTime);
        //保存的时间
        String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getActivity(), "saveH8Time", "");
       // Log.e("1111","-----保存的时间="+tmpSaveTime);
        long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) ;
        //Log.e("111","--------间隔="+diffTime);
        switch (view.getId()) {
            case R.id.week_newtv1:  //周
               // Log.e("11111","------1----="+diffTime);
                if(diffTime <2 ){
                    weekNewtv1.setEnabled(false);
                }else{
                    weekNewtv1.setEnabled(true);
                    SharedPreferencesUtils.setParam(getActivity(),"saveH8Time",System.currentTimeMillis() / 1000 + "");
                }
                if(weekNewtv1.isEnabled()){
                    h8XLin.setVisibility(View.GONE);
                    newH8XView.setVisibility(View.VISIBLE);
                    h8TesetRadioLin.setVisibility(View.VISIBLE);
                    watchDataList.clear();
                    entryList.clear();
                    weekTag = "week";
                    clearViewBack();
                    weekNewtv1.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                    weekNewview1.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                    getLindataFromServer("week");
                }

                break;
            case R.id.month_newtv2: //月
                Log.e("11111","------2----="+diffTime);
                if(diffTime <2 ){
                    monthNewtv2.setEnabled(false);
                }else{
                    monthNewtv2.setEnabled(true);
                    SharedPreferencesUtils.setParam(getActivity(),"saveH8Time",System.currentTimeMillis() / 1000 + "");
                }
                if(monthNewtv2.isEnabled()){
                    h8XLin.setVisibility(View.GONE);
                    h8TesetRadioLin.setVisibility(View.GONE);
                    newH8XView.setVisibility(View.GONE);
                    watchDataList.clear();
                    entryList.clear();
                    weekTag = "month";
                    clearViewBack();
                    monthNewtv2.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                    monthNewview2.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                    getLindataFromServer("month");
                }

                break;
            case R.id.year_newtv3:  //年的点击
               Log.e("11111","------3----="+diffTime);
                if(diffTime <2 ){
                    yearNewtv3.setEnabled(false);
                }else{
                    yearNewtv3.setEnabled(true);
                    SharedPreferencesUtils.setParam(getActivity(),"saveH8Time",System.currentTimeMillis() / 1000 + "");
                }
                if(yearNewtv3.isEnabled()){

                    h8XLin.setVisibility(View.GONE);
                    h8TesetRadioLin.setVisibility(View.GONE);
                    newH8XView.setVisibility(View.GONE);
                    watchDataList.clear();
                    entryList.clear();
                    weekTag = "year";
                    clearViewBack();
                    yearNewtv3.setTextColor(ContextCompat.getColor(getActivity(), R.color.new_colorAccent));
                    yearNewview3.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.h8_datareportperiodselected, null));
                    getLindataFromServer("year");
                }
                break;
            case R.id.newh8dataUserLi:  //至个人中心
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.h8_dataLinChartImg:   //至柱状图
                startActivity(new Intent(getActivity(), H8DataLinchartActivity.class));
                break;
            case R.id.h8_data_titleLinImg:  //至列表
                startActivity(new Intent(getActivity(), RecordHistoryActivity.class));
                break;
            case R.id.h8_dataShareImg:  //分享
                startActivity(new Intent(getActivity(),H8ShareActivity.class));
                break;

        }
    }

    private void clearViewBack() {
        weekNewview1.setBackground(null);
        monthNewview2.setBackground(null);
        yearNewview3.setBackground(null);
        weekNewview1.setBackgroundColor(getResources().getColor(R.color.black_c));
        monthNewview2.setBackgroundColor(getResources().getColor(R.color.black_c));
        yearNewview3.setBackgroundColor(getResources().getColor(R.color.black_c));
        weekNewtv1.setTextColor(Color.parseColor("#828282"));
        monthNewtv2.setTextColor(Color.parseColor("#828282"));
        yearNewtv3.setTextColor(Color.parseColor("#828282"));

    }


    private class weekTvClickListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.xRb1:
                    showTopData(0);
                    xRb1.toggle();
                    break;
                case R.id.xRb2:
                    showTopData(1);
                    break;
                case R.id.xRb3:
                    showTopData(2);
                    break;
                case R.id.xRb4:
                    showTopData(3);
                    break;
                case R.id.xRb5:
                    showTopData(4);
                    break;
                case R.id.xRb6:
                    showTopData(5);
                    break;
                case R.id.xRb7:
                    showTopData(6);
                    break;
            }
        }
    }

    private void showTopData(int position) {
        watchNewh8DataStepTv.setText("" + watchDataList.get(position).getStepNumber() + "");
        String discan = watchDataList.get(position).getDistance();
        if (!WatchUtils.isEmpty(discan)) {
            if(WatchUtils.judgeUnit(MyApp.getContext())){ //公制
                watchNewh8DataDisTv.setText(discan + getResources().getString(R.string.km));
            }else{  //英制
                double tmpDis = Double.valueOf(discan);
                watchNewh8DataDisTv.setText(WatchUtils.doubleunitToImperial(tmpDis,0)+"mi");

            }

        }

        //watchNewh8DataDisTv.setText(watchDataList.get(position).getDistance() + getResources().getString(R.string.km));
        watchNewh8DataKcalTv.setText(watchDataList.get(position).getCalories() + getResources().getString(R.string.km_cal));
        watchNewh8DatadatelTv.setText(watchDataList.get(position).getRtc());
    }


    public void LineChart(List<WatchDataDatyBean> wtList, int count, Map<String,Integer> mp){
        //Log.e(TAG,"----count="+count);
        if(getActivity() == null || getActivity().isFinishing())
            return;
        //数据解析
        entryList.clear();
        if(count == 12 && dtLis.size()>0){    //年
            for(int k=0;k<=12;k++){
                entryList.add(new Entry(k,mp.get(dtLis.get(k))));
            }

        }else{
            for(int i = 0;i<count;i++){
                //Log.e(TAG,"------解析="+wtList.get(i).getStepNumber());
                entryList.add(new Entry(i,wtList.get(i).getStepNumber()));
                dtLis.add(wtList.get(i).getRtc().substring(5, wtList.get(0).getRtc().length()));
            }
        }

        watchNewh8dataweekChar.getDescription().setEnabled(false);
        watchNewh8dataweekChar.setPinchZoom(false);
        watchNewh8dataweekChar.setScaleXEnabled(false);
        watchNewh8dataweekChar.setScaleEnabled(false);
        //是否拖动
        watchNewh8dataweekChar.setDragEnabled(false);
        //右侧Y轴
        YAxis leftAxis = watchNewh8dataweekChar.getAxisRight();  //得到图表的右侧Y轴实例
        leftAxis.enableGridDashedLine(10f, 20f, 0f); //设置横向表格为虚线
        leftAxis.setLabelCount(2,false);  //设置y轴显示的数量
        leftAxis.setGridLineWidth(0);
        leftAxis.setEnabled(false);

        //左侧y轴
        YAxis leftY = watchNewh8dataweekChar.getAxisLeft();
        leftY.setEnabled(false);

        IAxisValueFormatter iAxisValueFormatter = new BarXFormartValue(watchNewh8dataweekChar,dtLis);

        //x轴
        XAxis xAxis = watchNewh8dataweekChar.getXAxis();  //设置X轴
        xAxis.setDrawGridLines(false);  //不显示网格线

        xAxis.setValueFormatter(iAxisValueFormatter);
        if(count == 7){ //周
            xAxis.setEnabled(false);
        }else{
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }

        Legend mLegend = watchNewh8dataweekChar.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.NONE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        if(entryList != null && entryList.size() > 0){

            //设置数据
            LineDataSet lineDataSet = new LineDataSet(entryList, "");

            lineDataSet.setColor(Color.parseColor("#40e0d0")); //设置该线的颜色
            lineDataSet.setCircleColor(Color.parseColor("#40e0d0")); //设置节点圆圈颜色
            lineDataSet.setDrawValues(true); //设置是否显示点的坐标值
            lineDataSet.setValueTextColor(Color.parseColor("#5abdfe"));
            lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);// 设置平滑曲线

            //线的集合（可单条或多条线）
            List<ILineDataSet> dataSets = new ArrayList<>();

            dataSets.add(lineDataSet);
            //把要画的所有线(线的集合)添加到LineData里
            LineData lineData = new LineData(dataSets);

            watchNewh8dataweekChar.setDrawGridBackground(false);
            watchNewh8dataweekChar.setDrawGridBackground(false);
            //把最终的数据setData
            watchNewh8dataweekChar.setData(lineData);
            watchNewh8dataweekChar.isAnimationCacheEnabled();
//            //动画
            watchNewh8dataweekChar.animateX(800);
            closeLoadingDialog();
           handler.sendEmptyMessage(1001);

        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            weekNewtv1.setEnabled(true);
            monthNewtv2.setEnabled(true);
            yearNewtv3.setEnabled(true);
        }
    };


}
