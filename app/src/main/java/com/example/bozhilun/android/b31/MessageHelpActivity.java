package com.example.bozhilun.android.b31;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2019/5/7
 */
public class MessageHelpActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    //打开通知
    @BindView(R.id.helpNotificationTv)
    TextView helpNotificationTv;
    //读取联系人的权限
    @BindView(R.id.helpContPermissionTv)
    TextView helpContPermissionTv;
    //拨打电话权限
    @BindView(R.id.helpPhonePermissionTv)
    TextView helpPhonePermissionTv;
    //读取通话记录权限
    @BindView(R.id.helpCallLogPermissionTv)
    TextView helpCallLogPermissionTv;
    //读取联系人权限
    @BindView(R.id.helpContPermission2Tv)
    TextView helpContPermission2Tv;
    //发送短信权限
    @BindView(R.id.helpSMSPermissionTv)
    TextView helpSMSPermissionTv;


    //联系人
    private boolean contactsFlag = false;
    //电话
    private boolean callPhoneFlag = false;
    //通话记录
    private boolean callLogFlag = false;
    //短信
    private boolean smsFlag = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_help_layout);
        ButterKnife.bind(this);

        initViews();


    }


    @Override
    protected void onResume() {
        super.onResume();
        readPermissions();
    }

    private void readPermissions() {
        //判断是否有联系人的权限
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.READ_CONTACTS)){
            helpContPermissionTv.setText(getResources().getString(R.string.string_enable));
            helpContPermission2Tv.setText(getResources().getString(R.string.string_enable));
            contactsFlag = true;
        }else{
            contactsFlag = false;
            helpContPermissionTv.setText(getResources().getString(R.string.string_disable));
            helpContPermission2Tv.setText(getResources().getString(R.string.string_disable));
        }

        //拨打电话
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.CALL_PHONE)){
            callPhoneFlag = true;
            helpPhonePermissionTv.setText(getResources().getString(R.string.string_enable));
        }else{
            callPhoneFlag = false;
            helpPhonePermissionTv.setText(getResources().getString(R.string.string_disable));
        }

        //通话记录
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.READ_CALL_LOG)){
            callLogFlag = true;
            helpCallLogPermissionTv.setText(getResources().getString(R.string.string_enable));
        }else {
            callLogFlag = false;
            helpCallLogPermissionTv.setText(getResources().getString(R.string.string_disable));
        }

        //短信
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.READ_SMS)){
            smsFlag = true;
            helpSMSPermissionTv.setText(getResources().getString(R.string.string_enable));
        }else{
            smsFlag = false;
            helpSMSPermissionTv.setText(getResources().getString(R.string.string_disable));
        }


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.Messagealert));

    }

    @OnClick({R.id.commentB30BackImg, R.id.helpNotificationTv,
            R.id.helpContPermissionTv, R.id.helpPhonePermissionTv,
            R.id.helpCallLogPermissionTv, R.id.helpContPermission2Tv,
            R.id.helpSMSPermissionTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.helpNotificationTv:   //通知
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intent, 1001);
                break;
            case R.id.helpContPermission2Tv:    //联系人
                if(!contactsFlag){
                    getContactsPermission(Permission.READ_CONTACTS);
                }else{
                    openPermission();
                }

                break;
            case R.id.helpContPermissionTv: //联系人
                //String contPer = helpContPermissionTv.getText().toString().trim();
                if(!contactsFlag){
                    getContactsPermission(Permission.READ_CONTACTS);
                }else{
                    openPermission();
                }
                break;
            case R.id.helpPhonePermissionTv:    //拨打电话
                //String callPhoneStr = helpPhonePermissionTv.getText().toString().trim();
                if(!callPhoneFlag){
                    getContactsPermission(Permission.CALL_PHONE);
                }else{
                    openPermission();
                }
                break;
            case R.id.helpCallLogPermissionTv:  //通话记录
                //String callLogStr = helpCallLogPermissionTv.getText().toString().trim();
                if(!callLogFlag){
                    getContactsPermission(Permission.READ_CALL_LOG);
                }else{
                    openPermission();
                }

                break;
            case R.id.helpSMSPermissionTv:  //短信
                //String smsStr = helpSMSPermissionTv.getText().toString().trim();
                if(!smsFlag){
                    getContactsPermission(Permission.READ_SMS,Permission.RECEIVE_MMS,Permission.SEND_SMS);
                }else{
                    openPermission();
                }
                break;
        }
    }


    //获取相关权限
    private void getContactsPermission(String...permission){
        try {
            AndPermission.with(this)
                    .runtime()
                    .permission(permission)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                            executor.execute();
                        }

                    }).onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    readPermissions();
                }
            }).start();

            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                AndPermission.with(MessageHelpActivity.this)
                        .runtime()
                        .permission(Permission.ANSWER_PHONE_CALLS)
//                            ,Manifest.permission.WRITE_CALL_LOG)
                        .rationale(new Rationale<List<String>>() {
                            @Override
                            public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                                executor.execute();
                            }
                        })
                        .start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //打开权限设置页面
    private void openPermission(){
        AndPermission.with(MessageHelpActivity.this)
                .runtime().setting().start(1001);
    }


}
