package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.b18.B18Constance;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2019/11/14
 */
public class B18DeviceFragment extends LazyFragment {

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @BindView(R.id.b18TurnWristToggleBtn)
    ToggleButton b18TurnWristToggleBtn;
    Unbinder unbinder;

    FragmentManager fragmentManager;


    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;
    @BindView(R.id.b18DeviceSportGoalTv)
    TextView b18DeviceSportGoalTv;
    @BindView(R.id.b18DeviceSleepGoalTv)
    TextView b18DeviceSleepGoalTv;
    @BindView(R.id.b18DeviceUnitTv)
    TextView b18DeviceUnitTv;

    @BindView(R.id.b18VersionTv)
    TextView b18VersionTv;



    private AlertDialog.Builder builder;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLoadingDialog();
            startActivity(new Intent(getActivity(), NewSearchActivity.class));
            getActivity().finish();
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b18_device_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        initData();

        return view;
    }

    private void initData() {
        sportGoalList = new ArrayList<>();
        sleepGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sportGoalList.add(i + "");
        }

        for (int i = 1; i < 48; i++) {
            sleepGoalList.add(WatchUtils.mul(Double.valueOf(i), 0.5) + "");
        }

        //公英制
        boolean isUnit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        b18DeviceUnitTv.setText(isUnit?getResources().getString(R.string.setkm) : getResources().getString(R.string.setmi));

        //是否开启了转腕亮屏
        boolean isWrist = (boolean) SharedPreferencesUtils.getParam(getActivity(), B18Constance.B18_IS_TURN_WRIST, false);
        b18TurnWristToggleBtn.setChecked(isWrist);
        if (MyCommandManager.DEVICENAME != null)
            B18BleConnManager.getB18BleConnManager().setTurnWristStatus(isWrist);

        //运动目标
        int sportGoal = (int) SharedPreferencesUtils.getParam(getActivity(), Commont.SPORT_GOAL_STEP, 0);
        b18DeviceSportGoalTv.setText(sportGoal+"");
        //睡眠目标
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),Commont.SLEEP_GOAL_KEY,"");
        b18DeviceSleepGoalTv.setText(b30SleepGoal);

        //版本号
        int b18Version = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),"B18_version",0);
        b18VersionTv.setText(getResources().getString(R.string.app_firm_version)+ ": "+b18Version);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.device));
        b18TurnWristToggleBtn.setOnCheckedChangeListener(onCheckedChangeListener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.commentB30BackImg, R.id.b18DeviceAlarmRel,
            R.id.b18DeviceLongSitRel, R.id.b18DeviceunBindRel,
            R.id.b18DeviceClearDataRel, R.id.b18DeviceScreenTimeRel,
            R.id.b18DeviceMsgRel, R.id.b18DeviceSportRel,
            R.id.b18DeviceSleepRel, R.id.b18DeviceUnitRel,R.id.b18DeviceOtaRel})
    public void onClick(View view) {
        fragmentManager = getFragmentManager();
        if (fragmentManager == null)
            return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                getActivity().finish();
                break;
            case R.id.b18DeviceSportRel:    //运动目标设置
                setSportGoal();
                break;
            case R.id.b18DeviceSleepRel:    //睡眠目标设置
                setSleepGoal();
                break;
            case R.id.b18DeviceUnitRel: //单位设置
                showUnitDialog();
                break;
            case R.id.b18DeviceMsgRel:
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.b18DeviceFragment, new B18MessageAlertFragment())
                        .commit();
                break;
            case R.id.b18DeviceAlarmRel:
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.b18DeviceFragment, new B18DeviceAlarmFragment())
                        .commit();
                break;
            case R.id.b18DeviceLongSitRel:
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.b18DeviceFragment, new B18LongSitFragment())
                        .commit();
                break;
            case R.id.b18DeviceScreenTimeRel:   //屏保时间
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.b18DeviceFragment, new B18DeviceScreenTimeFragment())
                        .commit();
                break;
            case R.id.b18DeviceOtaRel:      //固件升级
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.b18DeviceFragment, new B18OtaFragment())
                        .commit();
                break;
            case R.id.b18DeviceClearDataRel:
                clearB18Device();
                break;
            case R.id.b18DeviceunBindRel:   //解绑
                unBindB18Device();
                break;
        }
    }


    //展示公英制
    private void showUnitDialog() {

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0) {
                            b18DeviceUnitTv.setText(getResources().getString(R.string.setkm));
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                            // changeCustomSetting(true);
                        } else {
                            //changeCustomSetting(false);
                            b18DeviceUnitTv.setText(getResources().getString(R.string.setmi));
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                        }

//                        changeCustomSetting(i == 0);
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        }).show();
    }






    //清除数据
    private void clearB18Device() {
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.prompt))
                .setMessage("是否清除数据?")
                .setPositiveButton("Yest", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        B18BleConnManager.getB18BleConnManager().clearDeviceData();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }



    //解绑
    private void unBindB18Device() {
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.prompt))
                .setMessage(getResources().getString(R.string.string_devices_is_disconnected))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showLoadingDialog("Loading...");
                        B18BleConnManager.getB18BleConnManager().disConnB18Device(getActivity());
                        handler.sendEmptyMessageDelayed(0x00,2 * 1000);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 设置转腕亮屏开关
     */
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed())
                return;
            SharedPreferencesUtils.setParam(getActivity(), B18Constance.B18_IS_TURN_WRIST, isChecked);
            B18BleConnManager.getB18BleConnManager().setTurnWristStatus(isChecked);
        }
    };


    //设置运动目标
    private void setSportGoal() {
        ProfessionPick professionPick = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b18DeviceSportGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(getActivity(), Commont.SPORT_GOAL_STEP, Integer.valueOf(profession.trim()));
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sportGoalList) //min year in loop
                .dateChose("8000") // date chose when init popwindow
                .build();
        professionPick.showPopWin(getActivity());
    }



    //设置睡眠目标
    private void setSleepGoal() {
        ProfessionPick sleepProfession = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b18DeviceSleepGoalTv.setText(profession);
                        SharedPreferencesUtils.setParam(getActivity(), Commont.SLEEP_GOAL_KEY, profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(18) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        sleepProfession.showPopWin(getActivity());
    }

}
