package com.example.bozhilun.android.b30;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.b15pdb.B15PDBCommont;
import com.example.bozhilun.android.b15p.common.B15PContentState;
import com.example.bozhilun.android.b15p.interfaces.ConntentStuteListenter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.bean.UpDataBean;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.Dev;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.L4Command;
import com.tjdL4.tjdmain.ctrls.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class B30DufActivity extends WatchBaseActivity implements UpdateManager.OnOTAUpdateListener, RequestView, ConntentStuteListenter {
    private static final String TAG = "B30DufActivity";
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @BindView(R.id.progress_number)
    TextView progressNumber;
    @BindView(R.id.b30DufBtn)
    Button btnStartUp;
    @BindView(R.id.up_prooss)
    LinearLayout up_prooss;
    @BindView(R.id.progressBar_upgrade)
    ProgressBar proBar;
    int b15pProgress = 0;


    private String upDataStringUrl = "";//固件升级链接
    private String bleName = "null";
    private UpdateManager updateManager = null;
    private int upDataState = 0x00;
    private String FileStringPath = Environment.getExternalStorageDirectory().getPath() + "/Android/com.bozlun.healthday.android/cache/" + System.currentTimeMillis() + "B25_DEV.bin";
    private String FileStringPath2 = Environment.getExternalStorageDirectory().getPath() + "/Android/com.bozlun.healthday.android/cache/" + System.currentTimeMillis();
    private String FileStringPath3 = Environment.getExternalStorageDirectory().getPath() + "/Android/com.bozlun.healthday.android/cache/";
    private boolean isUping = false;//是否正在升级中
    private boolean isUping2 = true;//是否正在升级中
    private B15PContentState b15PContentState = null;
    private L4M.BTStReceiver dataReceiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_dfu);
        ButterKnife.bind(this);

        //注册下载状态监听
        Aria.download(this).register();

        dataReceiver = B15PContentState.getInstance().getDataReceiver();
        if (dataReceiver != null) L4M.registerBTStReceiver(this, dataReceiver);
        b15PContentState = B15PContentState.getInstance();
        b15PContentState.setB15PContentState(this);

        Intent intent = getIntent();
        bleName = intent.getStringExtra("type");
        if (WatchUtils.isEmpty(bleName)) {
            bleName = "null";
        }
        if (Commont.isDebug) Log.e(TAG, "  值 " + bleName);

        initViews();
    }


    @Override
    public void b15p_Connection_State(int state) {
        //正在连接
        if (state == 1) {
            if (Commont.isDebug) Log.e(TAG, "--B15P--正在链接");

//            MyCommandManager.DEVICENAME = null;
        }
        //已连接
        else if (state == 2) {
            String s = L4M.GetConnectedMAC();
//            SharedPreferencesUtils.saveObject(MyApp.getInstance(), Commont.BLEMAC, s);
            if (Commont.isDebug)
                Log.e(TAG, "--B15P--已连接 " + "====" + s + "------" + L4M.GetConnecteddName());
//            MyCommandManager.DEVICENAME = (WatchUtils.isEmpty(L4M.GetConnecteddName()) ? "B15P" : L4M.GetConnecteddName());


            if (Commont.isDebug) Log.e(TAG, "--B15P--已连接 " + "监听同步时间");
            L4M.SetResultListener(mBTResultListenr);
            L4M.InitData(MyApp.getInstance(), 1, 0);
            L4M.SysnALLData();
            L4M.SysnALLData();
            if (!isUping2) handler.sendEmptyMessageDelayed(0x03, 3500);
        }
        //未连接
        else {
            if (Commont.isDebug) Log.e(TAG, "--B15P--未连接");
//            MyCommandManager.DEVICENAME = null;
        }
    }


    /**
     * 在这里处理任务执行中的状态，如进度进度条的刷新
     *
     * @param task
     */
    //@Download.onTaskRunning
    protected void running(DownloadTask task) {
        if (Commont.isDebug) Log.e(TAG, "===下载中...");
    }

    /**
     * 在这里处理任务完成的状态
     *
     * @param task
     */
    //@Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        if (Commont.isDebug) Log.e(TAG, "===下载完成...");
        upDataState = 0x01;
    }

    private void initViews() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        updateManager = new UpdateManager(this);
        updateManager.setOnOTAUpdateListener(this);

        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.firmware_upgrade));
    }


    private String typeName = "B25";
    private String clientType = "B15P";

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUping) {
            try {
                deleteDirectory(FileStringPath3);
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
            if (bleName.equals("B25")) {
                FileStringPath = Environment.getExternalStorageDirectory().getPath() + "/Android/com.bozlun.healthday.android/cache/" + System.currentTimeMillis() + "/B25_DEV.bin";

                /**
                 * 固件版本
                 *
                 * 此处判断---2.0的为仓库的版本，升级时用的OTA和  后面烧录过得 OTA 有所不同
                 * 原来仓库的没有喝水提醒
                 */
                //固件版本
                String b25_hv = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_HV", "2.0");
                //软件版本
                String b25_sV = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_SV", "1.0");

                if ((b25_hv.equals("2.0") && b25_sV.equals("1.0"))
                        || (b25_hv.equals("2.0") && b25_sV.equals("1.1"))
                        || (b25_hv.equals("2.0") && b25_sV.equals("1.2"))
                        || (b25_hv.equals("2.0") && b25_sV.equals("1.3"))
                        || (b25_hv.equals("2.0") && b25_sV.equals("1.4"))
                        || (b25_hv.equals("2.0") && b25_sV.equals("1.5"))
                        || b25_hv.equals("3.0")) {
                    typeName = "B25";
                    clientType = "B25_OTA_WH";
                    if (Commont.isDebug) Log.e(TAG, "  仓库版本获取升级  " + b25_hv + "    " + b25_sV);
                    //获取版本失败，重新获取
                    getNetWorke(typeName, clientType);
                } else {
                    typeName = "B25";
                    clientType = "B25_OTA";
                    if (Commont.isDebug) Log.e(TAG, "  非仓库版本获取升级  " + b25_hv + "    " + b25_sV);
                    //获取版本失败，重新获取
                    getNetWorke(typeName, clientType);
                }

            } else if (bleName.equals("B15P")) {
                FileStringPath = Environment.getExternalStorageDirectory().getPath() + "/Android/com.bozlun.healthday.android/cache/" + System.currentTimeMillis() + "/B15P_DEV.bin";
                typeName = "B15P";
                clientType = "B15P_OTA";
                //获取版本失败，重新获取
                getNetWorke(typeName, clientType);
            } else {
                //String w30S_v = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
            }
        }
    }


    @Override
    protected void onDestroy() {
        Aria.download(this).unRegister();
        super.onDestroy();
        if (dataReceiver != null) {
            L4M.unregisterBTStReceiver(this, dataReceiver);
        }
        B15PContentState.getInstance().stopSeachDevices();
        if (updateManager != null) updateManager = null;
    }


    @OnClick({R.id.commentB30BackImg, R.id.b30DufBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
//                if (isUping) shhowTips();
//                else finish();
                finish();
                break;
            case R.id.b30DufBtn:
                if (btnStartUp.isEnabled()) {
                    startUp();
                }
                break;
        }
    }


    void startUp() {
        if (upDataState == 0x01) {//可升级的状态
            //   FileStringPath    bin文件(固件)路径
            //	 tempAddr   设备mac地址
            String tempAddr = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
            if (!WatchUtils.isEmpty(FileStringPath) && !WatchUtils.isEmpty(tempAddr)) {
                if (existsFile(FileStringPath)) {
                    //if (Commont.isDebug)Log.e(TAG, " bin文件(固件)路径  " + FileStringPath + " \n  设备mac地址  " + tempAddr);
                    //if (Commont.isDebug)Log.e(TAG, "----2222----");
                    if (updateManager == null) {
                        updateManager = new UpdateManager(B30DufActivity.this);
                        updateManager.setOnOTAUpdateListener(B30DufActivity.this);
                    }

                    updateManager.updateOTA(FileStringPath, tempAddr);
                } else {
                    if (!WatchUtils.isEmpty(upDataStringUrl)) {
                        Toast.makeText(B30DufActivity.this,
                                getResources().getString(R.string.string_w30s_ota_file), Toast.LENGTH_SHORT).show();//OTA包不存在，正在下载
                        Aria.download(this)
                                .load(String.valueOf(upDataStringUrl))     //读取下载地址
                                .setFilePath(FileStringPath) //设置文件保存的完整路径
                                .ignoreFilePathOccupy()
                        .create();   //启动下载
                    } else {
                        getNetWorke(typeName, clientType);
                    }
                }

            }
        } else if (upDataState == 0x02) {//版本获取失败的状态
            //获取版本失败，重新获取
            getNetWorke(typeName, clientType);
        }
    }

    /**
     * 判断文件是否存在
     */
    public boolean existsFile(String filePath) {
        File file = new File(filePath);
        if (Commont.isDebug) Log.e(TAG, "====  " + file + "===" + file.exists());
        if (file.exists()) {
            return true;
        }
        return false;
    }


    /**
     * OTA 升级相关
     *
     * @param len
     * @param current
     */
    @Override
    public void progressChanged(int len, int current) {
        isUping = true;
        isUping2 = true;
        int progre_num = current * 100 / len; //升级进度
        b15pProgress = progre_num;
        if (Commont.isDebug) Log.e(TAG, "=========== " + progre_num + "   升级中>>>>>>");
        progressNumber.setText(getResources().getString(R.string.auto_upgrade));
        proBar.setIndeterminate(false);
        proBar.setProgress(progre_num);

        // 21   22  23  24  1  2  3  4  5  6  7  8  9  10  11  12  13
        if (!isFinishing() && up_prooss != null && up_prooss.getVisibility() == View.INVISIBLE) {
            up_prooss.setVisibility(View.VISIBLE);
        }
        btnStartUp.setEnabled(false);//默认禁止点击
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
    }


    int upDataCount = 0;//自动重新升级的次数重新

    @Override
    public void finishOTA(boolean success) {

        Commont.HasOTA = true;//全局配置说明这次启动APP有过OTA升级
//        isUping = false;//赋值没在升级
        if (Commont.isDebug)
            Log.e(TAG, "===========升级结束   结果：" + (success ? "成功" : "失败") + "  ---  " + success);
        if (!success) {
            upDataCount++;
            if (upDataCount <= 5) {
                //升级失败
                progressNumber.setText(getResources().getString(R.string.string_updata_error_tryup) + "  " + upDataCount);//"准备更新"
                proBar.setIndeterminate(true);
                up_prooss.setVisibility(View.VISIBLE);//默认显示
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//能点击的背景
                btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));

                handler.sendEmptyMessageDelayed(0x01, 700);
            } else {
                isUping = false;//赋值没在升级
                isUping2 = false;
                //升级失败
                progressNumber.setText(getResources().getString(R.string.string_updata_error_isup));//"准备更新"
                proBar.setIndeterminate(true);
                up_prooss.setVisibility(View.VISIBLE);//默认显示
                btnStartUp.setEnabled(true);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
                btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
            }
        } else {
            upDataCount = 0;
            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
            btnStartUp.setEnabled(false);//默认禁止点击
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
            btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本

            if (!WatchUtils.isEmpty(L4M.GetConnectedMAC())) {
                try {
                    if (L4M.Get_Connect_flag() == 2) Dev.RemoteDev_CloseManual();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            B15PContentState.getInstance().bleSeachDevices();
            /**
             * 等待一会，用户同步数据
             */
            isUping = false;//赋值没在升级
            isUping2 = false;

            handler.sendEmptyMessage(0x02);
        }
//        if (bleName.equals("B15P")) {
//
//            if (b15pProgress >= 99) {
//                upDataCount = 0;
//                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//                btnStartUp.setEnabled(false);//默认禁止点击
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
//
//                Dev.RemoteDev_CloseManual();
//                B15PContentState.getInstance().bleSeachDevices();
//                /**
//                 * 等待一会，用户同步数据
//                 */
//                isUping2 = false;
//                handler.sendEmptyMessage(0x02);
//            } else {
//                upDataCount++;
//                b15pProgress = 0;
//                if (upDataCount <= 5) {
//                    //升级失败
//                    progressNumber.setText(getResources().getString(R.string.string_updata_error_tryup) + "  " + upDataCount);//"准备更新"
//                    proBar.setIndeterminate(true);
//                    up_prooss.setVisibility(View.VISIBLE);//默认显示
//                    btnStartUp.setEnabled(false);//默认禁止点击
//                    btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//能点击的背景
//                    btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
//
//                    handler.sendEmptyMessageDelayed(0x01, 700);
//                } else {
//                    isUping = false;//赋值没在升级
//                    //升级失败
//                    progressNumber.setText(getResources().getString(R.string.string_updata_error_isup));//"准备更新"
//                    proBar.setIndeterminate(true);
//                    up_prooss.setVisibility(View.VISIBLE);//默认显示
//                    btnStartUp.setEnabled(true);//默认禁止点击
//                    btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
//                    btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
//                }
//            }
//
//        } else {
//            if (!success) {
//                upDataCount++;
//                if (upDataCount <= 5) {
//                    //升级失败
//                    progressNumber.setText(getResources().getString(R.string.string_updata_error_tryup) + "  " + upDataCount);//"准备更新"
//                    proBar.setIndeterminate(true);
//                    up_prooss.setVisibility(View.VISIBLE);//默认显示
//                    btnStartUp.setEnabled(false);//默认禁止点击
//                    btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//能点击的背景
//                    btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
//
//                    handler.sendEmptyMessageDelayed(0x01, 700);
//                } else {
//                    isUping = false;//赋值没在升级
//                    //升级失败
//                    progressNumber.setText(getResources().getString(R.string.string_updata_error_isup));//"准备更新"
//                    proBar.setIndeterminate(true);
//                    up_prooss.setVisibility(View.VISIBLE);//默认显示
//                    btnStartUp.setEnabled(true);//默认禁止点击
//                    btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
//                    btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
//                }
//            } else {
//                upDataCount = 0;
//                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//                btnStartUp.setEnabled(false);//默认禁止点击
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
//
//
//                Dev.RemoteDev_CloseManual();
//                B15PContentState.getInstance().bleSeachDevices();
//                /**
//                 * 等待一会，用户同步数据
//                 */
//                isUping2 = false;
//                handler.sendEmptyMessage(0x02);
//            }
//
//        }


    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    handler.removeMessages(0x01);
                    startUp();
                    break;
                case 0x02:
                    handler.removeMessages(0x02);
                    up_prooss.setVisibility(View.VISIBLE);//默认隐藏
                    btnStartUp.setEnabled(false);//默认禁止点击
                    proBar.setIndeterminate(true);
                    progressNumber.setText(getResources().getString(R.string.string_ota_dev_ready));//"准备更新"
                    break;
                case 0x03:
                    getLanguage();
                    handler.removeMessages(0x03);
                    ToastUtil.showToast(B30DufActivity.this, getResources().getString(R.string.string_ota_uccess));
                    up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
                    isUping = false;//赋值没在升级
                    break;
            }
            return false;
        }
    });


    void getLanguage() {

        String localelLanguage = Locale.getDefault().getLanguage();

        String param = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "Languages", "EN");

        if (Commont.isDebug)
            Log.e(TAG, "----------localelLanguage=" + localelLanguage + "   " + param);
        if (!WatchUtils.isEmpty(param)) {
            if (!WatchUtils.isEmpty(localelLanguage) && localelLanguage.equals("zh")) {
                if (Commont.isDebug) Log.e(TAG, "======   " + localelLanguage + "    设置中文 ");
                L4Command.LanguageZH();//中文
            } else {
                if (Commont.isDebug) Log.e(TAG, "======   " + localelLanguage + "    设置英文 ");
                L4Command.LanguageEN();//英文
            }
        }
    }

    L4M.BTResultListenr mBTResultListenr = new L4M.BTResultListenr() {
        @Override
        public void On_Result(String TypeInfo, String StrData, Object DataObj) {

            if (TypeInfo.equals(L4M.SetLanguage) && StrData.equals(L4M.OK)) {
                if (StrData.equals("OK")) {
                    String param = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "Languages", "EN");
                    SharedPreferencesUtils.setParam(MyApp.getContext(), "Languages", (!WatchUtils.isEmpty(param) && param.equals("ZH")) ? "ZH" : "EN");
                }
            }
        }
    };


    @Override
    public void startOTA(int len) {
        b15pProgress = 0;
        String mac = (String) SharedPreferencesUtils.readObject(MyApp.getInstance(), Commont.BLEMAC);
        if (!WatchUtils.isEmpty(mac))
            B15PDBCommont.getInstance().clearTodayDatas(mac, WatchUtils.getCurrentDate());
        if (Commont.isDebug) Log.e(TAG, "===========开始升级");
        //开始升级
        Commont.HasOTA = true;//全局配置说明这次启动APP有过OTA升级
        proBar.setIndeterminate(true);
        isUping = true;//赋值正在升级
        isUping2 = true;
        btnStartUp.setEnabled(false);//默认禁止点击
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
    }


    private RequestPressent requestPressent;

    /**
     * getNetWorke()获取后台版本
     */
    public void getNetWorke(String typename, String clientType) {
        try {
            if (Commont.isDebug) Log.e(TAG, "===" + typename + "==" + clientType);
            if (WatchUtils.isEmpty(typename) || WatchUtils.isEmpty(clientType)) return;
            String b25_V = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_SV", "1.0");
            String baseurl = URLs.HTTPs;
            JSONObject jsonObect = new JSONObject();
            jsonObect.put("clientType", clientType);
            jsonObect.put("version", b25_V);
            jsonObect.put("status", "0");
            jsonObect.put("devType", typename);
//            jsonObect.put("clientType", "B25_OTA");
//            jsonObect.put("version", b25_V);
//            jsonObect.put("status", "0");
//            jsonObect.put("devType", "B25");

            if (requestPressent != null) {
                //获取版本
                requestPressent.getRequestJSONObject(1, baseurl + URLs.getVersion, B30DufActivity.this, jsonObect.toString(), 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /***
     * 关于网络请求
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.strinf_up_data_getvis));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (what != 1) return;
        if (object == null) return;
        if (Commont.isDebug) Log.e(TAG, "-----请求数据的结果----obj=" + object.toString());
        UpDataBean upDataBean = new Gson().fromJson(object.toString(), UpDataBean.class);
//        if (isOneUp==0){
        if (upDataBean.getResultCode().equals("010")) {
            //String w30S_v = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
            btnStartUp.setEnabled(false);//默认禁止点击
            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
            btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
        } else {
            String version = upDataBean.getVersion();
            String b25_V = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_SV", "1.0");
            if (WatchUtils.isEmpty(version.trim()) || WatchUtils.isEmpty(b25_V.trim())) return;
            if (Double.valueOf(version.trim()) > Double.valueOf(b25_V.trim())) {
//                    MyCommandManager.isOta = true;  //升级状态下
//                    W30SBleUtils.isOtaConn = true;

                //放在下载完成中
                //upDataState = 0x01;
                upDataState = 0x00;//有新版本可下载
                up_prooss.setVisibility(View.VISIBLE);//默认显示
                btnStartUp.setEnabled(true);
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
                btnStartUp.setText(getResources().getString(R.string.string_w30s_upgrade));
                progressNumber.setText(getResources().getString(R.string.string_w30s_upgradeable) + " ->> V" + version);//"可升级 ->> " + version
                //获取下载链接
                upDataStringUrl = upDataBean.getUrl().trim();
                Log.e(TAG, "---upDataStringUrl--=" + upDataStringUrl);

                if (new File(FileStringPath2).exists()) {
                    try {
                        deleteDirectory(FileStringPath3);
                        Aria.download(this)
                                .load(String.valueOf(upDataStringUrl))     //读取下载地址
                                .setFilePath(FileStringPath)//设置文件保存的完整路径
                                .ignoreFilePathOccupy()
                              .create();   //启动下载
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Aria.download(this)
                            .load(String.valueOf(upDataStringUrl))     //读取下载地址
                            .setFilePath(FileStringPath) //设置文件保存的完整路径
                            .ignoreFilePathOccupy()
                          .create();   //启动下载
                }


                progressNumber.setText(getResources().getString(R.string.string_rade_updata));//"准备更新"
                proBar.setIndeterminate(true);

            } else {
//                    W30SBleUtils.isOtaConn = false;
                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
                btnStartUp.setEnabled(false);//默认禁止点击
                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
            }

        }
//        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
//        W30SBleUtils.isOtaConn = false;
        e.getMessage();
        upDataState = 0x02;
        Toast.makeText(this, getResources().getString(R.string.get_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
        btnStartUp.setEnabled(true);
        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
        up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
        btnStartUp.setText(getResources().getString(R.string.string_w30s_reacquire_version));//"重新获取版本"
    }

    @Override
    public void closeLoadDialog(int what) {

    }

    public void deleteDirectory(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            return;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                removeFile(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void removeFile(File file) {
        //如果是文件直接删除
        if (file.isFile()) {
            file.delete();
            return;
        }
        //如果是目录，递归判断，如果是空目录，直接删除，如果是文件，遍历删除
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                removeFile(f);
            }
            file.delete();
        }
    }


   /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isUping) shhowTips();
            else finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    /**
     * OTA升级中提示禁止返回
     *//*
    void shhowTips() {
        new CommomDialog(B30DufActivity.this, R.style.dialog, getResources().getString(R.string.string_ota_noback), new CommomDialog.OnCloseListener_OneBtn() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).setDoubleBtn(false).setTitle(getResources().getString(R.string.prompt)).show();
    }*/

//
//    private static final String TAG = "B30DufActivity";
//    @BindView(R.id.commentB30BackImg)
//    ImageView commentB30BackImg;
//    @BindView(R.id.commentB30TitleTv)
//    TextView commentB30TitleTv;
//
//    @BindView(R.id.progress_number)
//    TextView progressNumber;
//    @BindView(R.id.b30DufBtn)
//    Button btnStartUp;
//    @BindView(R.id.up_prooss)
//    LinearLayout up_prooss;
//    @BindView(R.id.progressBar_upgrade)
//    ProgressBar proBar;
//
//
//    private String upDataStringUrl = "";//固件升级链接
//    private String bleName = "null";
//    private UpdateManager updateManager = null;
//    private int upDataState = 0x00;
//    private String FileStringPath = Environment.getExternalStorageDirectory().getPath() + "/Android/com.example.bozhilun.android/cache/B25_DEV.bin";
//    private String FileStringPath2 = Environment.getExternalStorageDirectory().getPath() + "/Android/com.example.bozhilun.android/cache/";
//    private boolean isUping = false;//是否正在升级中
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_b30_dfu);
//        ButterKnife.bind(this);
//
//        //注册下载状态监听
//        Aria.download(this).register();
//        Intent intent = getIntent();
//        bleName = intent.getStringExtra("type");
//        if (WatchUtils.isEmpty(bleName)) {
//            bleName = "null";
//        }
//        Log.e(TAG, "  值 " + bleName);
//        initViews();
//    }
//
//    /**
//     * 在这里处理任务执行中的状态，如进度进度条的刷新
//     *
//     * @param task
//     */
//    @Download.onTaskRunning
//    protected void running(DownloadTask task) {
//        Log.e(TAG, "===下载中...");
//    }
//
//    /**
//     * 在这里处理任务完成的状态
//     *
//     * @param task
//     */
//    @Download.onTaskComplete
//    void taskComplete(DownloadTask task) {
//        Log.e(TAG, "===下载完成...");
//
//        progressNumber.setText(getResources().getString(R.string.string_rade_updata));//"准备更新"
//        proBar.setIndeterminate(true);
//        upDataState = 0x01;
//    }
//
//    private void initViews() {
//        requestPressent = new RequestPressent();
//        requestPressent.attach(this);
//        updateManager = new UpdateManager(this);
//        updateManager.setOnOTAUpdateListener(this);
//
//        commentB30BackImg.setVisibility(View.VISIBLE);
//        commentB30TitleTv.setText(getResources().getString(R.string.firmware_upgrade));
//    }
//
//
//    private String typeName = "B25";
//    private String clientType = "B15P";
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!isUping) {
//            if (bleName.equals("B25")) {
//                FileStringPath = Environment.getExternalStorageDirectory().getPath() + "/Android/com.example.bozhilun.android/cache/B25_DEV.bin";
//                typeName = "B25";
//                clientType = "B25_OTA";
//                //获取版本失败，重新获取
//                getNetWorke(typeName, clientType);
//
//            } else if (bleName.equals("B15P")) {
//                FileStringPath = Environment.getExternalStorageDirectory().getPath() + "/Android/com.example.bozhilun.android/cache/B15P_DEV.bin";
//                typeName = "B15P";
//                clientType = "B15P_OTA";
//                //获取版本失败，重新获取
//                getNetWorke(typeName, clientType);
//            } else {
//                //String w30S_v = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
//                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//                btnStartUp.setEnabled(false);//默认禁止点击
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
//            }
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (updateManager != null) updateManager = null;
//    }
//
//
//    @OnClick({R.id.commentB30BackImg, R.id.b30DufBtn})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.commentB30BackImg:
//                finish();
//                break;
//            case R.id.b30DufBtn:
//                if (btnStartUp.isEnabled()) {
//                    startUp();
//                }
//                break;
//        }
//    }
//
//
//    void startUp() {
//        if (upDataState == 0x01) {//可升级的状态
//            //   FileStringPath    bin文件(固件)路径
//            //	 tempAddr   设备mac地址
//            String tempAddr = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);
//            if (!WatchUtils.isEmpty(FileStringPath) && !WatchUtils.isEmpty(tempAddr)) {
//                if (existsFile(FileStringPath)) {
//                    //Log.e(TAG, " bin文件(固件)路径  " + FileStringPath + " \n  设备mac地址  " + tempAddr);
//                    //Log.e(TAG, "----2222----");
//                    if (updateManager == null) {
//                        updateManager = new UpdateManager(B30DufActivity.this);
//                        updateManager.setOnOTAUpdateListener(B30DufActivity.this);
//                    }
//                    updateManager.updateOTA(FileStringPath, tempAddr);
//                } else {
//                    if (!WatchUtils.isEmpty(upDataStringUrl)) {
//                        Toast.makeText(B30DufActivity.this,
//                                getResources().getString(R.string.string_w30s_ota_file), Toast.LENGTH_SHORT).show();//OTA包不存在，正在下载
//                        Aria.download(this)
//                                .load(String.valueOf(upDataStringUrl))     //读取下载地址
//                                .setDownloadPath(FileStringPath) //设置文件保存的完整路径
//                                .start();   //启动下载
//                    } else {
//                        getNetWorke(typeName, clientType);
//                    }
//                }
//
//            }
//        } else if (upDataState == 0x02) {//版本获取失败的状态
//            //获取版本失败，重新获取
//            getNetWorke(typeName, clientType);
//        }
//    }
//
//    /**
//     * 判断文件是否存在
//     */
//    public boolean existsFile(String filePath) {
//        File file = new File(filePath);
//        Log.e(TAG, "====  " + file + "===" + file.exists());
//        if (file.exists()) {
//            return true;
//        }
//        return false;
//    }
//
//
//    /**
//     * OTA 升级相关
//     *
//     * @param len
//     * @param current
//     */
//    @Override
//    public void progressChanged(int len, int current) {
//        isUping = true;
//        int progre_num = current * 100 / len; //升级进度
//        Log.e(TAG, "=========== " + progre_num + "   升级中>>>>>>");
//        progressNumber.setText(getResources().getString(R.string.auto_upgrade));
//        proBar.setIndeterminate(false);
//        proBar.setProgress(progre_num);
//
//        btnStartUp.setEnabled(false);//默认禁止点击
//        btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
//    }
//
//
//    int upDataCount = 0;//自动重新升级的次数重新
//
//    @Override
//    public void finishOTA(boolean success) {
//        isUping = false;//赋值没在升级
//        Log.e(TAG, "===========升级结束   结果：" + (success ? "成功" : "失败"));
//        if (!success) {
//            upDataCount++;
//            if (upDataCount <= 5) {
//                //升级失败
//                progressNumber.setText(getResources().getString(R.string.string_updata_error_tryup) + "  " + upDataCount);//"准备更新"
//                proBar.setIndeterminate(true);
//                up_prooss.setVisibility(View.VISIBLE);//默认显示
//                btnStartUp.setEnabled(false);//默认禁止点击
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
//
//                handler.sendEmptyMessageDelayed(0x01, 700);
//            } else {
//                //升级失败
//                progressNumber.setText(getResources().getString(R.string.string_updata_error_isup));//"准备更新"
//                proBar.setIndeterminate(true);
//                up_prooss.setVisibility(View.VISIBLE);//默认显示
//                btnStartUp.setEnabled(true);//默认禁止点击
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.string_w30s_re_upgrade));
//            }
//        } else {
//            upDataCount = 0;
//            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//            btnStartUp.setEnabled(false);//默认禁止点击
//            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//            btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
//        }
//    }
//
//
//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what) {
//                case 0x01:
//                    startUp();
//                    break;
//            }
//            return false;
//        }
//    });
//
//
//    @Override
//    public void startOTA(int len) {
//        Log.e(TAG, "===========开始升级");
//        //开始升级
//        proBar.setIndeterminate(true);
//        isUping = true;//赋值正在升级
//        btnStartUp.setEnabled(false);//默认禁止点击
//        btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//        btnStartUp.setText(getResources().getString(R.string.upgrade));//"升级中禁止操作"
//    }
//
//
//    private RequestPressent requestPressent;
//
//    /**
//     * getNetWorke()获取后台版本
//     */
//    public void getNetWorke(String typename, String clientType) {
//        try {
//            if (WatchUtils.isEmpty(typename) || WatchUtils.isEmpty(clientType)) return;
//            String b25_V = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_V", "1.0");
//            String baseurl = URLs.HTTPs;
//            JSONObject jsonObect = new JSONObject();
//            jsonObect.put("clientType", clientType);
//            jsonObect.put("version", b25_V);
//            jsonObect.put("status", "0");
//            jsonObect.put("devType", typename);
////            jsonObect.put("clientType", "B25_OTA");
////            jsonObect.put("version", b25_V);
////            jsonObect.put("status", "0");
////            jsonObect.put("devType", "B25");
//
//            if (requestPressent != null) {
//                //获取版本
//                requestPressent.getRequestJSONObject(1, baseurl + URLs.getVersion, B30DufActivity.this, jsonObect.toString(), 0);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /***
//     * 关于网络请求
//     * @param what
//     */
//    @Override
//    public void showLoadDialog(int what) {
//        showLoadingDialog(getResources().getString(R.string.strinf_up_data_getvis));
//    }
//
//    @Override
//    public void successData(int what, Object object, int daystag) {
//        closeLoadingDialog();
//        if (what != 1) return;
//        if (object == null) return;
//        Log.e(TAG, "---------obj=" + object.toString());
//        UpDataBean upDataBean = new Gson().fromJson(object.toString(), UpDataBean.class);
////        if (isOneUp==0){
//        if (upDataBean.getResultCode().equals("010")) {
//            //String w30S_v = (String) SharedPreferenceUtil.get(NewW30sFirmwareUpgrade.this, "W30S_V", "20");
//            up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//            btnStartUp.setEnabled(false);//默认禁止点击
//            btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//            btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
//        } else {
//            String version = upDataBean.getVersion();
//            String b25_V = (String) SharedPreferencesUtils.getParam(B30DufActivity.this, "B25_V", "1.0");
//            if (WatchUtils.isEmpty(version.trim()) || WatchUtils.isEmpty(b25_V.trim())) return;
//            if (Double.valueOf(version.trim()) > Double.valueOf(b25_V.trim())) {
////                    MyCommandManager.isOta = true;  //升级状态下
////                    W30SBleUtils.isOtaConn = true;
//
//                //放在下载完成中
//                //upDataState = 0x01;
//                upDataState = 0x00;//有新版本可下载
//                up_prooss.setVisibility(View.VISIBLE);//默认显示
//                btnStartUp.setEnabled(true);
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_selector));//能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.string_w30s_upgrade));
//                progressNumber.setText(getResources().getString(R.string.string_w30s_upgradeable) + " ->> V" + version);//"可升级 ->> " + version
//                //获取下载链接
//                upDataStringUrl = upDataBean.getUrl().trim();
//                Log.e(TAG, "---upDataStringUrl--=" + upDataStringUrl);
//
//
//                if (new File(FileStringPath2).exists()){
//                    try {
//                        deleteDirectory(FileStringPath2);
//                        Aria.download(this)
//                                .load(String.valueOf(upDataStringUrl))     //读取下载地址
//                                .setDownloadPath(FileStringPath) //设置文件保存的完整路径
//                                .start();   //启动下载
//                    }catch (Exception e){
//                        e.getLocalizedMessage();
//                    }
//                }else {
//                    Aria.download(this)
//                            .load(String.valueOf(upDataStringUrl))     //读取下载地址
//                            .setDownloadPath(FileStringPath) //设置文件保存的完整路径
//                            .start();   //启动下载
//                }
//            } else {
////                    W30SBleUtils.isOtaConn = false;
//                up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//                btnStartUp.setEnabled(false);//默认禁止点击
//                btnStartUp.setBackground(getResources().getDrawable(R.drawable.blue_btn_un_selector));//默认不能点击的背景
//                btnStartUp.setText(getResources().getString(R.string.latest_version));//已经是最新版本
//            }
//
//        }
////        }
//    }
//
//    @Override
//    public void failedData(int what, Throwable e) {
//        closeLoadingDialog();
////        W30SBleUtils.isOtaConn = false;
//        e.getMessage();
//        upDataState = 0x02;
//        Toast.makeText(this, getResources().getString(R.string.get_fail) + e.getMessage(), Toast.LENGTH_SHORT).show();
//        btnStartUp.setEnabled(true);
//        btnStartUp.setBackground(getResources().getDrawable(R.drawable.w30s_blue_background_off));//能点击的背景
//        up_prooss.setVisibility(View.INVISIBLE);//默认隐藏
//        btnStartUp.setText(getResources().getString(R.string.string_w30s_reacquire_version));//"重新获取版本"
//    }
//
//    @Override
//    public void closeLoadDialog(int what) {
//
//    }
//
//
//    /**
//     * 删除目录及目录下的文件
//     *
//     * @param filePath 要删除的目录的文件路径
//     * @return 目录删除成功返回true，否则返回false
//     */
//    private boolean deleteDirectory(String filePath) {
//        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
//        if (!filePath.endsWith(File.separator))
//            filePath = filePath + File.separator;
//        File dirFile = new File(filePath);
//        // 如果dir对应的文件不存在，或者不是一个目录，则退出
//        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
//            Toast.makeText(getApplicationContext(), "删除目录失败：" + filePath + "不存在！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        boolean flag = true;
//        // 删除文件夹中的所有文件包括子目录
//        File[] files = dirFile.listFiles();
//        for (File file : files) {
//            // 删除子文件
//            if (file.isFile()) {
//                flag = deleteSingleFile(file.getAbsolutePath());
//                if (!flag)
//                    break;
//            }
//            // 删除子目录
//            else if (file.isDirectory()) {
//                flag = deleteDirectory(file
//                        .getAbsolutePath());
//                if (!flag)
//                    break;
//            }
//        }
//        if (!flag) {
//            Toast.makeText(getApplicationContext(), "删除目录失败！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        // 删除当前目录
//        if (dirFile.delete()) {
//            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
//            return true;
//        } else {
//            Toast.makeText(getApplicationContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }
//
//
//    /**
//     * 删除单个文件
//     *
//     * @param filePath$Name 要删除的文件的文件名
//     * @return 单个文件删除成功返回true，否则返回false
//     */
//    private boolean deleteSingleFile(String filePath$Name) {
//        File file = new File(filePath$Name);
//        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
//        if (file.exists() && file.isFile()) {
//            if (file.delete()) {
//                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
//                return true;
//            } else {
//                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }

}
