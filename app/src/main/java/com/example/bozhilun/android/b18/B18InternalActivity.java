package com.example.bozhilun.android.b18;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B18CountSleepBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.google.gson.Gson;
import com.hplus.bluetooth.BleProfileManager;
import com.hplus.bluetooth.command.OnResponseListener;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B18内部测试页面
 * Created by Admin
 * Date 2019/11/25
 */
public class B18InternalActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.showB18Tv)
    TextView showB18Tv;
    @BindView(R.id.womenYearEdit)
    EditText womenYearEdit;
    @BindView(R.id.womenMonthEdit)
    EditText womenMonthEdit;
    @BindView(R.id.womenDayEdit)
    EditText womenDayEdit;
    @BindView(R.id.womenCycleEdit)
    EditText womenCycleEdit;
    @BindView(R.id.womenInterEdit)
    EditText womenInterEdit;
    @BindView(R.id.b50MsgEdit)
    EditText b50MsgEdit;

    @BindView(R.id.weekEdit)
    EditText weekEdit;


    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b18_internal_layout);
        ButterKnife.bind(this);


        initViews();
        BleProfileManager.getInstance().getCommandController().addResponseListener(onResponseListener);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("内部测试页面");

    }

    @OnClick({R.id.commentB30BackImg, R.id.b18SyncTimeBtn,
            R.id.b18GetDeviceSleepBtn, R.id.b18GetDbSleepBtn,
            R.id.sendWomenBtn, R.id.b50SendMsgBtn,R.id.syncWeekBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.b18SyncTimeBtn:
//                if (MyCommandManager.DEVICENAME == null)
//                    return;
                ToastUtil.showToast(B18InternalActivity.this, "获取版本号");
                BleProfileManager.getInstance().getCommandController().getDeviceVersionCommand();


                break;
            case R.id.b18GetDeviceSleepBtn:
                //findDbSleep();

                BleProfileManager.getInstance().getCommandController().sendIncomingNum("188888888888");
                BleProfileManager.getInstance().getCommandController().sendIncomingName("金三胖");

                break;
            case R.id.b18GetDbSleepBtn:
//                if(MyCommandManager.DEVICENAME == null)
//                    return;
                //获取汇总睡眠
                //BleProfileManager.getInstance().getCommandController().getSleepDataCommand();

                BleProfileManager.getInstance().getCommandController().sendClearCall(1);
                break;
            case R.id.sendWomenBtn:    //设置数据
                String yearStr = womenYearEdit.getText().toString();
                String monthStr = womenMonthEdit.getText().toString();
                String dayStr = womenDayEdit.getText().toString();
                String cycleStr = womenCycleEdit.getText().toString();
                String intevalStr = womenInterEdit.getText().toString();
                if (WatchUtils.isEmpty(yearStr) || WatchUtils.isEmpty(monthStr) || WatchUtils.isEmpty(dayStr) || WatchUtils.isEmpty(cycleStr) || WatchUtils.isEmpty(intevalStr))
                    return;
                if (!BleProfileManager.getInstance().isConnected())
                    return;
                B18BleConnManager.getB18BleConnManager().setWomenData(Integer.valueOf(yearStr), Integer.valueOf(monthStr), Integer.valueOf(dayStr), Integer.valueOf(cycleStr), Integer.valueOf(intevalStr));


                break;
            case R.id.b50SendMsgBtn:
                String msgStr = b50MsgEdit.getText().toString();
                if(WatchUtils.isEmpty(msgStr))
                    return;
                BleProfileManager.getInstance().getCommandController().sendSMSText(msgStr);
                break;
            case R.id.syncWeekBtn:
                String weekStr = weekEdit.getText().toString();
                if(WatchUtils.isEmpty(weekStr))
                    return;
                BleProfileManager.getInstance().getCommandController().setWeekCommand(Integer.valueOf(weekStr));
                break;

        }
    }

    private void findDbSleep() {

    }


    private OnResponseListener onResponseListener = new OnResponseListener() {
        @Override
        public void onResponse(String s) {
            Log.e("AA", "----------s=" + s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                if (!jsonObject.has("type"))
                    return;
                int type = jsonObject.getInt("type");
                switch (type) {
                    case 0x02:  //汇总睡眠返回
                        analysisCountSleep(jsonObject);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    StringBuffer stringBuffer = new StringBuffer();

    private void analysisCountSleep(JSONObject jsonObject) {
        try {
            if (!jsonObject.getBoolean("response")) return;

            String dataStr = jsonObject.getString("data");
            if (dataStr == null)
                return;
            B18CountSleepBean b18CountSleepBean = gson.fromJson(dataStr, B18CountSleepBean.class);
            int year = b18CountSleepBean.getYear();
            if (year == 0) {
                showB18Tv.setText(stringBuffer.toString());
                return;
            }
            String dayStr = year + "-" + b18CountSleepBean.getMonth() + "-" + b18CountSleepBean.getDay();
            stringBuffer.append(dataStr + ":" + gson.toJson(b18CountSleepBean) + "\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onResponseListener != null)
            BleProfileManager.getInstance().getCommandController().removeResponseListener(onResponseListener);
    }
}
