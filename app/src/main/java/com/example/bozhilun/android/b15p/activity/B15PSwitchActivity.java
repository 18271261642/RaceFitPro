package com.example.bozhilun.android.b15p.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.SwitchBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.Dev;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.BractletDrinkSet;
import com.tjdL4.tjdmain.contr.BractletFuncSet;
import com.tjdL4.tjdmain.contr.BractletSedentarySet;
import com.tjdL4.tjdmain.contr.L4Command;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B31开关设置界面
 * Created by Admin
 * Date 2018/12/19
 */
public class B15PSwitchActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {


    private static final String TAG = "B31SwitchActivity";
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.save_text)
    TextView save_text;
    /**
     * 设备功能
     */
    //断连提醒
    @BindView(R.id.b31SwitchDisAlertTogg)
    ToggleButton b31SwitchDisAlertTogg;
    //佩戴检测
    @BindView(R.id.b31CheckWearToggleBtn)
    ToggleButton b31CheckWearToggleBtn;
    //心率自动检测
    @BindView(R.id.b31AutoHeartToggleBtn)
    ToggleButton b31AutoHeartToggleBtn;
    //血压自动检测
    @BindView(R.id.b30AutoBloadToggleBtn)
    ToggleButton b30AutoBloadToggleBtn;
    //血氧夜间检测
    @BindView(R.id.b31AutoBPOxyToggbleBtn)
    ToggleButton b31AutoBPOxyToggbleBtn;

    @BindView(R.id.line_dring_remind)
    RelativeLayout line_dring_remind;
    @BindView(R.id.view_drk)
    View view_drk;
//    /**
//     * 设备显示
//     */
//    //步数显示
//    @BindView(R.id.b31SwitchStepShow)
//    ToggleButton b31SwitchStepShow;
//    //距离显示
//    @BindView(R.id.b31SwitchrangeDis)
//    ToggleButton b31SwitchrangeDis;
//    //卡路里
//    @BindView(R.id.b31SwitchKcl)
//    ToggleButton b31SwitchKcl;
//    //心率显示
//    @BindView(R.id.b30SwitchHeart)
//    ToggleButton b30SwitchHeart;
//    //血压显示
//    @BindView(R.id.b3SwirrerBlood)
//    ToggleButton b3SwirrerBlood;
//    //查找手机
//    @BindView(R.id.b3SwirrerFind)
//    ToggleButton b3SwirrerFind;
//    //MAC 显示
//    @BindView(R.id.b3SwirrerMac)
//    ToggleButton b3SwirrerMac;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    readDeviceCusSetting();
                    break;
                case 0x01:
                    finish();
                    break;
                case 0x02:
                    L4Command.Brlt_FuncGet(new L4M.BTResultListenr() {
                        @Override
                        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
                            final String tTypeInfo = TypeInfo;
                            final String TempStr = StrData;
                            final Object TempObj = DataObj;
                            Log.e(TAG, "inTempStr:" + TempStr);

                            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                                handler.sendEmptyMessage(0x00);
                                return;
                            }
                            if (tTypeInfo.equals(L4M.SetFunc) && TempStr.equals(L4M.OK)) {
                                L4Command.Brlt_FuncGet(null);
                            }
                            if (tTypeInfo.equals(L4M.GetFunc) && TempStr.equals(L4M.Data)) {
                                BractletFuncSet.FuncSetData myAlarmClockData = (BractletFuncSet.FuncSetData) TempObj;
                                //sw_manage.setChecked(myAlarmClockData.mSW_manage);//读取开关状态
                                Log.e(TAG, "开关状态  亮屏 " + myAlarmClockData.mSW_manage
                                        + "  久坐 " + myAlarmClockData.mSW_sed + "  喝水 " + myAlarmClockData.mSW_drink
                                        + "  拍照 " + myAlarmClockData.mSW_camera + "  放丢失 " + myAlarmClockData.mSW_antilost);


                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, myAlarmClockData.mSW_antilost);//断开连接提醒--防丢失
                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSedentary, myAlarmClockData.mSW_sed);//久坐
                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWrists, myAlarmClockData.mSW_manage);//翻腕亮屏
                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDrink, myAlarmClockData.mSW_drink);//喝水
                                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISCamera, myAlarmClockData.mSW_camera);//拍照

                                handler.sendEmptyMessage(0x00);
                            }

                        }
                    });
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b15p_switch_layout);
        ButterKnife.bind(this);

        initViews();
        save_text.setVisibility(View.VISIBLE);
        //软件版本号
        String b25_sV = Dev.get_SWVerCode();
        //硬件版本号
        String b25_hv = Dev.get_HWVerCode();

        if ((b25_hv.equals("2.0") && b25_sV.equals("1.0"))
                || (b25_hv.equals("2.0") && b25_sV.equals("1.1"))
                || (b25_hv.equals("2.0") && b25_sV.equals("1.2"))
                || (b25_hv.equals("2.0") && b25_sV.equals("1.3"))
                || (b25_hv.equals("2.0") && b25_sV.equals("1.4"))
                || (b25_hv.equals("2.0") && b25_sV.equals("1.5"))
                || b25_hv.equals("3.0")) {
            line_dring_remind.setVisibility(View.GONE);
            view_drk.setVisibility(View.GONE);
        }

        /**
         * 读取设备内的开关
         */
        if (!this.isFinishing()) showLoadingDialog(getResources().getString(R.string.dlog));
        //活动刚创建就去读取开关
        handler.sendEmptyMessageDelayed(0x02, 200);
    }


    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_switch_setting));

        b31SwitchDisAlertTogg.setOnCheckedChangeListener(this); // --- 防止丢失
        b31CheckWearToggleBtn.setOnCheckedChangeListener(this); // --- 翻腕亮屏
        b31AutoHeartToggleBtn.setOnCheckedChangeListener(this);// --- 久坐提醒
        b31AutoBPOxyToggbleBtn.setOnCheckedChangeListener(this);// 摇一摇拍照
        b30AutoBloadToggleBtn.setOnCheckedChangeListener(this);// ---喝水提醒


//        b31SwitchStepShow.setOnCheckedChangeListener(this);// ---步数显示
//        b31SwitchrangeDis.setOnCheckedChangeListener(this);// ---距离显示
//        b31SwitchKcl.setOnCheckedChangeListener(this);// ---卡路里
//        b30SwitchHeart.setOnCheckedChangeListener(this);// ---心率显示
//        b3SwirrerBlood.setOnCheckedChangeListener(this);// ---血压显示
//        b3SwirrerFind.setOnCheckedChangeListener(this);// ---查找手机
//        b3SwirrerMac.setOnCheckedChangeListener(this);// ---MAC 显示

        if (switchBeans == null) switchBeans = new ArrayList<>();
        if (switchBeans.isEmpty() || switchBeans.size() < 4) {
            switchBeans.clear();
//            boolean wrists = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWrists, false);//翻腕亮屏
//            boolean sedentatry = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSedentary, false);//久坐
//            boolean drink = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDrink, false);//喝水
//            boolean camera = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCamera, false);//拍照
//            boolean disalert = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒--防丢失

            switchBeans.add(0, new SwitchBean("wrists", false));
            switchBeans.add(1, new SwitchBean("sedentatry", false));
            switchBeans.add(2, new SwitchBean("drink", false));
            switchBeans.add(3, new SwitchBean("camera", false));
            switchBeans.add(4, new SwitchBean("disalert", false));
        }
    }


    @OnClick({R.id.save_text, R.id.commentB30BackImg, R.id.line_sedentary_remind, R.id.line_dring_remind})
    public void onViewClicked(View view) {
        Intent intent = new Intent(B15PSwitchActivity.this, B15PShockDetailedActivity.class);
        switch (view.getId()) {
            case R.id.save_text:
                if (switchBeans == null) switchBeans = new ArrayList<>();
                if (switchBeans.isEmpty() || switchBeans.size() < 4) {
                    switchBeans.clear();
                    boolean wrists = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWrists, false);//翻腕亮屏
                    boolean sedentatry = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSedentary, false);//久坐
                    boolean drink = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDrink, false);//喝水
                    boolean camera = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCamera, false);//拍照
                    boolean disalert = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒--防丢失

                    switchBeans.add(0, new SwitchBean("wrists", wrists));
                    switchBeans.add(1, new SwitchBean("sedentatry", sedentatry));
                    switchBeans.add(2, new SwitchBean("drink", drink));
                    switchBeans.add(3, new SwitchBean("camera", camera));
                    switchBeans.add(4, new SwitchBean("disalert", disalert));
                }
                //翻腕亮屏,断开连接提醒,久坐,喝水,拍照
                Log.e(TAG, " 要设置的   亮屏" + switchBeans.get(0).isOpen()
                        + "   久坐" + switchBeans.get(1).isOpen()
                        + "  喝水" + switchBeans.get(2).isOpen()
                        + "  拍照" + switchBeans.get(3).isOpen()
                        + "  断连" + switchBeans.get(4).isOpen());
                showLoadingDialog(getResources().getString(R.string.dlog));
                boolean switchs = Commont.b15pSetSwitch(switchBeans.get(0).isOpen(),
                        switchBeans.get(1).isOpen(),
                        switchBeans.get(2).isOpen(),
                        switchBeans.get(3).isOpen(),
                        switchBeans.get(4).isOpen());
                Log.e(TAG, " 开关状态设置结果 " + switchs);
                /**
                 * 设置失败了，就读取设备里面的
                 */
                if (!switchs) {
                    Log.e(TAG, " 抬手亮屏设置失败去读取设备的 ");
                    ToastUtil.showShort(B15PSwitchActivity.this, getResources().getString(R.string.settings_fail));
                    //监听
                    L4Command.Brlt_FuncGet(btResultListenr);
                } else {
                    ToastUtil.showShort(B15PSwitchActivity.this, getResources().getString(R.string.settings_success));

                    handler.sendEmptyMessageDelayed(0x01, 1000);
                }
                break;
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.line_sedentary_remind:
                boolean sedentary = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSedentary, false);//久坐
                if (sedentary) {
                    intent.putExtra("type", "sedentary");
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.line_dring_remind:
                boolean dring = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDrink, false);//久坐
                if (dring) {
                    intent.putExtra("type", "dring");
                    startActivityForResult(intent, 1);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Log.d("------------", requestCode + "===" + resultCode);
            if (requestCode == 1) {
                if (!this.isFinishing()) showLoadingDialog(getResources().getString(R.string.dlog));
                switch (resultCode) {
                    case 0:
                        closeLoadingDialog();
                        break;
                    case 1:
                        //01:00
                        String mShockTimeS = data.getStringExtra("ShockTime");
                        String mStartShockTimeS = data.getStringExtra("StartShockTime");
                        String mStopShockTimeS = data.getStringExtra("StopShockTime");
                        Log.d("====1====", "间隔：" + mShockTimeS + "= 开始结束 ==" + mStartShockTimeS + "--" + mStopShockTimeS);

                        if (!WatchUtils.isEmpty(mShockTimeS)) {
                            String[] alTimeSplit = mShockTimeS.split("[:]");
                            if (alTimeSplit.length >= 2) {
                                int hour = Integer.valueOf(alTimeSplit[0]);
                                int minute = Integer.valueOf(alTimeSplit[1]);

                                String[] startimeS = mStartShockTimeS.split("[:]");
                                String[] endtimeS = mStopShockTimeS.split("[:]");

                                Log.e(TAG, "久坐    间隔： " + (hour * 60 + minute) + "分钟   开始：" + startimeS + "   结束：" + endtimeS);

                                //设置数据对象
                                BractletSedentarySet.SedentarySetData mySedSetData = new BractletSedentarySet.SedentarySetData();
                                mySedSetData.allminutes = (hour * 60) + minute;//minute 总分钟
                                //设置
                                String ret = L4Command.SedentarySet(mySedSetData);/*ret  返回值类型文档最下面*/
                                if (ret.equals("OK")) {
                                    Toast.makeText(B15PSwitchActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
//                                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSedentary, true);//久坐
//                                    Message message = Message.obtain();
//                                    message.what = 0x02;
//                                    message.obj = true;
//                                    handler.sendMessageDelayed(message, 2000);
                                }
                            }
                        }

//                        String substringS = mShockTimeS.substring(0, mShockTimeS.length() - 1);
//                        String[] startimeS = mStartShockTimeS.split("[:]");
//                        String[] endtimeS = mStopShockTimeS.split("[:]");


//                        Log.e(TAG, "久坐    间隔： " + (Integer.valueOf(substringS.replace(" ", "").trim()) * 60) + "   开始：" + startimeS + "   结束：" + endtimeS);
//                        MyApp.getInstance().getmW30SBLEManage().setSitNotification(Integer.valueOf(startimeS[0]),
//                                Integer.valueOf(startimeS[1]), Integer.valueOf(endtimeS[0]),
//                                Integer.valueOf(endtimeS[1]), Integer.valueOf(substringS), true);
//                        //久坐
//                        SharedPreferencesUtils.setParam(B15PSwitchActivity.this, "w30sSedentaryRemind", true);
//                        b31AutoHeartToggleBtn.setChecked(true);
//                        b31AutoHeartToggleBtn.postInvalidate();
                        break;
                    case 2:
                        String mShockTimeD = data.getStringExtra("ShockTime");
                        String mStartShockTimeD = data.getStringExtra("StartShockTime");
                        String mStopShockTimeD = data.getStringExtra("StopShockTime");
                        Log.d("====2====", mShockTimeD + "===" + mStartShockTimeD + "--" + mStopShockTimeD);


                        if (!WatchUtils.isEmpty(mShockTimeD)) {
                            String[] alTimeSplit = mShockTimeD.split("[:]");
                            if (alTimeSplit.length >= 2) {
                                int hour = Integer.valueOf(alTimeSplit[0]);
                                int minute = Integer.valueOf(alTimeSplit[1]);

                                String[] startimeS = mStartShockTimeD.split("[:]");
                                String[] endtimeS = mStopShockTimeD.split("[:]");

                                Log.e(TAG, "喝水    间隔： " + (hour * 60 + minute) + "分钟   开始：" + startimeS + "   结束：" + endtimeS);

                                //设置数据对象
                                BractletDrinkSet.DrinkSetData myDrinkSetData = new BractletDrinkSet.DrinkSetData();
                                myDrinkSetData.allminutes = (hour * 60) + minute;//minute 总分钟
                                //设置
                                String retD = L4Command.DrinkSet(myDrinkSetData); /*ret  返回值类型在文档最下面*/
                                if (retD.equals("OK")) {
                                    Toast.makeText(B15PSwitchActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
//                                    SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDrink, true);//喝水
//                                    Message message = Message.obtain();
//                                    message.what = 0x03;
//                                    message.obj = true;
//                                    handler.sendMessageDelayed(message, 2000);
                                }
                            }
                        }
//
//                        String substringD = mShockTimeD.substring(0, mShockTimeD.length() - 1);
//                        String[] startimeD = mStartShockTimeD.split("[:]");
//                        String[] endtimeD = mStopShockTimeD.split("[:]");
//
//
//                        Log.e(TAG, "喝水    间隔： " + (Integer.valueOf(substringD.replace(" ", "").trim()) * 60) + "   开始：" + startimeD + "   结束：" + endtimeD);
//
//                        //设置数据对象
//                        BractletDrinkSet.DrinkSetData myDrinkSetData = new BractletDrinkSet.DrinkSetData();
//                        myDrinkSetData.allminutes = Integer.valueOf(substringD.replace(" ", "").trim()) * 60;//minute 总分钟
//                        //设置
//                        String retD = L4Command.DrinkSet(myDrinkSetData); /*ret  返回值类型在文档最下面*/
//                        if (retD.equals("OK")) {
////                            Toast.makeText(B15PSwitchActivity.this, getResources().getString(R.string.settings_success), Toast.LENGTH_SHORT).show();
//                            SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDrink, true);//喝水
//                            Message message = Message.obtain();
//                            message.what = 0x03;
//                            message.obj = true;
//                            handler.sendMessageDelayed(message, 2000);
//                        }

//                        MyApp.getInstance().getmW30SBLEManage().setDrinkingNotification(Integer.valueOf(startimeD[0]),
//                                Integer.valueOf(startimeD[1]), Integer.valueOf(endtimeD[0]),
//                                Integer.valueOf(endtimeD[1]), Integer.valueOf(substringD), true);
//
//                        //喝水
//                        SharedPreferencesUtils.setParam(B15PSwitchActivity.this, "w30sDringRemind", true);
//                        b30AutoBloadToggleBtn.setChecked(true);
//                        b30AutoBloadToggleBtn.postInvalidate();
                        break;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.e(TAG, "开关状态   onCheckedChanged状态改变 " + (buttonView.isPressed() ? "  是用户点击  " : "  是界面更新  "));
        if (!buttonView.isPressed())
            return;
        switch (buttonView.getId()) {
            case R.id.b31CheckWearToggleBtn:
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWrists, isChecked);//翻腕亮屏
//                booleansNew[0] = isChecked;

                switchBeans.set(0, new SwitchBean("wrists", isChecked));

                break;
            case R.id.b31AutoHeartToggleBtn:
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSedentary, isChecked);//久坐
//                booleansNew[1] = isChecked;
                switchBeans.set(1, new SwitchBean("sedentatry", isChecked));

                if (isChecked) {
                    boolean sedentary = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSedentary, false);//久坐
                    if (sedentary) {
                        Intent intent = new Intent(B15PSwitchActivity.this, B15PShockDetailedActivity.class);
                        intent.putExtra("type", "sedentary");
                        startActivityForResult(intent, 1);
                    }
                }
                break;
            case R.id.b30AutoBloadToggleBtn:
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDrink, isChecked);//喝水
//                booleansNew[2] = isChecked;
                switchBeans.set(2, new SwitchBean("drink", isChecked));

                if (isChecked) {
                    boolean dring = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDrink, false);//久坐
                    if (dring) {
                        Intent intent = new Intent(B15PSwitchActivity.this, B15PShockDetailedActivity.class);
                        intent.putExtra("type", "dring");
                        startActivityForResult(intent, 1);
                    }
                }
                break;
            case R.id.b31AutoBPOxyToggbleBtn:
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISCamera, isChecked);//拍照
//                booleansNew[3] = isChecked;
                switchBeans.add(3, new SwitchBean("camera", isChecked));

                break;
            case R.id.b31SwitchDisAlertTogg:    //断连提醒
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, isChecked);//断开连接提醒--防丢失
//                booleansNew[4] = isChecked;
                switchBeans.set(4, new SwitchBean("disalert", isChecked));
                break;
//            case R.id.b31SwitchStepShow://步数显示
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "StepShow", isChecked);//步数
//                break;
//            case R.id.b31SwitchrangeDis: //距离显示
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "DisShow", isChecked);//距离
//                break;
//            case R.id.b31SwitchKcl://卡路里
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "KclShow", isChecked);//卡路里
//                break;
//            case R.id.b30SwitchHeart://心率显示
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "HeartShow", isChecked);//心率
//                break;
//            case R.id.b3SwirrerBlood: //血压显示
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "BloopShow", isChecked);//血压
//                break;
//            case R.id.b3SwirrerFind://查找手机
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "FindPhoneShow", isChecked);//查手机
//                break;
//            case R.id.b3SwirrerMac://MAC 显示
//                SharedPreferencesUtils.setParam(MyApp.getContext(), "MacShow", isChecked);//mac
//                break;
        }
    }


    /**
     * 翻腕亮屏,久坐,喝水,拍照,断开连接提醒
     */
//    boolean booleans[] = new boolean[5];
//    boolean booleansNew[] =  new boolean[5];
    List<SwitchBean> switchBeans = new ArrayList<>();

    /**
     * 读取本地主开关状态并设置
     */
    private void readDeviceCusSetting() {
        closeLoadingDialog();
        if (MyCommandManager.DEVICENAME == null)
            return;
//        booleans[0] = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWrists, false);//翻腕亮屏
//        booleans[1] = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSedentary, false);//久坐
//        booleans[2] = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDrink, false);//喝水
//        booleans[3] = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCamera, false);//拍照
//        booleans[4] = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒--防丢失
//
//        b31CheckWearToggleBtn.setChecked(booleans[0]); // --- 翻腕亮屏
//        b31AutoHeartToggleBtn.setChecked(booleans[1]);// --- 久坐提醒
//        b30AutoBloadToggleBtn.setChecked(booleans[2]);// ---喝水提醒
//        b31AutoBPOxyToggbleBtn.setChecked(booleans[3]);// 摇一摇拍照
//        b31SwitchDisAlertTogg.setChecked(booleans[4]); // --- 防止丢失

        boolean wrists = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISWrists, false);//翻腕亮屏
        boolean sedentatry = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSedentary, false);//久坐
        boolean drink = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDrink, false);//喝水
        boolean camera = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCamera, false);//拍照
        boolean disalert = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISDisAlert, false);//断开连接提醒--防丢失

        if (switchBeans.size() > 4) {
            switchBeans.clear();

            switchBeans.add(0, new SwitchBean("wrists", wrists));
            switchBeans.add(1, new SwitchBean("sedentatry", sedentatry));
            switchBeans.add(2, new SwitchBean("drink", drink));
            switchBeans.add(3, new SwitchBean("camera", camera));
            switchBeans.add(4, new SwitchBean("disalert", disalert));
        } else {
            switchBeans.set(0, new SwitchBean("wrists", wrists));
            switchBeans.set(1, new SwitchBean("sedentatry", sedentatry));
            switchBeans.set(2, new SwitchBean("drink", drink));
            switchBeans.set(3, new SwitchBean("camera", camera));
            switchBeans.set(4, new SwitchBean("disalert", disalert));
        }

        b31CheckWearToggleBtn.setChecked(wrists); // --- 翻腕亮屏
        b31AutoHeartToggleBtn.setChecked(sedentatry);// --- 久坐提醒
        b30AutoBloadToggleBtn.setChecked(drink);// ---喝水提醒
        b31AutoBPOxyToggbleBtn.setChecked(camera);// 摇一摇拍照
        b31SwitchDisAlertTogg.setChecked(disalert); // --- 防止丢失
    }


    /**
     * 读取开关状态
     */
    L4M.BTResultListenr btResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {
            final String tTypeInfo = TypeInfo;
            final String TempStr = StrData;
            final Object TempObj = DataObj;
            Log.e(TAG, "inTempStr:" + TempStr);

            if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                handler.sendEmptyMessage(0x00);
                return;
            }
            if (tTypeInfo.equals(L4M.SetFunc) && TempStr.equals(L4M.OK)) {
                L4Command.Brlt_FuncGet(null);
            }
            if (tTypeInfo.equals(L4M.GetFunc) && TempStr.equals(L4M.Data)) {
                BractletFuncSet.FuncSetData myAlarmClockData = (BractletFuncSet.FuncSetData) TempObj;
                //sw_manage.setChecked(myAlarmClockData.mSW_manage);//读取开关状态
                Log.e(TAG, "开关状态  亮屏 " + myAlarmClockData.mSW_manage
                        + "  久坐 " + myAlarmClockData.mSW_sed + "  喝水 " + myAlarmClockData.mSW_drink
                        + "  拍照 " + myAlarmClockData.mSW_camera + "  放丢失 " + myAlarmClockData.mSW_antilost);


                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDisAlert, myAlarmClockData.mSW_antilost);//断开连接提醒--防丢失
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISSedentary, myAlarmClockData.mSW_sed);//久坐
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISWrists, myAlarmClockData.mSW_manage);//翻腕亮屏
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISDrink, myAlarmClockData.mSW_drink);//喝水
                SharedPreferencesUtils.setParam(MyApp.getContext(), Commont.ISCamera, myAlarmClockData.mSW_camera);//拍照

                handler.sendEmptyMessageDelayed(0x00, 200);
            }

        }
    };

}
