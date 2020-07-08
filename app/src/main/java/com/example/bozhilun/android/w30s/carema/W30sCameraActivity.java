package com.example.bozhilun.android.w30s.carema;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.activity.W30sAlbumActivity;
import com.example.bozhilun.android.w30s.ble.W37BleOperateManager;
import com.example.bozhilun.android.w30s.ble.W37DataAnalysis;
import com.example.bozhilun.android.w30s.utils.CameraUtils;
import com.example.bozhilun.android.xwatch.ble.XWatchBleAnalysis;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.tjdL4.tjdmain.L4M;
import com.tjdL4.tjdmain.contr.L4Command;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICameraDataListener;
import com.veepoo.protocol.model.enums.ECameraStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/23 08:58
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30sCameraActivity extends WatchBaseActivity implements View.OnClickListener {

    private static final String TAG = "W30sCameraActivity";
    //开始拍照的指令
    public static final String W37_CAMERA_TAKE_PHOTO = "com.example.bozhilun.android.w30s.ble.take_photo";


    CameraPreview mCameraPreview;
    CameraLine mCameraLine;
    RelativeLayout id_rl_cp_view;
    //【重力感应处理】 app内锁定横屏 或用户锁定横屏时候获得方向
    private ScreenSwitchUtils mScreenSwitchInstance;
    Camera mCamera;

    public static final int ZOOM_FACTOR = 5;//缩放因子
    private int zoomValue = 0;

    public static final int FLAG_CHOOCE_PICTURE = 2001;//选择图片
    private final int FLAG_AUTO_FOCUS = 1001;//自动对焦
    private final int TAKE_PHOTO_FINISH = 1002;//拍完
    private final int FOCUS_DURATION = 3000;//延迟聚焦

    //    private final int FOCUS_YAOYIYAO = 111000;
    private boolean safeToTakePicture = true;
    private boolean isPortrait = true;
    int cameraCurrentlyLocked;
    private int defaultCameraId = 1;
    //1为后置摄像头
    int cameraPosition = 1;
    int tmpCamera = 0;
    private int orientationState = ScreenSwitchUtils.ORIENTATION_HEAD_IS_UP;

    private int mScreenHeight, mScreenWidth;
    private int viewHeight;
    boolean isShow = false;
    boolean isShowCamera = false;

    //是否是手动切换摄像头
    boolean isChange = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Log.e(TAG, "-----hand=" + msg.what);
            switch (msg.what) {
                case FLAG_AUTO_FOCUS:   //闪光灯
                    try {
                        if (mCamera != null && safeToTakePicture && !TextUtils.isEmpty(mCamera.getParameters().getFlashMode())) {
                            mCamera.startPreview();
                            mCamera.autoFocus(null);
                            //Toast.makeText(CameraActivity.this, "auto focus", Toast.LENGTH_SHORT).show();
                        }
                        handler.sendEmptyMessageDelayed(FLAG_AUTO_FOCUS, FOCUS_DURATION);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case TAKE_PHOTO_FINISH:     //拍完照片
                    if (msg.obj == null) {
                        return;
                    }
                    String filePath = msg.obj.toString();
                    Intent intent = new Intent(W30sCameraActivity.this, PreviewActivity.class);
                    Bitmap bitmapByUrl = getBitmapByUrl(filePath);
                    Log.d("======图1==", filePath + "--" + bitmapByUrl);
                    SharedPreferencesUtils.setParam(W30sCameraActivity.this, "camera", filePath);
                    xiangceIM.setImageBitmap(bitmapByUrl);
                    intent.putExtra("filePath", filePath);
                    startActivity(intent);
                    break;
                case 1111:
                    byte[] b = (byte[]) msg.obj;
                    handleAndSaveBitmap(b);
                    break;

            }


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "-----onCreate---");
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //设置当前窗体为全屏显示-----------------定义全屏参数
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//【旋转问题】首先强制竖屏，手机横过来时候 控件不变
        setContentView(R.layout.w30s_camera_activity);


        initViews();

        IntentFilter intentFilter = new IntentFilter(W37BleOperateManager.X_WATCH_TAKE_PHOTO);
        intentFilter.addAction(W37_CAMERA_TAKE_PHOTO);
        registerReceiver(myBroadcastReceiver,intentFilter);

        verticalDevice();


    }

    private void initViews() {
        //【重力感应处理】 app内锁定横屏 或用户锁定横屏时候获得方向
        mScreenSwitchInstance = ScreenSwitchUtils.init(getApplicationContext());

        findViewById(R.id.image_backes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!CameraUtils.checkCameraHardware(this)) {
//            Toast.makeText(W30sCameraActivity.this, "设备没有摄像头", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        init();
        DoubleClick();

        // centerWindowView = findViewById(R.id.center_window_view);
        Log.d("CameraSurfaceView", "CameraSurfaceView onCreate currentThread : " + Thread.currentThread());
        // 得到屏幕的大小
        WindowManager wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wManager.getDefaultDisplay();
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();
        viewHeight = mScreenWidth / 2;
        //startCamera();



    }

    private void verticalDevice() {

        Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
        if(MyCommandManager.DEVICENAME == null)return;
        if(WatchUtils.isVPBleDevice(MyCommandManager.DEVICENAME)){
            MyApp.getInstance().getVpOperateManager().startCamera(iBleWriteResponse, new ICameraDataListener() {
                @Override
                public void OnCameraDataChange(ECameraStatus coStatus) {
                    if (coStatus == ECameraStatus.TAKEPHOTO_CAN) {  //拍照
                        if (isShow) {
                            if (isShowCamera) {
                                takePhonePic();
                            }
                        }
                    }
                }
            });
            return;
        }
        if(set.contains(MyCommandManager.DEVICENAME)){
            //监听
            L4Command.CameraOn(new L4M.BTResultListenr() {
                @Override
                public void On_Result(String TypeInfo, String StrData, Object DataObj) {
                    final String tTypeInfo = TypeInfo;
                    final String TempStr = StrData;
                    final Object TempObj = DataObj;
                    Log.e(TAG, "inTempStr:" + TempStr);

                    if (TypeInfo.equals(L4M.ERROR) && StrData.equals(L4M.TIMEOUT)) {
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tTypeInfo.equals(L4M.RemoteCapOn) && TempStr.equals(L4M.OK)) {
                                Log.e(TAG, "   RemoteCapOn");
                            }
                            if (tTypeInfo.equals(L4M.RemoteCapOFF) && TempStr.equals(L4M.OK)) {
                                Log.e(TAG, "   RemoteCapOFF");
                            }
                            if (tTypeInfo.equals(L4M.RemoteCapTakeCap) && TempStr.equals(L4M.OK)) {
                                Log.e(TAG, "   RemoteCapTakeCap");
                                L4Command.CameraCap_Respone();//响应
                                takePhonePic();
                            }
                        }
                    });
                }
            });
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "-----onStart---");
        //【重力感应处理】
        try {
            mScreenSwitchInstance.start(this);
            isShowCamera = true;
            startCamera();


            String cameraStute = (String) SharedPreferencesUtils.getParam(W30sCameraActivity.this, "cameraStute", "");
            if (!WatchUtils.isEmpty(cameraStute)) {
                p = mCamera.getParameters();
                switch (cameraStute) {
                    case Camera.Parameters.FLASH_MODE_ON:// 拍照时打开闪光灯
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        mCamera.setParameters(p);
                        //如果闪状态为 拍照时打开-----改为-----》》》系统决定
                        shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_auto));
                        break;
                    case Camera.Parameters.FLASH_MODE_OFF:// 拍照时始终关闭闪光灯
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        mCamera.setParameters(p);
                        //如果闪关灯状态为 始终关闭-----改为-----》》》拍照时打开
                        shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_on));
                        break;
                    case Camera.Parameters.FLASH_MODE_AUTO:// 系统决定是否开启闪光灯（推荐使用）
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);
                        //如果闪状态为 系统决定-----改为-----》》》始终关闭
                        shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_off));
                        break;
                }
            }

            //注册连接状态的广播
            //registerReceiver(myBroadcastReceiver, makeGattUpdateIntentFilter());

            //注册舟海手环拍照的广播
            MyApp.getInstance().getW37BleOperateManager().registerW37CameraListener();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Camera.Parameters p;

    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"---------camera="+action);
            if(action == null)
                return;
            if (action.equals(W37DataAnalysis.W37_CAMERA_TAKE_PHOTO)) { //舟海系列拍照
                if (isShow) {
                    if (isShowCamera) {
                        takePhonePic();
                    }
//                        handler.sendEmptyMessageDelayed(FOCUS_YAOYIYAO, 6000);
                }
            }


            if(action.equals(W37BleOperateManager.X_WATCH_TAKE_PHOTO)){ //XWatch拍照
                if (isShow) {
                    if (isShowCamera) {
                        takePhonePic();
                    }
                }
            }

        }
    };

    /**
     * 广播过滤
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W30SBLEServices.ACTION_CAMERA_AVAILABLE_DEVICE);//拍照

        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "-----onResume---");
        isShow = true;
//        startCamera();
        //changeCameraTwo(isChange);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "-----onPause---");
        stopFocus();
        isShow = false;
        releaseCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "-----onStop---");
        //【重力感应处理】
        isShow = false;

        mScreenSwitchInstance.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "-----onDestroy---");
        try {
            Log.e(TAG,"---MyCommandManager.DEVICENAME="+MyCommandManager.DEVICENAME);
            //退出拍照模式
            if (MyCommandManager.DEVICENAME != null) {
                Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
                if (WatchUtils.isVPBleDevice(MyCommandManager.DEVICENAME)) {
                    MyApp.getInstance().getVpOperateManager().stopCamera(iBleWriteResponse, new ICameraDataListener() {
                        @Override
                        public void OnCameraDataChange(ECameraStatus coStatus) {

                        }
                    });
                }

                //腾进达方案，用户信息要同步
                else if (!WatchUtils.isEmpty(MyCommandManager.DEVICENAME) || set.contains(MyCommandManager.DEVICENAME)) {
                    //拍照结束时销毁
                    L4Command.CameraOff();
                    L4M.SetResultListener(null);
                }

                else if(MyCommandManager.DEVICENAME.equals("SWatch")){
                    XWatchBleAnalysis.getW37DataAnalysis().openOrCloseCamera(0);
                }
            }


            isShow = false;
            releaseCamera();
            unregisterReceiver(myBroadcastReceiver);
            MyApp.getInstance().getW37BleOperateManager().unRegisterW37CameraListener();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "------CameraSurfaceView imgPath : " + requestCode + "===" + resultCode);
        if (requestCode == FLAG_CHOOCE_PICTURE && resultCode == RESULT_OK) {
            isShow = true;
            startCamera();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "---onRestart---");
    }

    /**
     * 根据图片路径获取本地图片的Bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapByUrl(String url) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(url);
            bitmap = BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            bitmap = null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fis = null;
            }
        }

        return bitmap;
    }


    ImageView xiangtouzhuanIM, shanguangIM, paizhaoIM, xiangceIM;

    private void init() {
        id_rl_cp_view = (RelativeLayout) findViewById(R.id.id_rl_cp_view);
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mCameraLine = (CameraLine) findViewById(R.id.ccmer_line);
        xiangtouzhuanIM = (ImageView) findViewById(R.id.xiangtouzhuan);
        shanguangIM = (ImageView) findViewById(R.id.shanguang);
        paizhaoIM = (ImageView) findViewById(R.id.paizhao);
        xiangceIM = (ImageView) findViewById(R.id.xiangce);
        xiangtouzhuanIM.setOnClickListener(this);
        shanguangIM.setOnClickListener(this);
        paizhaoIM.setOnClickListener(this);
        xiangceIM.setOnClickListener(this);
    }

    private void startCamera() {
        // Open the default i.e. the first rear facing camera.
        try {
            if (mCamera == null) {
                mCamera = Camera.open(tmpCamera);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(this, "启动照相机失败，请检查设备并打开权限", Toast.LENGTH_SHORT).show();
        }
        cameraCurrentlyLocked = defaultCameraId;
        mCameraPreview.setCamera(mCamera);

        startFocus();
    }

    /**
     * 开启自动对焦
     */
    private void startFocus() {
        stopFocus();
        handler.sendEmptyMessageDelayed(FLAG_AUTO_FOCUS, FOCUS_DURATION);
    }

    /**
     * 关闭自动对焦
     */
    private void stopFocus() {
        handler.removeMessages(FLAG_AUTO_FOCUS);
    }

    /**
     * 释放mCamera
     */
    private void releaseCamera() {
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mCameraPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 双击放大缩小
     */
    private void DoubleClick() {
        //双击效果
        DoubleClickConfig.registerDoubleClickListener(id_rl_cp_view, new DoubleClickConfig.OnDoubleClickListener() {

            @Override
            public void OnSingleClick(View v) {
                // TODO Auto-generated method stub
                zoomDown();//单机缩小
            }

            @Override
            public void OnDoubleClick(View v) {
                // TODO Auto-generated method stub
                zoomUp();//双击放大
            }
        });
    }

    /**
     * 缩小
     */
    public void zoomDown() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters p = mCamera.getParameters();
        if (!p.isZoomSupported()) return;

        if (zoomValue > 0) {
            zoomValue--;
        } else {
            zoomValue = 0;
            // Toast.makeText(getApplicationContext(), "已缩小到最小级别", Toast.LENGTH_SHORT).show();
            return;
        }
        int value = (int) (1F * zoomValue / ZOOM_FACTOR * p.getMaxZoom());
        p.setZoom(value);
        mCamera.setParameters(p);
    }

    /**
     * 放大
     */
    public void zoomUp() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters p = mCamera.getParameters();
        if (!p.isZoomSupported()) return;

        if (zoomValue < ZOOM_FACTOR) {
            zoomValue++;
        } else {
            zoomValue = ZOOM_FACTOR;
//            Toast.makeText(getApplicationContext(), "已放大到最大级别", Toast.LENGTH_SHORT).show();
            return;
        }
        int value = (int) (1F * zoomValue / ZOOM_FACTOR * p.getMaxZoom());
        p.setZoom(value);
        mCamera.setParameters(p);
    }

    /**
     * 拍照
     *
     * @param shutter
     * @param raw
     * @param jpeg
     */
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
                            Camera.PictureCallback jpeg) {
        if (mCamera != null) {
            if (safeToTakePicture) {
                mCamera.takePicture(shutter, raw, jpeg);
                safeToTakePicture = false;
            }
        }
    }


    /**
     * 开关闪光灯
     * <p/>
     * 持续的亮灯FLASH_MODE_TORCH
     * 闪一下FLASH_MODE_ON
     * 关闭模式FLASH_MODE_OFF
     * 自动感应是否要用闪光灯FLASH_MODE_AUTO
     */
    public void toggleFlash() {
        if (mCamera == null) {
            return;
        }

        /**
         * Camera.Parameters.FLASH_MODE_ON 拍照时打开闪光灯
         * Camera.Parameters.FLASH_MODE_OFF 拍照时始终关闭闪光灯
         * Camera.Parameters.FLASH_MODE_AUTO 系统决定是否开启闪光灯（推荐使用）
         * Camera.Parameters.FLASH_MODE_TORCH 手电筒模式 一直开着闪光灯。
         */
        if (p == null) {
            p = mCamera.getParameters();
        }

        String cameraStute = (String) SharedPreferencesUtils.getParam(W30sCameraActivity.this, "cameraStute", "");
        if (!WatchUtils.isEmpty(cameraStute)) {
            p = mCamera.getParameters();
            switch (cameraStute) {
                case Camera.Parameters.FLASH_MODE_ON:// 拍照时打开闪光灯
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    mCamera.setParameters(p);
                    //如果闪状态为 拍照时打开-----改为-----》》》系统决定
                    shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_auto));
                    break;
                case Camera.Parameters.FLASH_MODE_OFF:// 拍照时始终关闭闪光灯
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    mCamera.setParameters(p);
                    //如果闪关灯状态为 始终关闭-----改为-----》》》拍照时打开
                    shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_on));
                    break;
                case Camera.Parameters.FLASH_MODE_AUTO:// 系统决定是否开启闪光灯（推荐使用）
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(p);
                    //如果闪状态为 系统决定-----改为-----》》》始终关闭
                    shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_off));
                    break;
            }
        }

//        if (Camera.Parameters.FLASH_MODE_OFF.equals(p.getFlashMode())) {
//            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//            mCamera.setParameters(p);
//            shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_on));
//        } else if (Camera.Parameters.FLASH_MODE_ON.equals(p.getFlashMode())) {
//            p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//            mCamera.setParameters(p);
//            shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_auto));
//        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(p.getFlashMode())) {
//            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//持续的亮灯
//            mCamera.setParameters(p);
//            shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_light));
//        } else if (Camera.Parameters.FLASH_MODE_TORCH.equals(p.getFlashMode())) {
//            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//            mCamera.setParameters(p);
//            shanguangIM.setImageDrawable(getResources().getDrawable(R.mipmap.camera_flash_off));
//        } else {
//            Toast.makeText(this, "Flash mode setting is not supported.", Toast.LENGTH_SHORT).show();
//        }

    }

    /**
     * 切换前后相投
     */
    void changeCameraTwo(boolean ischange) {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        if (mCamera == null) return;
        Log.e(TAG, "---cameraPosition=" + cameraPosition);
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
//                Toast.makeText(this, "1现在是后置，变更为前置", Toast.LENGTH_SHORT).show();
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置

                    mCamera.stopPreview();//停掉原来摄像头的预览
                   /*
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头*/
                    releaseCamera();
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    tmpCamera = i;
                    Log.e(TAG, "---22--tmp-=" + tmpCamera + "--i=" + i);
                    try {
                        mCamera.setPreviewDisplay(mCameraPreview.getLouisSurfaceHolder());//通过surfaceview显示取景画面
                        mCamera.setDisplayOrientation(90); //
                        mCameraPreview.setCamera(mCamera);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
//                Toast.makeText(this, "2现在是前置， 变更为后置", Toast.LENGTH_SHORT).show();
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置

                    mCamera.stopPreview();//停掉原来摄像头的预览
                    /*
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头*/
                    releaseCamera();
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    tmpCamera = i;
                    Log.e(TAG, "---22--tmp-=" + tmpCamera + "--i=" + i);
                    try {
                        mCamera.setPreviewDisplay(mCameraPreview.getLouisSurfaceHolder());//通过surfaceview显示取景画面
                        mCamera.setDisplayOrientation(90); //
                        mCameraPreview.setCamera(mCamera);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xiangtouzhuan:
                isChange = true;
                changeCameraTwo(isChange);
                break;
            case R.id.shanguang:
                toggleFlash();
                break;
            case R.id.paizhao:
                if (isShowCamera) {
                    takePhonePic();
                }
                break;
            case R.id.xiangce:
                choosePicture();
                break;
        }
    }

    public void takePhonePic() {
        try {
            stopFocus();
            takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (data == null || data.length <= 0) {
                        safeToTakePicture = true;
                        return;
                    }
                    Log.d("CameraSurfaceView", "CameraSurfaceView onPictureTaken data.length : " + data.length);
                    isPortrait = mScreenSwitchInstance.isPortrait();
                    orientationState = mScreenSwitchInstance.getOrientationState();
                    Log.e("==========", "louis==xx==isPortrait：" + isPortrait);
                    Log.e("=========", "louis==xx==orientationState：" + orientationState);
                    // 保存图片
                    final byte[] b = data;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.obj = b;
                            message.what = 1111;
                            handler.sendMessage(message);
                            isShowCamera = false;
                            safeToTakePicture = true;
                        }
                    }).start();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 查看拍摄的图片
     */
    private void choosePicture() {
        startActivity(W30sAlbumActivity.class);
    }

    /**
     * 处理拍照图片并保存
     *
     * @param data
     */
    private Bitmap rightBitmap;

    private synchronized void handleAndSaveBitmap(byte[] data) {
        // 保存图片
        //### Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
        //Bitmap b = CameraUtils.Bytes2Bitmap(data);

        Bitmap b = CameraUtils.byteToBitmap(data);
        Log.e(TAG, "----handleAndSaveBitmap--cameraPosition=" + cameraPosition + "--=" + orientationState);
        if (cameraPosition == 1) {
            //后置摄像头
            //rightBitmap = Utils.rotate(b, 0);
            rightBitmap = CameraUtils.rotate(b, 90);//摆正位置
            //rightBitmap = Utils.rotate(b, 180);
            // rightBitmap = Utils.rotate(b, 270);
            //根据重力感应 更正旋转
            switch (orientationState) {
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_UP:
                    break;
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_DOWN:
                    rightBitmap = CameraUtils.rotate(rightBitmap, 180);
                    break;
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_LEFT:
                    rightBitmap = CameraUtils.rotate(rightBitmap, 270);
                    break;
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_RIGHT:
                    rightBitmap = CameraUtils.rotate(rightBitmap, 90);
                    break;
            }
        } else {
            //前置摄像头
            rightBitmap = CameraUtils.rotate(b, 270);//摆正位置
            //根据重力感应 更正旋转
            switch (orientationState) {
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_UP:
//                    if(isChange){
//                        rightBitmap = CameraUtils.rotate(rightBitmap, 180);
//                    }
                    break;
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_DOWN:
                    rightBitmap = CameraUtils.rotate(rightBitmap, 180);
                    break;
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_LEFT:
                    rightBitmap = CameraUtils.rotate(rightBitmap, 90);
                    break;
                case ScreenSwitchUtils.ORIENTATION_HEAD_IS_RIGHT:
                    rightBitmap = CameraUtils.rotate(rightBitmap, 270);
                    break;
            }
        }
        if (rightBitmap != null) {
            String filePath = saveImageToGallery(W30sCameraActivity.this, rightBitmap);
//        String filePath = saveImageFile(W30sCameraActivity.this, rightBitmap);
            Message message = handler.obtainMessage();
            message.what = TAKE_PHOTO_FINISH;
            message.obj = filePath;
            message.sendToTarget();
        }

    }


//
//    private String saveImageFile(Context context, Bitmap bmp) {
//        String filePath = null;
//        File file = Utils.getDiskCacheDir(this, "B30");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
////        File f = new File(file, "picture.jpg");
//        String fileName = System.currentTimeMillis() + ".jpg";
//        File f = new File(file, fileName);
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//            filePath = f.getAbsolutePath();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (bmp != null && !bmp.isRecycled()) {
//                bmp.recycle();
//                bmp = null;
//            }
//            // 其次把文件插入到系统图库
//            try {
//                MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                        file.getAbsolutePath(), fileName, null);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            // 最后通知图库更新
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + f)));
//        }
//        return filePath;
//    }


    public static String saveImageToGallery(Context context, Bitmap bmp) {
        String filePath = null;
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "/Pictures");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        Log.d("======图==", file + "");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.flush();
            fos.close();
            filePath = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(String.valueOf(appDir))));
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "w30s/pic")));
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }
        return filePath;
    }

    /**
     * 获取从相册中选择的图片的据对路径
     *
     * @param uri
     * @return
     */
    private String getUrl(Uri uri) {
        if (uri == null) {
            return null;
        }

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        return TextUtils.isEmpty(img_path) ? null : img_path;
    }

}
