package com.example.bozhilun.android.siswatch.utils;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.view.PromptDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

/**
 * Created by Administrator on 2017/9/28.
 */

public class UpdateManager {

    private static final String TAG = "UpdateManager";

    private Context mContext;
    private String url;
    private CommonSubscriber commonSubscriber;
    private boolean isRegedit = false;
    File apkFile;

    //下载提示框
    private ProgressDialog mProgressDialog;

    private AlertDialog.Builder alertDialog;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1001){
                String tmpDir = (String) msg.obj;
                if(tmpDir != null){
                    FileOperUtils.deleteFile(tmpDir);
                }
            }
        }
    };


    public UpdateManager(Context mContext, String url) {
        this.mContext = mContext;
        this.url = url;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("下载中...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
        mProgressDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {


                    }
                });

    }

    public void setUpdate(final String upUrl) {
        final PromptDialog pd = new PromptDialog(mContext);
        pd.show();
        pd.setTitle(mContext.getResources().getString(R.string.prompt));
        pd.setContent(mContext.getResources().getString(R.string.newversion));
        pd.setleftText("NO");
        pd.setrightText("YES");
        pd.setListener(new PromptDialog.OnPromptDialogListener() {
            @Override
            public void leftClick(int code) {
                pd.dismiss();
            }

            @Override
            public void rightClick(int code) {
                pd.dismiss();
                Intent intent = new Intent(mContext.getApplicationContext(), UpdateWebViewActivity.class);
                intent.putExtra("updateUrl", upUrl);
                mContext.startActivity(intent);
            }
        });
    }


    public void setUpdate2(final String downurl) {
        if(WatchUtils.isEmpty(downurl))
            return;
        alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.prompt))
                .setMessage(mContext.getResources().getString(R.string.newversion))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        chooseDownloadType(downurl);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    //选择更新模式，本地或者应用宝
    private void chooseDownloadType(final String dUrl) {
        String[] itmeStr = new String[]{"本地下载","应用宝下载"};
        alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("请选择更新方式")
                .setItems(itmeStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which){
                            case 0:
                                showDownloadDialog(dUrl);
                                break;
                            case 1:
                                downloadFromYyb();
                                break;
                        }
                    }
                });
        alertDialog.show();
    }

    //从应用宝下载
    private void downloadFromYyb() {
        //判断是否安装应用宝
        if(WatchUtils.isClientInstalled(mContext,"com.tencent.android.qqdownloader")){
            WatchUtils.gotoAppStory(mContext,mContext.getPackageName());
        }else{
            mContext.startActivity(new Intent(mContext,UpdateWebViewActivity.class));
        }

    }

    //开始下载
    private void downLoadTheApkData(String url) {
        String apkUrl = url;
        Log.e(TAG, "----apkUrl=" + apkUrl);
        String dir = mContext.getExternalFilesDir("RaceFitPro").toString();
        Log.e(TAG, "----dir-=" + dir);
        if(dir != null){
            Message message = new Message();
            message.what = 1001;
            message.obj = dir;
            handler.sendMessage(message);
        }

        File folder = Environment.getExternalStoragePublicDirectory(dir);
        if (folder.exists() && folder.isDirectory()) {
            //删除
            FileOperUtils.deleteFile(folder);
        } else {
            folder.mkdirs();
        }
//		String filename = apkUrl.substring(apkUrl.lastIndexOf("/"),
//				apkUrl.length());
        String filename =  System.currentTimeMillis() / 100 + ".apk";
        //String filename = "downLoadApk"+".apk";
        String destinationFilePath = dir +  filename;

        Log.e(TAG, "----destinationFilePath-=" + destinationFilePath);
        apkFile = new File(destinationFilePath);
        mProgressDialog.show();
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra("url", apkUrl);
        intent.putExtra("dest", destinationFilePath);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        mContext.startService(intent);
    }

    //检查更新
    public void checkForUpdate(final boolean isTrue) {
        if (url != null) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("version", WatchUtils.getVersionCode(mContext));
                jsonObject.put("appName","com.example.bozhilun.android");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String result) {
                    Log.e("update", "---result---" + result);
                    if (!WatchUtils.isEmpty(result)) {
                        try {
                            JSONObject jsono = new JSONObject(result);
                            if(!jsono.has("code"))
                                return;
                            if(jsono.getInt("code") == 200){
                                JSONObject jsonObject1 = jsono.getJSONObject("data");
                                String verStr = jsonObject1.getString("version");
                                if(!WatchUtils.isEmpty(verStr) && Integer.valueOf(verStr.trim())>WatchUtils.getVersionCode(mContext)){
                                    Log.e("update", "-----返回地址=" + jsonObject1.getString("url"));
                                    setUpdate2(jsonObject1.getString("url"));
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }, mContext);
            OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObject.toString());

        }
    }

    //卸载广播
    public void destoryUpdateBroad() {
        try {
            if (mReceiver != null && isRegedit) {
                mContext.unregisterReceiver(mReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //下载
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                mProgressDialog.setProgress(progress);
                mProgressDialog.setCancelable(false);
                if (progress == 100) {
                    mProgressDialog.dismiss();
                    installDownLoadAPK(apkFile);
                }
            }
        }
    }

    //安装
    private void installDownLoadAPK(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider_racefitpro", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }


    private DownloadManager mDownloadManager;
    private long downloadId;

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog(String dowUrl) {
        try {
            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RaceFitPro.apk");
            if (!WatchUtils.isEmpty(apkFile.getPath())) {
                rmoveFile(apkFile.getPath());
            }
            //应用宝下载链接
            //String downPath = "http://imtt.dd.qq.com/16891/F2348496960C064EC9B355F194BEE1D8.apk?fsname=com.example.bozhilun.android_3.6.0.6_44.apk";//下载路径 根据服务器返回的apk存放路径
            //String downPath = dowUrl;//下载路径 根据服务器返回的apk存放路径
            mContext.registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            isRegedit = true;
            //使用系统下载类
            mDownloadManager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(dowUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedOverRoaming(false);

            //创建目录下载
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "RaceFitPro.apk");
            // 把id保存好，在接收者里面要用
            downloadId = mDownloadManager.enqueue(request);
            //设置允许使用的网络类型，这里是移动网络和wifi都可以
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            //机型适配
            request.setMimeType("application/vnd.android.package-archive");
            //通知栏显示
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("RaceFitPro");
            request.setDescription("Downloading ...");
            request.setVisibleInDownloadsUi(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            switch (intent.getAction()) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    checkStatus();
                    break;
            }

        }
    };


    /**
     * 检查下载状态
     */
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        if(cursor == null)
            return;
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    installAPK();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "download failure", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        cursor.close();
    }

    /**
     * 7.0兼容
     */
    private void installAPK() {
        File apkFile =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "RaceFitPro.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider_racefitpro", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }


    public File getPathFile(String path) {
        String apkName = path.substring(path.lastIndexOf("/"));
        File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), apkName);
        return outputFile;
    }

    public void rmoveFile(String path) {
        File file = getPathFile(path);
         file.delete();
    }

}
