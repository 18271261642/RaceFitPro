package com.example.bozhilun.android.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.B15pHomeActivity;
import com.example.bozhilun.android.b18.B18HomeActivity;
import com.example.bozhilun.android.b30.B30HomeActivity;
import com.example.bozhilun.android.b31.B31HomeActivity;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.h8.H8HomeActivity;
import com.example.bozhilun.android.w30s.ble.W37HomeActivity;
import com.example.bozhilun.android.xwatch.XWatchHomeActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;


/**
 * 启动页
 */
public class LaunchActivity extends WatchBaseActivity {

    private static final String TAG = "LaunchActivity";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    switchLoginUser();
                    break;

            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch_layout);


        initData();


        //final boolean isGuide = (boolean) SharedPreferencesUtils.getParam(LaunchActivity.this,"isGuide",false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1001);
//                Message message = handler.obtainMessage();
//                message.what = 1001;
//                message.obj = isGuide;
//                handler.sendMessage(message);
            }
        }, 2 * 1000);

    }

    @Override
    public void hideTitleStute() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void initData() {

        SharedPreferencesUtils.setParam(this, "save_curr_time", System.currentTimeMillis() / 1000 + "");
        SharedPreferencesUtils.setParam(this, "curr_code", 0);

        //B30目标步数 默认8000
        int goalStep = (int) SharedPreferencesUtils.getParam(this,Commont.SPORT_GOAL_STEP,0);
        if(goalStep==0){
            SharedPreferencesUtils.setParam(this,Commont.SPORT_GOAL_STEP,10000);
        }
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(this,Commont.SLEEP_GOAL_KEY,"");
        if(WatchUtils.isEmpty(b30SleepGoal)){
            SharedPreferencesUtils.setParam(MyApp.getContext(),Commont.SLEEP_GOAL_KEY,"8.0");
        }
        //B30的默认密码
        String b30Pwd = (String) SharedPreferencesUtils.getParam(this,Commont.DEVICESCODE,"");
        if(WatchUtils.isEmpty(b30Pwd)){
            SharedPreferencesUtils.setParam(this,Commont.DEVICESCODE,"0000");
        }

        //SharedPreferencesUtils.setParam(LaunchActivity.this, "w30sunit", true);

        //H8默认目标步数
        String defaultTagStep = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "settagsteps", "");
        if(WatchUtils.isEmpty(defaultTagStep))
            SharedPreferencesUtils.setParam(this, "settagsteps", "10000");

        //H8的公英制，默认公制
        String h8Unit = (String) SharedPreferencesUtils.getParam(LaunchActivity.this,Commont.H8_UNIT,"km");
        if(WatchUtils.isEmpty(h8Unit))
            SharedPreferencesUtils.setParam(LaunchActivity.this,Commont.H8_UNIT,"km");


        //H8手表初始化消息提醒开关
        String isFirst = (String) SharedPreferencesUtils.getParam(LaunchActivity.this, "msgfirst", "");
        if (WatchUtils.isEmpty(isFirst)) {
            SharedPreferencesUtils.setParam(LaunchActivity.this, "msgfirst", "isfirst");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "weixinmsg", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "msg", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "qqmsg", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Viber", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Twitteraa", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "facebook", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Whatsapp", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Instagrambutton", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "laidian", "0");
            SharedPreferencesUtils.setParam(MyApp.getContext(), "laidianphone", "off");
        }


        //链接: https://pan.baidu.com/s/11fs6K6RpMSQDU_n4l7-TgA 提取码: wp8m 复制这段内容后打开百度网盘手机App，操作更方便哦

    }

    //判断进入的页面
    private void switchLoginUser() {
        String userId = (String) SharedPreferencesUtils.readObject(LaunchActivity.this, "userId");
        //判断有没有登录
        if (!WatchUtils.isEmpty(userId)) {
            Log.e("GuideActivity", "--------蓝牙---" + SharedPreferencesUtils.readObject(LaunchActivity.this,  Commont.BLENAME));
            String btooth = (String) SharedPreferencesUtils.readObject(LaunchActivity.this,  Commont.BLENAME);
            if (!WatchUtils.isEmpty(btooth) ) {
                if ("bozlun".equals(btooth)) {    //H8 手表
                    MyApp.getInstance().h8BleManagerInstance();
                    //handler.sendEmptyMessage(1002);
                    startActivity(H8HomeActivity.class);
                } else if ("H9".equals(btooth)) {   // H9 手表
                    startActivity(H9HomeActivity.class);
                } else if ("W30".equals(btooth) || "w30".equals(btooth) || "W31".equals(btooth) || "W37".equals(btooth)) {
                    MyApp.getInstance().getW37ConnStatusService();
                    startActivity(W37HomeActivity.class);
                } else if ("B30".equals(btooth) || "B36".equals(btooth) ||"B36M".equals(btooth)) {      //B30和B36
                    startActivity(B30HomeActivity.class);
                }else if("Ringmii".equals(btooth)){
                    startActivity(B30HomeActivity.class);
                } else if("B31".equals(btooth)||"B31S".equals(btooth)||"500S".equals(btooth)
                        || btooth.equals("E Watch") || btooth.contains("YWK") || btooth.contains("SpO2")){
                    startActivity(B31HomeActivity.class);
                }else if ("B15P".equals(btooth)||"B25".equals(btooth)) {
                    startActivity(B15pHomeActivity.class);
                }
                else if("B18".equals(btooth) || "B16".equals(btooth)){
                    startActivity(B18HomeActivity.class);
                }else if("XWatch".equals(btooth) || "SWatch".equals(btooth)){
                    MyApp.getInstance().getW37ConnStatusService();
                    startActivity(XWatchHomeActivity.class);
                }
                else {
                    startActivity(NewSearchActivity.class);
                }
            } else {
                startActivity(NewSearchActivity.class);
            }
            finish();
        } else {
            startActivity(FastLoginActivity.class);
        }
    }
}
