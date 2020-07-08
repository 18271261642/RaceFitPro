package com.example.bozhilun.android.B18I.b18isystemic;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.Motor;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 震动
 * @author： 安
 * @crateTime: 2017/9/5 18:32
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class ShockActivity extends WatchBaseActivity {
    public final String TAG = "----->>>" + this.getClass();
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.shock_image1)
    ImageView shockImage1;
    @BindView(R.id.shock_image2)
    ImageView shockImage2;
    @BindView(R.id.shock_image3)
    ImageView shockImage3;
    @BindView(R.id.shock_image4)
    ImageView shockImage4;
    @BindView(R.id.shock_close)
    LinearLayout shockClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_shock_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.shock_str));
    }

    @Override
    protected void onStart() {
        super.onStart();

        showLoadingDialog(getResources().getString(R.string.dlog));
        shockClose.setVisibility(View.GONE);//关闭震动的H9没有
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new Motor(commandResultCallback));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @OnClick({R.id.image_back, R.id.shock_close,
            R.id.shock_weak, R.id.shock_standard, R.id.shock_strong})
    public void onClick(View view) {
        showLoadingDialog(getResources().getString(R.string.dlog));
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.shock_close:
                setRT(0);
                break;
            case R.id.shock_weak:
                setRT(1);
                //H9
                // 0x01 :  默认最弱震动强度
                // 0x03 :  震动强度中
                // 0x05 :  震动强度高
//                showLoadingDialog(getResources().getString(R.string.dlog));
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new Motor(commandResultCallback, (byte) 0x01));
                break;
            case R.id.shock_standard:
                setRT(2);
                //H9
//                showLoadingDialog(getResources().getString(R.string.dlog));
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new Motor(commandResultCallback, (byte) 0x03));
                break;
            case R.id.shock_strong:
                setRT(3);
                //H9
//                showLoadingDialog(getResources().getString(R.string.dlog));
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new Motor(commandResultCallback, (byte) 0x05));
                break;
        }

//        AppsBluetoothManager.getInstance(MyApp.getContext())
//                .sendCommand(new Motor(commandResultCallback));
    }


    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {

        @Override
        public void onSuccess(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand instanceof Motor) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    byte motor = GlobalVarManager.getInstance().getMotor();
                    Log.d(TAG, "-------震动获取成功----" + motor);
                    SharedPreferencesUtils.setParam(MyApp.getInstance(), "H9_Motor", (int) motor);
                    // 0x01 :  默认最弱震动强度
                    // 0x03 :  震动强度中
                    // 0x05 :  震动强度高
                    if (motor == 3) {
                        setRT(1);
                    } else if (motor == 5) {
                        setRT(2);
                    } else if (motor == 7) {
                        setRT(3);
                    }
                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    Log.d(TAG, "震动强度设置成功");
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new Motor(commandResultCallback));
                }

            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();

            int motor = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_Motor", 0x01);
            Log.d(TAG, "-------震动获取成功----" + motor);
            // 0x01 :  默认最弱震动强度
            // 0x03 :  震动强度中
            // 0x05 :  震动强度高
            if (motor == 1) {
                setRT(1);
            } else if (motor == 3) {
                setRT(2);
            } else if (motor == 5) {
                setRT(3);
            }
        }
    };


    /**
     * 选中对号状态
     *
     * @param a
     */
    public void setRT(int a) {
        try {
            if (!this.isFinishing()){
                if (a == 0) {
                    shockImage1.setVisibility(View.VISIBLE);
                    shockImage2.setVisibility(View.INVISIBLE);
                    shockImage3.setVisibility(View.INVISIBLE);
                    shockImage4.setVisibility(View.INVISIBLE);
                } else if (a == 1) {
                    shockImage1.setVisibility(View.INVISIBLE);
                    shockImage2.setVisibility(View.VISIBLE);
                    shockImage3.setVisibility(View.INVISIBLE);
                    shockImage4.setVisibility(View.INVISIBLE);
                } else if (a == 2) {
                    shockImage1.setVisibility(View.INVISIBLE);
                    shockImage2.setVisibility(View.INVISIBLE);
                    shockImage3.setVisibility(View.VISIBLE);
                    shockImage4.setVisibility(View.INVISIBLE);
                } else if (a == 3) {
                    shockImage1.setVisibility(View.INVISIBLE);
                    shockImage2.setVisibility(View.INVISIBLE);
                    shockImage3.setVisibility(View.INVISIBLE);
                    shockImage4.setVisibility(View.VISIBLE);
                }
            }
        }catch (Error e){}


    }
}
