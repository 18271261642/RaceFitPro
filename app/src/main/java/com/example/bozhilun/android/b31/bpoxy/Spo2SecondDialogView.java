package com.example.bozhilun.android.b31.bpoxy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.veepoo.protocol.model.enums.ESpo2hDataType;
import com.veepoo.protocol.util.HRVOriginUtil;
import com.veepoo.protocol.util.Spo2hOriginUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_BREATH;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_HEART;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_LOWSPO2H;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SLEEP;
import static com.veepoo.protocol.model.enums.ESpo2hDataType.TYPE_SPO2H;

/**
 * Created by Admin
 * Date 2019/1/5
 */
public class Spo2SecondDialogView extends AlertDialog {

    private static final String TAG = "Spo2SecondDialogView";

    private RecyclerView recyclerView;
    private List<Map<String,Float>> mapList;
    private Context context;
    private ShowSpo2DetailAdapter showSpo2DetailAdapter;

    private TextView maxVTv;

    private int spo2Type;

    private List<Float> calList = new ArrayList<>();


    public Spo2SecondDialogView(@NonNull Context context) {
        super(context);
        this.context = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spo2_second_dialog_view);

        initViews();



    }

    private void initViews() {
        maxVTv = findViewById(R.id.spo2MaxTv);
        recyclerView = findViewById(R.id.spo2SecondDialogRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);
        mapList = new ArrayList<>();
        showSpo2DetailAdapter = new ShowSpo2DetailAdapter(mapList,context);
        recyclerView.setAdapter(showSpo2DetailAdapter);
    }


    //设置类型
    public void setSpo2Type(int spo2Type) {
        this.spo2Type = spo2Type;
    }

    //设置显示列表数据
    @SuppressLint("SetTextI18n")
    public void setMapList(List<Map<String, Float>> list) {
        mapList.clear();
        calList.clear();
        mapList.addAll(list);
        showSpo2DetailAdapter.setSpowTag(spo2Type);
        showSpo2DetailAdapter.notifyDataSetChanged();
        float hrvSum = 0;
        for(Map<String,Float> calMap : list){
            calList.add(calMap.get("value"));
            hrvSum = hrvSum + calMap.get("value");
        }
        //Log.e(TAG,"---------平均值="+Collections.max(calList)+"---="+Collections.min(calList));
        DecimalFormat decimalFormat = new DecimalFormat("#");    //不保留小数
        maxVTv.setText(context.getResources().getString(R.string.max_value)+"="+decimalFormat.format(Collections.max(calList))
                +","+context.getResources().getString(R.string.min_value)+"="+decimalFormat.format(Collections.min(calList))+","+
                context.getResources().getString(R.string.ave_value)+"="+decimalFormat.format(hrvSum/calList.size()));

    }

    //设置显示最大值，最小值和平均值数据
    @SuppressLint("SetTextI18n")
    public void setSporUtils(Spo2hOriginUtil sporUtils){

        //获取低氧数据[最大，最小，平均]       *参考：取低氧最大值，大于20，则显示偏高，其他显示正常
        int[] stypeArrayVlue = sporUtils.getOnedayDataArr(getSpo2Type(spo2Type));
        //Log.e(TAG, "showLowSpo2h [最大，最小，平均]: " + Arrays.toString(stypeArrayVlue)+"--=spo2Type="+spo2Type);
        maxVTv.setText(context.getResources().getString(R.string.max_value)+"="+stypeArrayVlue[0]
                +","+context.getResources().getString(R.string.min_value)+"="+stypeArrayVlue[1]+","+
        context.getResources().getString(R.string.ave_value)+"="+stypeArrayVlue[2]);



    }

    //HRV的最大值最小值和平均值
    @SuppressLint("SetTextI18n")
    public void setHRVUtils(HRVOriginUtil mHrvOriginUtil, int var){
        int[] hrvArrayData = mHrvOriginUtil.getOnedayDataArr(var);
        //Log.e(TAG,"-------hrvArrayData="+Arrays.toString(hrvArrayData));
        maxVTv.setText(context.getResources().getString(R.string.max_value)+"="+hrvArrayData[0]
                +","+context.getResources().getString(R.string.min_value)+"="+hrvArrayData[1]+","+
                context.getResources().getString(R.string.ave_value)+"="+hrvArrayData[2]);

    }


    private ESpo2hDataType getSpo2Type(int tag){
        ESpo2hDataType eSpo2hDataType = null;
        switch (tag){
            case 0:     //血氧，呼吸暂停
                eSpo2hDataType =  ESpo2hDataType.TYPE_SPO2H;
                break;
            case 1:     //心脏负荷
                eSpo2hDataType =  ESpo2hDataType.TYPE_HEART;
                break;
            case 2:     //睡眠活动
                eSpo2hDataType =  ESpo2hDataType.TYPE_SLEEP;
                break;
            case 3:     //呼吸率
                eSpo2hDataType =  ESpo2hDataType.TYPE_BREATH;
                break;
            case 4:     //低氧时间
                eSpo2hDataType =  ESpo2hDataType.TYPE_LOWSPO2H;
                break;
        }
        return eSpo2hDataType;
    }

}
