package com.example.bozhilun.android.commdbserver;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.LogTestUtil;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30BloadDataView;
import com.example.bozhilun.android.b30.service.B30DataServer;
import com.example.bozhilun.android.b30.view.DataMarkView;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.data.BarXFormartValue;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2019/3/14
 */
public class CommDataFragment extends LazyFragment {

    private static final String TAG = "CommDataFragment";

    View commView;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30DataWeekTv)
    TextView b30DataWeekTv;
    @BindView(R.id.b30DataMonthTv)
    TextView b30DataMonthTv;
    @BindView(R.id.b30DataYearTv)
    TextView b30DataYearTv;
    @BindView(R.id.stepDataValueTv)
    TextView stepDataValueTv;
    @BindView(R.id.stepDataChartView)
    BarChart stepDataChartView;
    @BindView(R.id.sleepDataValueTv)
    TextView sleepDataValueTv;
    @BindView(R.id.sleepDataChartView)
    BarChart sleepDataChartView;
    @BindView(R.id.heartDataValueTv)
    TextView heartDataValueTv;
    @BindView(R.id.heartDataChartView)
    BarChart heartDataChartView;
    @BindView(R.id.bloodDataValueTv)
    TextView bloodDataValueTv;
    //血压的图表
    @BindView(R.id.charBloadView)
    B30BloadDataView charBloadView;
    Unbinder unbinder;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
    @BindView(R.id.b30BloadChartLin)
    RelativeLayout b30BloadChartLin;

    private Gson gson = new Gson();
    //步数相关
    private List<Integer> mValues;
    List<BarEntry> pointbar;
    private List<String> xStepList;

    //心率相关

    private List<Integer> heartValues;
    /**
     * 心率X轴数据
     */
    private List<String> heartXList;
    private List<BarEntry> heartBarEntryList;


    /**
     * 睡眠的数值
     */
    private List<Float> sleepVlaues;
    /**
     * 睡眠X轴
     */
    private List<String> sleepXList;
    private List<BarEntry> sleepBarEntryList;


    //血压的时间数据
    private ArrayList<String> bloodDateList = new ArrayList<>();
    //临时的血压数据源
    private ArrayList<String> tempBloodDateList = new ArrayList<>();


    //血压的数据源
    private List<SparseIntArray> bloodList = new ArrayList<>();

    private int SectectCode = 7;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:      //步数
                    if (getActivity() != null && !getActivity().isFinishing())
                        showStepsChat(mValues, xStepList);
                    break;
                case 0x02:  //心率
                    if (getActivity() != null && !getActivity().isFinishing())
                        showHeartChart(heartValues, heartXList);
                    break;
                case 0x03:  //睡眠
                    if (getActivity() != null && !getActivity().isFinishing())
                        showSleepChart(sleepVlaues, sleepXList);
                    break;
                case 0x04:  //血压
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        Log.e(TAG, "bloodList " + bloodList.toString());
                        Log.e(TAG, "bloodDateList " + bloodDateList.toString());
                        charBloadView.updateView(bloodList, bloodDateList);
                        charBloadView.setScal(false);
                    }
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        commView = inflater.inflate(R.layout.fragment_b30_data, container, false);
        unbinder = ButterKnife.bind(this, commView);


        initViews();
        initData();


        return commView;
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.data));
        String bleName = WatchUtils.getSherpBleName(getActivity());
        if (WatchUtils.isEmpty(bleName))
            return;
        Log.e(TAG,"-------bleName="+bleName);
        if (bleName.equals("B30")
                || bleName.equals("Ringmii")
                || bleName.equals("B31S")
                || bleName.equals("500S")) { //目前B30和盖德的B30有血压功能
            b30BloadChartLin.setVisibility(View.VISIBLE);
        } else {
            if(bleName.equals("B31")){
                boolean isB31HasBp = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.IS_B31_HAS_BP_KEY,false);
                b30BloadChartLin.setVisibility(isB31HasBp?View.VISIBLE:View.GONE);
                return;
            }

            b30BloadChartLin.setVisibility(View.GONE);
        }

    }

    private void initData() {
        mValues = new ArrayList<>();
        xStepList = new ArrayList<>();
        pointbar = new ArrayList<>();

        heartValues = new ArrayList<>();
        heartXList = new ArrayList<>();
        heartBarEntryList = new ArrayList<>();

        sleepVlaues = new ArrayList<>();
        sleepXList = new ArrayList<>();
        sleepBarEntryList = new ArrayList<>();

        //查询数据
        String userId = (String) SharedPreferencesUtils.readObject(getContext(), Commont.USER_ID_DATA);
        setClearStyle(0);


    }


    //根据开始和计算日期查询数据
    private void findDataForDB(int code) {
        mValues.clear();
        xStepList.clear();
        String endDay = WatchUtils.obtainFormatDate(1); //默认昨天
        String startDay;

        switch (code) {
            case 0x00:
                startDay = WatchUtils.obtainFormatDate(7);
                break;
            case 0x01:
                startDay = WatchUtils.obtainFormatDate(30);
                break;
            case 0x02:
                startDay = WatchUtils.obtainFormatDate(365);
                break;
            default:
                startDay = WatchUtils.obtainFormatDate(7);
                break;
        }

        Log.e(TAG, "------startDay=" + startDay + "--endDay=" + endDay);


        String bleMac = WatchUtils.getSherpBleMac(getContext());
        if(WatchUtils.isEmpty(bleMac))
            return;
        //步数的集合
        List<CommDownloadDb> stepCountDb = CommDBManager.getCommDBManager().findCommDownloadDb(bleMac,
                CommDBManager.COMM_TYPE_STEP, startDay, endDay);
        if (stepCountDb != null && !stepCountDb.isEmpty()) {
            analysisStepData(stepCountDb, code);
        }


        //心率
        List<CommDownloadDb> heartDb = CommDBManager.getCommDBManager().findCommDownloadDb(bleMac,
                CommDBManager.COMM_TYPE_HEART, startDay, endDay);
        if (heartDb != null && !heartDb.isEmpty()) {
            analysisHeartData(heartDb, code);
        }


        //睡眠
        List<CommDownloadDb> sleepDb = CommDBManager.getCommDBManager().findCommDownloadDb(bleMac,
                CommDBManager.COMM_TYPE_SLEEP, startDay, endDay);
        if (sleepDb != null && !sleepDb.isEmpty()) {
            analysisSleepData(sleepDb, code);
        }


        //查询血压
        List<CommDownloadDb> bloodDb = CommDBManager.getCommDBManager().findCommDownloadDb(bleMac,
                CommDBManager.COMM_TYPE_BLOOD, startDay, endDay);
        if (bloodDb != null && !bloodDb.isEmpty()) {
            analysisBloodData(bloodDb, code);
        }

    }

    //解析血压数据
    private void analysisBloodData(final List<CommDownloadDb> bloodDb, final int code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tempBloodDateList.clear();
                bloodDateList.clear();
                bloodList.clear();
                Collections.sort(bloodDb, new Comparator<CommDownloadDb>() {
                    @Override
                    public int compare(CommDownloadDb o1, CommDownloadDb o2) {
                        return o1.getDateStr().compareTo(o2.getDateStr());
                    }
                });

                Map<String, String> bloodMap = new HashMap<>();
                //日期和总次数的map
                Map<String, Integer> countMap = new HashMap<>();
                //次数
                int bloodCount = 0;
                //高压的总值
                int heightBloodCountValue = 0;
                //低压的总值
                int lowBloodCountValue = 0;

                for (CommDownloadDb commDownloadDb : bloodDb) {
                    //血压的数据
                    String bloodStr = commDownloadDb.getStepNumber();
                    //高压
                    int hightBlood = Integer.valueOf(StringUtils.substringBefore(bloodStr, "-").trim());
                    //低压
                    int lowBlood = Integer.valueOf(StringUtils.substringAfter(bloodStr, "-"));
                    String dateStr = commDownloadDb.getDateStr();

                    if (code == 0x02) {
                        //日期
                        String yearDateStr = dateStr.substring(2, 7); //显示 yy-MM
                        if (bloodMap.get(yearDateStr) != null) {
                            if (hightBlood != 0) {
                                bloodCount++;
                                heightBloodCountValue += hightBlood;
                                lowBloodCountValue += lowBlood;
                            }
                        } else {
                            if (hightBlood != 0) {
                                bloodCount = 1;
                                heightBloodCountValue += hightBlood;
                                lowBloodCountValue += lowBlood;
                            }
                        }

                        bloodMap.put(yearDateStr, heightBloodCountValue + "-" + lowBloodCountValue);
                        countMap.put(yearDateStr, bloodCount);

                    } else {
                        //数据源
                        SparseIntArray sparseIntArray = new SparseIntArray();
                        sparseIntArray.append(lowBlood, hightBlood);
                        bloodList.add(sparseIntArray);
                        //日期
                        if (code == 0x01) {
                            tempBloodDateList.add(dateStr.substring(5, dateStr.length()));
                        } else {
                            bloodDateList.add(dateStr.substring(5, dateStr.length()));
                        }

                    }

                }

                if (code == 0x01) {   //月
                    bloodDateList.addAll(mendTime(tempBloodDateList));
                }

                if (code == 0x02) {       //年的数据
                    //遍历map
                    for (Map.Entry<String, String> mp : bloodMap.entrySet()) {
                        tempBloodDateList.add(mp.getKey());
                    }

                    //排序
                    Collections.sort(tempBloodDateList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    //遍历时间数据，得到数据源
                    for (int i = 0; i < tempBloodDateList.size(); i++) {
                        SparseIntArray sparseIntArray = new SparseIntArray();
                        //每个月有数据的总次数
                        float couontStr = countMap.get(tempBloodDateList.get(i));
                        //总的高压和低压的数据
                        String countBloStr = bloodMap.get(tempBloodDateList.get(i));
                        //总的高压值
                        int countHeightBlood = Integer.valueOf(StringUtils.substringBefore(countBloStr, "-"));
                        //总的低压值
                        int countLowBlood = Integer.valueOf(StringUtils.substringAfter(countBloStr, "-"));
                        int hV = (int) (countHeightBlood / couontStr);
                        int lowV = (int) (countLowBlood / couontStr);
                        sparseIntArray.append(lowV, hV);
                        bloodList.add(sparseIntArray);
                    }
                    bloodDateList.addAll(mendTime(tempBloodDateList));
                }

                handler.sendEmptyMessage(0x04);
            }
        }).start();

    }

    //计算睡眠
    private void analysisSleepData(final List<CommDownloadDb> sleepDb, final int code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleepVlaues.clear();
                sleepXList.clear();
                //排序，
                Collections.sort(sleepDb, new Comparator<CommDownloadDb>() {
                    @Override
                    public int compare(CommDownloadDb o1, CommDownloadDb o2) {
                        return o1.getDateStr().compareTo(o2.getDateStr());
                    }
                });

                //用于计算年的，每个月总心率
                int sum = 0;
                //每个月有数据的天数
                int count = 0;
                //日期和数据的map
                Map<String, Integer> sleepMap = new HashMap<>();
                //日期和总次数的map
                Map<String, Integer> countMap = new HashMap<>();

                for (CommDownloadDb commDownloadDb : sleepDb) {
                    float stepNumber = Integer.valueOf(commDownloadDb.getStepNumber());
                    String dateStr = commDownloadDb.getDateStr();
                    if (code == 0x02) {   //年
                        String strDate = dateStr.substring(2, 7); //显示 yy-MM
                        if (sleepMap.get(strDate) != null) {
                            if (stepNumber != 0) {
                                count++;
                                sum += stepNumber;
                            }
                        } else {
                            if (stepNumber != 0) {
                                count = 1;
                                sum += stepNumber;
                            }
                        }
                        //保存日期和步数的map  key:yy-mm ;value : 总步数
                        sleepMap.put(strDate, sum);
                        countMap.put(strDate, count);

                    } else {
                        sleepVlaues.add((float) stepNumber / 60);
                        sleepXList.add(dateStr.substring(5, dateStr.length()));

                    }

                }

                if (code == 0x02) {
                    //遍历map，得到日期
                    for (Map.Entry<String, Integer> mp : sleepMap.entrySet()) {
                        sleepXList.add(mp.getKey().trim());
                    }

                    Collections.sort(sleepXList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    //遍历日期，排序
                    for (int i = 0; i < sleepXList.size(); i++) {
                        //每个月有数据的总次数
                        float couontStr = countMap.get(sleepXList.get(i));
                        //数据
                        int countStep = sleepMap.get(sleepXList.get(i)) / 60;
                        //每个月的平均步数
                        float avgStep = countStep / (couontStr == 0 ? 1 : couontStr);
                        sleepVlaues.add(avgStep);

                    }

                }
                handler.sendEmptyMessage(0x03);
            }
        }).start();


    }

    //计算心率
    private void analysisHeartData(final List<CommDownloadDb> heartDb, final int code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                heartValues.clear();
                heartXList.clear();
                //排序，
                Collections.sort(heartDb, new Comparator<CommDownloadDb>() {
                    @Override
                    public int compare(CommDownloadDb o1, CommDownloadDb o2) {
                        return o1.getDateStr().compareTo(o2.getDateStr());
                    }
                });

                //用于计算年的，每个月总心率
                int sum = 0;
                //每个月有护具的天数
                int count = 0;
                //日期和数据的map
                Map<String, Integer> heartMap = new HashMap<>();
                //日期和总次数的map
                Map<String, Integer> countMap = new HashMap<>();

                for (CommDownloadDb commDownloadDb : heartDb) {
                    int stepNumber = Integer.valueOf(commDownloadDb.getStepNumber());
                    String dateStr = commDownloadDb.getDateStr();
                    if (code == 0x02) {   //年
                        String strDate = dateStr.substring(2, 7); //显示 yy-MM
                        if (heartMap.get(strDate) != null) {
                            if (stepNumber != 0) {
                                count++;
                                sum += stepNumber;
                            }
                        } else {
                            if (stepNumber != 0) {
                                count = 1;
                                sum += stepNumber;
                            }
                        }
                        //保存日期和步数的map  key:yy-mm ;value : 总步数
                        heartMap.put(strDate, sum);
                        countMap.put(strDate, count);

                    } else {
                        heartValues.add(stepNumber);
                        heartXList.add(dateStr.substring(5, dateStr.length()));

                    }

                }


                if (code == 0x02) {   //年
                    //遍历map，得到日期
                    for (Map.Entry<String, Integer> mp : heartMap.entrySet()) {
                        heartXList.add(mp.getKey().trim());
                    }

                    Collections.sort(heartXList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    //遍历日期，排序
                    for (int i = 0; i < heartXList.size(); i++) {
                        //每个月有数据的总次数
                        int couontStr = countMap.get(heartXList.get(i));
                        //数据
                        int countStep = heartMap.get(heartXList.get(i));
                        //每个月的平均步数
                        int avgStep = countStep / (couontStr == 0 ? 1 : couontStr);
                        heartValues.add(avgStep);

                    }

                }
                handler.sendEmptyMessage(0x02);
            }
        }).start();


    }

    //步数的计算
    private void analysisStepData(final List<CommDownloadDb> stepDb, final int code) {
        Log.e(TAG,"-----------stepCountDb--------下载步数大小="+stepDb.size());

        new Thread(new Runnable() {
            @Override
            public void run() {
                //排序，
                Collections.sort(stepDb, new Comparator<CommDownloadDb>() {
                    @Override
                    public int compare(CommDownloadDb o1, CommDownloadDb o2) {
                        return o1.getDateStr().compareTo(o2.getDateStr());
                    }
                });

                //用于计算年的，每个月总步数
                int sum = 0;
                //每个月有步数的天数
                int count = 0;
                //日期和总步数的map
                Map<String, Integer> stepNumberMap = new HashMap<>();
                //日期和总次数的map
                Map<String, Integer> countMap = new HashMap<>();

                for (CommDownloadDb commDownloadDb : stepDb) {
                    int stepNumber = Integer.valueOf(commDownloadDb.getStepNumber());
                    String dateStr = commDownloadDb.getDateStr();
                    if (code == 0x02) {   //年
                        //Log.e(TAG,"----------commDownloadDb="+commDownloadDb.toString());
                        String strDate = dateStr.substring(2, 7); //显示 yy-MM
                        if (stepNumberMap.get(strDate) != null) {
                            if (stepNumber != 0) {
                                count++;
                                sum += stepNumber;
                            }
                        } else {
                            if (stepNumber != 0) {
                                count = 1;
                                sum += stepNumber;
                            }
                        }

                        //Log.e(TAG,"-----sum="+sum+"--count="+count);

                        //保存日期和步数的map  key:yy-mm ;value : 总步数
                        stepNumberMap.put(strDate, sum);
                        countMap.put(strDate, count);

                        // showStepsChat(mValues,xStepList);

                    } else {
                        mValues.add(stepNumber);
                        xStepList.add(dateStr.substring(5, dateStr.length()));

                    }

                }

                if (code == 0x02) {   //年
                    //遍历map，得到日期
                    for (Map.Entry<String, Integer> mp : stepNumberMap.entrySet()) {
                        xStepList.add(mp.getKey().trim());
                    }

                    Collections.sort(xStepList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    //遍历日期，排序
                    for (int i = 0; i < xStepList.size(); i++) {
                        //每个月有数据的总次数
                        int couontStr = countMap.get(xStepList.get(i));
                        //Log.e(TAG,"----------couontStr="+couontStr);
                        //步数
                        int countStep = stepNumberMap.get(xStepList.get(i));
                        //Log.e(TAG,"-------countStep="+countStep);
                        //每个月的平均步数
                        int avgStep = countStep / (couontStr == 0 ? 1 : couontStr);
                        //Log.e(TAG,"-------avgStep="+avgStep);
                        mValues.add(avgStep);

                    }

                }
                handler.sendEmptyMessage(0x01);
            }
        }).start();
    }


    /**
     * 补齐天数到七天
     */
    private List<String> mendTime(List<String> timeList) {
        int dateSize = timeList.size();
        //Log.e(TAG,"------dateSize="+dateSize);
        List<String> menList = new ArrayList<>();
        int interval = timeList.size() / (dateSize == 13 ? 6 : 7);
        if (interval > 0) {
            for (int i = 0; i < timeList.size(); i++) {
                int position = i * interval;// 防越界
                if (position < timeList.size())
                    menList.add(timeList.get(position));
            }
        } else {
            menList.addAll(timeList);
        }
        int mend = 7 - menList.size();// 补位
        if (mend < 1) return menList;
        for (int i = 0; i < mend; i++) {
            menList.add("");
        }
        return menList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.commentB30BackImg, R.id.b30DataWeekTv,
            R.id.b30DataMonthTv, R.id.b30DataYearTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                getActivity().finish();
                break;
            case R.id.b30DataWeekTv:    //周
                setClearStyle(0);
                break;
            case R.id.b30DataMonthTv:   //月
                setClearStyle(1);
                break;
            case R.id.b30DataYearTv:    //年
                setClearStyle(2);
                break;
        }
    }


    //周，月，年切换
    private void setClearStyle(int code) {
        if (b30DataWeekTv != null) {
            b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataWeekTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataMonthTv != null) {
            b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataMonthTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataYearTv != null) {
            b30DataYearTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataYearTv.setTextColor(Color.parseColor("#333333"));
        }

        if (getActivity() == null || getActivity().isFinishing())
            return;
        switch (code) {
            case 0:
                b30DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_one);
                break;
            case 1:
                b30DataMonthTv.setTextColor(getResources().getColor(R.color.white));
                b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap);
                break;
            case 2:
                b30DataYearTv.setTextColor(getResources().getColor(R.color.white));
                b30DataYearTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_two);
                break;
        }
        SectectCode = ((code == 0) ? 7 : (code == 1) ? 30 : (code == 2) ? 365 : 7);
        Log.e(TAG, "===查询的天数== " + SectectCode);
        findDataForDB(code);
    }


    // 展示步数图表
    private void showStepsChat(List<Integer> mValues, List<String> xList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        pointbar.clear();
        for (int i = 0; i < mValues.size(); i++) {
            pointbar.add(new BarEntry(i, mValues.get(i)));
        }
//        Log.e(TAG, "----步数大小=" + mValues.size());
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (mValues.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }
        stepDataChartView.setData(bardata);

        stepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        stepDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        stepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        stepDataChartView.setPinchZoom(false);
        stepDataChartView.setTouchEnabled(true);
        stepDataChartView.setScaleEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(stepDataChartView, xList);
        XAxis xAxis = stepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);
        stepDataChartView.getDescription().setEnabled(false);
        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(stepDataChartView);
        stepDataChartView.setMarker(dataMarkView);
        stepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        stepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        stepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        stepDataChartView.getAxisLeft().setEnabled(false);
        stepDataChartView.getXAxis().setSpaceMax(0.5f);
        stepDataChartView.animateXY(1000, 2000);//设置动画
    }


    //展示心率图表
    private void showHeartChart(List<Integer> heartList, List<String> xlt) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        heartBarEntryList.clear();
        for (int i = 0; i < heartList.size(); i++) {
            heartBarEntryList.add(new BarEntry(i, (int) heartList.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(heartBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
//        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (heartList.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }

        heartDataChartView.setData(bardata);
        heartDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        heartDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        heartDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        heartDataChartView.getLegend().setEnabled(false);
        heartDataChartView.setTouchEnabled(true);
        heartDataChartView.setPinchZoom(false);
        heartDataChartView.setScaleEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(heartDataChartView, xlt);
        XAxis xAxis = heartDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);//设置X轴的高度
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);


        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 1);
        dataMarkView.setChartView(heartDataChartView);
        heartDataChartView.setMarker(dataMarkView);

        heartDataChartView.getDescription().setEnabled(false);

        heartDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        heartDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        heartDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        heartDataChartView.getAxisLeft().setEnabled(false);
        heartDataChartView.getXAxis().setSpaceMax(0.5f);
        heartDataChartView.animateXY(1000, 2000);//设置动画
    }


    //展示睡眠图表
    private void showSleepChart(List<Float> sleepVlaues, List<String> sleepXList) {
        if (getActivity() == null || getActivity().isFinishing())
            return;
        sleepBarEntryList.clear();
        for (int i = 0; i < sleepVlaues.size(); i++) {
            sleepBarEntryList.add(new BarEntry(i, sleepVlaues.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(sleepBarEntryList, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.parseColor("#ffffff"));
//        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = sleepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(1.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (sleepVlaues.size() >= 15) {
            bardata.setBarWidth(0.2f);  //设置柱子宽度
            barDataSet.setValueTextSize(6.0f);//柱子顶部字体大小
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        sleepDataChartView.setData(bardata);
        sleepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        sleepDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        sleepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        sleepDataChartView.setTouchEnabled(true);
        sleepDataChartView.setPinchZoom(false);
        sleepDataChartView.setScaleEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(sleepDataChartView, sleepXList);
        XAxis xAxis = sleepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);
        sleepDataChartView.getDescription().setEnabled(false);


        DataMarkView dataMarkView = new DataMarkView(getActivity(), R.layout.mark_view_layout, 0);
        dataMarkView.setChartView(sleepDataChartView);
        sleepDataChartView.setMarker(dataMarkView);


        sleepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        sleepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        sleepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        sleepDataChartView.getAxisLeft().setEnabled(false);
        sleepDataChartView.getXAxis().setSpaceMax(0.5f);
        sleepDataChartView.animateXY(1000, 2000);//设置动画
    }

}
