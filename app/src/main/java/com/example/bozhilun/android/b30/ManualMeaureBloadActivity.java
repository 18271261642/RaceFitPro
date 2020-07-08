package com.example.bozhilun.android.b30;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.CustomCircleProgressBar;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPDetectDataListener;
import com.veepoo.protocol.model.datas.BpData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.enums.EBPDetectStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 测量血压界面
 */
public class ManualMeaureBloadActivity extends WatchBaseActivity {

    private static final String TAG = "ManualMeaureBloadActivi";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30MeaureBloadProgressView)
    CustomCircleProgressBar b30MeaureBloadProgressView;
    @BindView(R.id.b30MeaureStartImg)
    ImageView b30MeaureStartImg;
    @BindView(R.id.b30MeaurePlaceHolderImg)
    ImageView b30MeaurePlaceHolderImg;
    @BindView(R.id.showStateTv)
    TextView showStateTv;
    @BindView(R.id.manual_blood_public_line)
    View manual_blood_public_line;
    @BindView(R.id.manual_blood_private_line)
    View manual_blood_private_line;
    @BindView(R.id.private_mode_setting)
    TextView private_mode_setting;

    //开始或者停止测量的标识
    private boolean isStart = false;
    /**
     * true_私人模式 false_通用模式
     */
    private boolean privateMode;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BpData meaureBpData = (BpData) msg.obj;
            if (meaureBpData != null) {
                if(meaureBpData.getStatus() == EBPDetectStatus.STATE_BP_BUSY){
                    showStateTv.setText(WatchUtils.setBusyDesicStr());
                    stopMeaureBoload();
                    return;
                }

                if (meaureBpData.getProgress() == 100) {  //测量结束
                    stopMeaureBoload();
                    if (b30MeaureBloadProgressView != null) {
                        if (meaureBpData.getHighPressure() < 60 || meaureBpData.getLowPressure() < 30) {
                            b30MeaureBloadProgressView.setTmpTxt("0/0");
                        } else {
                            b30MeaureBloadProgressView.setTmpTxt(meaureBpData.getHighPressure() + "/" + meaureBpData.getLowPressure());//.setProgressText(meaureBpData.getHighPressure() + "/" + meaureBpData.getLowPressure());
                            showStateTv.setText(getResources().getString(R.string.string_normal));
                        }
                    }
                }
            } else {
                stopMeaureBoload();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_meaure_bload);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        b30MeaureBloadProgressView.setMaxProgress(100);
    }

    private void initViews() {
        commentB30TitleTv.setText(R.string.blood_manual_test);
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.GONE);
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg, R.id.b30MeaureStartImg, R.id.manual_blood_public
            , R.id.manual_blood_private, R.id.private_mode_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(ManualMeaureBloadActivity.this);
                break;
            case R.id.manual_blood_public:   //通用模式
                privateMode = false;
                manual_blood_private_line.setVisibility(View.GONE);
                private_mode_setting.setVisibility(View.GONE);
                manual_blood_public_line.setVisibility(View.VISIBLE);
                break;
            case R.id.manual_blood_private:   //私人模式
                privateMode = true;
                manual_blood_public_line.setVisibility(View.GONE);
                manual_blood_private_line.setVisibility(View.VISIBLE);
                private_mode_setting.setVisibility(View.VISIBLE);
                break;
            case R.id.private_mode_setting:   //  私人模式设置
                Intent intent = new Intent(this, PrivateBloadActivity.class);
                startActivity(intent);
                break;
            case R.id.b30MeaureStartImg:    //开始或者停止测量
                b30MeaurePlaceHolderImg.setVisibility(View.GONE);
                b30MeaureBloadProgressView.setVisibility(View.VISIBLE);
                if (MyCommandManager.DEVICENAME != null) {
                    if (!isStart) {
                        isStart = true;
                        showStateTv.setText("");
                        b30MeaureStartImg.setImageResource(R.drawable.detect_bp_pause);
                        b30MeaureBloadProgressView.setTmpTxt(null);
                        b30MeaureBloadProgressView.setScheduleDuring(27 * 1000);
                        b30MeaureBloadProgressView.setProgress(100);
                        if (MyCommandManager.DEVICENAME != null) {
                            MyApp.getInstance().getVpOperateManager().startDetectBP(bleWriteResponse, new IBPDetectDataListener() {
                                @Override
                                public void onDataChange(BpData bpData) {
                                    Log.e(TAG, "----bpData=" + bpData.toString());
                                    Message message = handler.obtainMessage();
                                    message.what = 1001;
                                    message.obj = bpData;
                                    handler.sendMessage(message);
                                }
                            }, privateMode ? EBPDetectModel.DETECT_MODEL_PRIVATE : EBPDetectModel
                                    .DETECT_MODEL_PUBLIC);
                        }
                    } else {
                        stopMeaureBoload();
                    }
                } else {
                    showStateTv.setText(getResources().getString(R.string.string_not_coon));
                }

                break;
        }
    }

    //停止测量
    private void stopMeaureBoload() {
        isStart = false;
        b30MeaureStartImg.setImageResource(R.drawable.detect_bp_start);
        b30MeaureBloadProgressView.stopAnim();
        MyApp.getInstance().getVpOperateManager().stopDetectBP(bleWriteResponse, privateMode ? EBPDetectModel.DETECT_MODEL_PRIVATE : EBPDetectModel
                .DETECT_MODEL_PUBLIC);
    }

    private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
            Log.e(TAG, "------bleWriteResponse=" + i);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(MyCommandManager.DEVICENAME != null)
           stopMeaureBoload();
    }

}
