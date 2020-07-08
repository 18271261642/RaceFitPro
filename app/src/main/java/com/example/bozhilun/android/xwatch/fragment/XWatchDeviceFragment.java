package com.example.bozhilun.android.xwatch.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.w30s.carema.W30sCameraActivity;
import com.example.bozhilun.android.w30s.wxsport.WXSportActivity;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.example.bozhilun.android.xwatch.ble.XWatchGoalListener;
import com.example.bozhilun.android.xwatch.ble.XWatchTimeModelListener;
import com.example.bozhilun.android.xwatch.ble.XWatchUnitListener;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2020/2/20
 */
public class XWatchDeviceFragment extends Fragment {

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.xWatchDeviceSportGoalTv)
    TextView xWatchDeviceSportGoalTv;
    @BindView(R.id.xWatchDeviceSleepGoalTv)
    TextView xWatchDeviceSleepGoalTv;
    @BindView(R.id.xWatchDeviceUnitTv)
    TextView xWatchDeviceUnitTv;
    Unbinder unbinder;


    @BindView(R.id.xWatchDeviceSleepRel)
    RelativeLayout xWatchDeviceSleepRel;
    @BindView(R.id.b18DeviceUnitRel)
    RelativeLayout b18DeviceUnitRel;


    FragmentManager fragmentManager;
    @BindView(R.id.sWatchFindWatchRel)
    RelativeLayout sWatchFindWatchRel;


    private XWatchBleAnalysis xWatchBleAnalysis;


    //运动目标
    ArrayList<String> sportGoalList;

    //公英制
    private AlertDialog.Builder unitBuilder;
    //时间格式
    private AlertDialog.Builder timeTypeBuilder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xWatchBleAnalysis = XWatchBleAnalysis.getW37DataAnalysis();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_x_watch_device_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        initData();

        return view;
    }

    private void initData() {
        sportGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sportGoalList.add(i + "");
        }
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.menu_settings));
        if(MyCommandManager.DEVICENAME == null)
            return;
        if (MyCommandManager.DEVICENAME.equals("XWatch")) {
            xWatchDeviceSleepRel.setVisibility(View.VISIBLE);
            b18DeviceUnitRel.setVisibility(View.VISIBLE);
            sWatchFindWatchRel.setVisibility(View.GONE);
        }

        try {
            int sportGoal = (int) SharedPreferencesUtils.getParam(getActivity(), Commont.SPORT_GOAL_STEP, 10000);
            xWatchDeviceSportGoalTv.setText(sportGoal + "");

            xWatchBleAnalysis.getDeviceSportGoal(new XWatchGoalListener() {
                @Override
                public void backDeviceGoal(int goal) {
                    if (getActivity().isFinishing()) //647249
                        return;
                    xWatchDeviceSportGoalTv.setText(goal + "");
                    SharedPreferencesUtils.setParam(getActivity(), Commont.SPORT_GOAL_STEP, goal);
                    readDeviceTimeModel();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //时间模式
    private void readDeviceTimeModel() {
        xWatchBleAnalysis.getWatchTimeType(new XWatchTimeModelListener() {
            @Override
            public void deviceeTimeModel(int model) {
                xWatchDeviceSleepGoalTv.setText(model == 0 ? "12h" : "24h");
                readDeviceUnit();
            }
        });
    }


    //距离单位 km or mile
    private void readDeviceUnit() {
        xWatchBleAnalysis.getDeviceKmUnit(new XWatchUnitListener() {
            @Override
            public void backDeviceUnit(int unit) {
                xWatchDeviceUnitTv.setText(unit == 0 ? getResources().getString(R.string.setkm) : getResources().getString(R.string.setmi));
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.commentB30BackImg, R.id.xWatchDeviceSportRel,
            R.id.b18DeviceUnitRel, R.id.xWatchDeviceMsgRel,
            R.id.xWatchDeviceAlarmRel, R.id.xWatchDeviceSleepRel,
            R.id.xWatchDeviceunBindRel, R.id.xWatchDevicePhotoRel,
            R.id.xWatchWxSportRel,R.id.sWatchFindWatchRel})
    public void onClick(View view) {
        fragmentManager = getFragmentManager();
        if (fragmentManager == null)
            return;
        if(MyCommandManager.DEVICENAME == null)
            return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                getActivity().finish();
                break;
            case R.id.xWatchDeviceSportRel:     //运动目标
                setSportGoal();
                break;
            case R.id.xWatchDeviceSleepRel: //时间格式
                setTimeType();
                break;
            case R.id.b18DeviceUnitRel:     //单位
                showUnitDialog();
                break;
            case R.id.xWatchDeviceMsgRel:   //消息提醒
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.xWatchDeviceFrameLayout, new XWatchMsgAlertFragment())
                        .commit();
                break;
            case R.id.xWatchDeviceAlarmRel: //闹钟
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.xWatchDeviceFrameLayout, MyCommandManager.DEVICENAME.equals("XWatch") ? new XWatchAlarmFragment() : new SWatchAlarmFragment())
                        .commit();
                break;
            case R.id.xWatchWxSportRel:     //微信运动
                Intent intent = new Intent(getActivity(), WXSportActivity.class);
                intent.putExtra("bleName", "XWatch");
                startActivity(intent);
                break;
            case R.id.xWatchDeviceunBindRel:    //断开连接
                disDeviceBle();
                break;
            case R.id.sWatchFindWatchRel:   //SWatch查找手表
                xWatchBleAnalysis.findSWatchDevice();
                break;
            case R.id.xWatchDevicePhotoRel:     //摇一摇拍照
                if (AndPermission.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) {
                    openCamera();
                } else {
                    AndPermission.with(this)
                            .runtime()
                            .permission(Permission.CAMERA, Permission.RECORD_AUDIO)
                            .rationale(rationale)
                            .onGranted(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {
                                    openCamera();
                                }
                            })
                            .start();
                }
                break;
        }
    }


    private void openCamera() {
        if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("SWatch")) {
            XWatchBleAnalysis.getW37DataAnalysis().openOrCloseCamera(1);
        }
        startActivity(new Intent(getActivity(), W30sCameraActivity.class));
    }


    //断开连接
    private void disDeviceBle() {
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.prompt))
                .content(getResources().getString(R.string.confirm_unbind_strap))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        unbindBle();
                    }
                }).show();
    }

    private void unbindBle() {
        MyApp.getInstance().getW37BleOperateManager().disBleDeviceByMac(MyApp.getInstance().getMacAddress());
        MyApp.getInstance().setMacAddress("");
        startActivity(new Intent(getActivity(), NewSearchActivity.class));
        getActivity().finish();

    }

    //设置时间格式
    private void setTimeType() {
        String[] timeItem = new String[]{"12h", "24h"};
        timeTypeBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.time_forma))
                .setItems(timeItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        xWatchDeviceSleepGoalTv.setText(which == 0 ? "12h" : "24h");
                        xWatchBleAnalysis.setWatchTimeType(which);
                    }
                }).setNegativeButton(getResources().getText(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        timeTypeBuilder.show();
    }


    //设置运动目标
    private void setSportGoal() {
        ProfessionPick professionPick = new ProfessionPick.Builder(getActivity(),
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        xWatchDeviceSportGoalTv.setText(profession);
                        xWatchBleAnalysis.setDeviceSportGoal(Integer.valueOf(profession.trim()));
                        SharedPreferencesUtils.setParam(getActivity(), Commont.SPORT_GOAL_STEP, Integer.valueOf(profession));
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


    //展示公英制
    private void showUnitDialog() {

        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        unitBuilder = new AlertDialog.Builder(getActivity());
        unitBuilder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0) {
                            xWatchDeviceUnitTv.setText(getResources().getString(R.string.setkm));
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
                            // changeCustomSetting(true);
                        } else {
                            //changeCustomSetting(false);
                            xWatchDeviceUnitTv.setText(getResources().getString(R.string.setmi));
                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSystem, false);//是否为公制
                        }
                        //设置公英制
                        xWatchBleAnalysis.setDeviceKmUnit(i);
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        }).show();
    }


    private Rationale rationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            AndPermission.with(XWatchDeviceFragment.this).runtime().setting().start(1001);
            executor.execute();
        }
    };

}
