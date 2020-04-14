package com.example.bozhilun.android.w30s.ota;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.bean.UpDataBean;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.example.bozhilun.android.R;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * OTA升级
 * Created by Administrator on 2018/5/25.
 */

public class W30OTAActivity extends WatchBaseActivity implements RequestView{

    private static final String TAG = "W30OTAActivity";


    @BindView(R.id.progressBar_upgrade)
    ProgressBar progressBarUpgrade;
    @BindView(R.id.btn_start_up)
    Button btnStartUp;

    RequestPressent requestPressent;
    int localVersion = 1;
    private String downloadUrl = null;
    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;
    private String downPath ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w30s_frrinware_upgrade);
        ButterKnife.bind(this);

        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        localVersion = Integer.valueOf((String)SharedPreferencesUtils.getParam(W30OTAActivity.this,"W30S_V","ab"));
        downPath = Environment.getExternalStorageDirectory()+"RaceDown/";

        getNetWorke();
    }

    /**
     * getNetWorke()获取后台版本
     */
    public void getNetWorke(){
        try {
            String w30S_v = (String) SharedPreferencesUtils.getParam(W30OTAActivity.this, "W30S_V", "20");
            if (WatchUtils.isEmpty(w30S_v)) return;
            String baseurl = URLs.HTTPs;
            JSONObject jsonObect = new JSONObject();
            jsonObect.put("clientType", "android");
            jsonObect.put("version", localVersion);
            if (requestPressent != null) {
                //获取版本
                requestPressent.getRequestJSONObject(1, baseurl + URLs.getVersion, W30OTAActivity.this, jsonObect.toString(), 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_start_up)
    public void onViewClicked() {
        downloadData(); //下载升级包
    }

    private void downloadData() {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。

            mDownloadRequest = new DownloadRequest(downloadUrl, RequestMethod.GET,
                    downPath,true, true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getInstance().download(0, mDownloadRequest, downloadListener);

            // 添加到队列，在没响应的时候让按钮不可用。
            //mBtnStart.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestPressent.detach();
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {

        Log.e(TAG,"---------succ-="+object);
        if(what == 1){
            UpDataBean upDataBean = new Gson().fromJson(object.toString(), UpDataBean.class);
            if(upDataBean.getResultCode().equals("001")){
                int version = Integer.valueOf(upDataBean.getVersion().trim());
                String downUrl = upDataBean.getUrl();
                if(version > localVersion){     //有新版本
                    downloadUrl = downUrl;
                }

            }


        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {


    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {

        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            Logger.e(exception);


        }
        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            updateProgress(progress, speed);

        }

        @Override
        public void onFinish(int what, String filePath) {
            Logger.d("Download finish, file path: " + filePath);

        }

        @Override
        public void onCancel(int what) {

        }

        private void updateProgress(int progress, long speed) {


        }
    };
}
