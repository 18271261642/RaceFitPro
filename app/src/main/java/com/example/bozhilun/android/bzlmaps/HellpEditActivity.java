package com.example.bozhilun.android.bzlmaps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bzlmaps.sos.GPSGaoDeUtils;
import com.example.bozhilun.android.bzlmaps.sos.GPSGoogleUtils;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.VerifyUtil;
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HellpEditActivity
        extends WatchBaseActivity implements View.OnLongClickListener {
    //implements Rationale<List<String>>, View.OnLongClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_content)
    TextView textContent;
    @BindView(R.id.text_user_phone_one)
    TextView textOne;
    @BindView(R.id.text_user_phone_two)
    TextView textTwo;
    @BindView(R.id.text_user_phone_three)
    TextView textThee;

    @BindView(R.id.text_user_name_one)
    TextView textNameOne;
    @BindView(R.id.text_user_name_two)
    TextView textNameTwo;
    @BindView(R.id.text_user_name_three)
    TextView textNameThee;
    @BindView(R.id.car_person_one)
    CardView carPersonOne;
    @BindView(R.id.car_person_two)
    CardView carPersonTwo;
    @BindView(R.id.car_person_three)
    CardView carPersonThree;
    //联系人
    @BindView(R.id.sosContPermissionTv)
    TextView sosContPermissionTv;
    //电话
    @BindView(R.id.sosPhonePermissionTv)
    TextView sosPhonePermissionTv;
    //通讯录
    @BindView(R.id.sosCallLogPermissionTv)
    TextView sosCallLogPermissionTv;
    //联系人
    @BindView(R.id.sosContPermission2Tv)
    TextView sosContPermission2Tv;
    //短信
    @BindView(R.id.sosSMSPermissionTv)
    TextView sosSMSPermissionTv;


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
        setContentView(R.layout.hellp_edit_activity);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        tvTitle.setText("SOS");
        toolbar.setTitle(" ");
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });//右边返回按钮点击事件
        statuteChange();

        carPersonOne.setOnLongClickListener(this);
        carPersonTwo.setOnLongClickListener(this);
        carPersonThree.setOnLongClickListener(this);
        textContent.setOnLongClickListener(this);
    }


    void statuteChange() {
        String stringpersonContent = (String) SharedPreferencesUtils.getParam(this, "personContent", "");
        String stringpersonOne = (String) SharedPreferencesUtils.getParam(this, "personOne", "");
        String stringpersonTwo = (String) SharedPreferencesUtils.getParam(this, "personTwo", "");
        String stringpersonThree = (String) SharedPreferencesUtils.getParam(this, "personThree", "");


        String nameOne = (String) SharedPreferencesUtils.getParam(HellpEditActivity.this, "NameOne", "");
        String nameTwo = (String) SharedPreferencesUtils.getParam(HellpEditActivity.this, "NameTwo", "");
        String nameThree = (String) SharedPreferencesUtils.getParam(HellpEditActivity.this, "NameThree", "");
        if (!WatchUtils.isEmpty(stringpersonContent)) {
            textContent.setText(stringpersonContent);
        }

        if (!WatchUtils.isEmpty(stringpersonOne)) {
            if (WatchUtils.isEmpty(nameOne)) nameOne = "RaceFitPro";
            textOne.setText(stringpersonOne);
            textNameOne.setText(nameOne);
        }

        if (!WatchUtils.isEmpty(stringpersonTwo)) {
            if (WatchUtils.isEmpty(nameTwo)) nameTwo = "RaceFitPro";
            textTwo.setText(stringpersonTwo);
            textNameTwo.setText(nameTwo);
        }

        if (!WatchUtils.isEmpty(stringpersonThree)) {
            if (WatchUtils.isEmpty(nameThree)) nameThree = "RaceFitPro";
            textThee.setText(stringpersonThree);
            textNameThee.setText(nameThree);
        }
    }


    private void selectConnection(int type) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, type);
    }


    private Intent mIntent;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                mIntent = data;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                    //申请授权，第一个参数为要申请用户授权的权限；第二个参数为requestCode 必须大于等于0，主要用于回调的时候检测，匹配特定的onRequestPermissionsResult。
//                    //可以从方法名requestPermissions以及第二个参数看出，是支持一次性申请多个权限的，系统会通过对话框逐一询问用户是否授权。
//                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//
//                }else{
//                    //如果该版本低于6.0，或者该权限已被授予，它则可以继续读取联系人。
//                    getContacts(data,1);
//                }
                boolean b = initPermission();
                if (b) {
                    getContacts(mIntent, 1);
                }

                break;
            case 2:
                mIntent = data;

                boolean b2 = initPermission();
                if (b2) {
                    getContacts(mIntent, 2);
                }

                break;
            case 3:
                mIntent = data;
                boolean b3 = initPermission();
                if (b3) {
                    getContacts(mIntent, 3);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void getContacts(Intent data, int type) {
        if (data == null) {
            return;
        }

        Uri contactData = data.getData();
        if (contactData == null) {
            return;
        }
        String name = "";
        String phoneNumber = "";

        Uri contactUri = data.getData();
        Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String hasPhone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }
            cursor.close();
            if (name == null) name = "";
            phoneNumber = phoneNumber.trim().replace(" ", "");
            switch (type) {
                case 1:
                    if (!WatchUtils.isEmpty(phoneNumber.trim())) {
                        if (WatchUtils.isEmpty(name)) name = "RaceFitPro";
                        SharedPreferencesUtils.setParam(HellpEditActivity.this, "personOne", phoneNumber.trim());
                        SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameOne", name);
                        textOne.setText(phoneNumber.trim());
                        textNameOne.setText(name);
                    }
                    break;
                case 2:
                    if (!WatchUtils.isEmpty(phoneNumber.trim())) {
                        if (WatchUtils.isEmpty(name)) name = "RaceFitPro";
                        SharedPreferencesUtils.setParam(HellpEditActivity.this, "personTwo", phoneNumber.trim());
                        SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameTwo", name);
                        textTwo.setText(phoneNumber.trim());
                        textNameTwo.setText(name);
                    }
                    break;
                case 3:
                    if (!WatchUtils.isEmpty(phoneNumber.trim())) {
                        if (WatchUtils.isEmpty(name)) name = "RaceFitPro";
                        SharedPreferencesUtils.setParam(HellpEditActivity.this, "personThree", phoneNumber.trim());
                        SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameThree", name);

                        textThee.setText(phoneNumber.trim());
                        textNameThee.setText(name);
                    }
                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        readPermissStatus();

    }


    @OnClick({R.id.text_content,
            R.id.car_person_one, R.id.car_person_two, R.id.car_person_three,
            R.id.btn_helps,
            R.id.image_one, R.id.image_two, R.id.image_three,

            R.id.sosContPermissionTv,R.id.sosPhonePermissionTv,
            R.id.sosCallLogPermissionTv,R.id.sosContPermission2Tv,
            R.id.sosSMSPermissionTv

        })
    public void onViewClicked(final View view) {

        boolean b = initPermission();
        if (!b) return;

        boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos
        switch (view.getId()) {
            case R.id.text_content:

                break;
            case R.id.car_person_one:
                if (!TextUtils.isEmpty(textOne.getText().toString().trim()) && isSos) {
                    Commont.SENDPHONE_COUNT = 4;
                    Commont.SENDMESSGE_COUNT = 2;
                    //Commont.isGPSed = true;
                    getGps();
                    call(textOne.getText().toString().trim());
                }
                break;
            case R.id.car_person_two:
                if (!TextUtils.isEmpty(textTwo.getText().toString().trim()) && isSos) {
                    Commont.SENDPHONE_COUNT = 4;
                    Commont.SENDMESSGE_COUNT = 2;
                    //Commont.isGPSed = true;
                    getGps();
                    call(textTwo.getText().toString().trim());
                }
                break;
            case R.id.car_person_three:
                if (!TextUtils.isEmpty(textThee.getText().toString().trim()) && isSos) {
                    Commont.SENDPHONE_COUNT = 4;
                    Commont.SENDMESSGE_COUNT = 2;
                    //Commont.isGPSed = true;
                    getGps();
                    call(textThee.getText().toString().trim());
                }
                break;
            case R.id.btn_helps:
                //按了一次
                if ((!TextUtils.isEmpty(textOne.getText().toString().trim())
                        || !TextUtils.isEmpty(textTwo.getText().toString().trim())
                        || !TextUtils.isEmpty(textThee.getText().toString().trim()))
                        && isSos) {
                    Commont.SENDPHONE_COUNT = 4;
                    Commont.SENDMESSGE_COUNT = 2;
                    //Commont.isGPSed = true;
                    getGps();
                    if (!TextUtils.isEmpty(textOne.getText().toString().trim())) {
                        call(textOne.getText().toString().trim());
                    } else {
                        if (!TextUtils.isEmpty(textTwo.getText().toString().trim())) {
                            call(textTwo.getText().toString().trim());
                        } else {
                            if (!TextUtils.isEmpty(textThee.getText().toString().trim())) {
                                call(textThee.getText().toString().trim());
                            }
                        }
                    }
                } else {
//                                    ToastUtil.showShort(HellpEditActivity.this, getResources().getString(R.string.string_sos_tip));
                    ToastUtil.showShort(HellpEditActivity.this, getResources().getString(R.string.string_sos_tip));
                }
                break;
            case R.id.image_one:
                selectConnection(1);
                break;
            case R.id.image_two:
                selectConnection(2);
                break;
            case R.id.image_three:
                selectConnection(3);
                break;


            case R.id.sosContPermissionTv:  //联系人通讯录
                if(!contactsFlag){
                    getContactsPermission(Permission.READ_CONTACTS);
                }else{
                    openPermission();
                }
                break;
            case R.id.sosPhonePermissionTv:     //电话
                if(!callPhoneFlag){
                    getContactsPermission(Permission.CALL_PHONE);
                }else{
                    openPermission();
                }
                break;
            case R.id.sosCallLogPermissionTv:   //通话记录
                if(!callLogFlag){
                    getContactsPermission(Permission.READ_CALL_LOG);
                }else{
                    openPermission();
                }
                break;
            case R.id.sosContPermission2Tv: //联系人，通讯录
                if(!contactsFlag){
                    getContactsPermission(Permission.READ_CONTACTS);
                }else{
                    openPermission();
                }
                break;
            case R.id.sosSMSPermissionTv:   //发送短信
                if(!smsFlag){
                    getContactsPermission(Permission.SEND_SMS,Permission.READ_SMS);
                }else{
                    openPermission();
                }
                break;

        }



    }

    public static String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,//
            Manifest.permission.READ_CONTACTS,//
            Manifest.permission.READ_CALL_LOG,//
            Manifest.permission.USE_SIP};
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;//权限请求码

    //4、权限判断和申请
    private boolean initPermission() {
        boolean isOk = false;
        mPermissionList.clear();//清空已经允许的没有通过的权限
        //逐个判断是否还有未通过的权限
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限到mPermissionList中
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            isOk = false;
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        } else {
            //权限已经都通过了，可以将程序继续打开了
            Log.e("=======", "权限请求完成A");
            isOk = true;
        }
        return isOk;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {//如果有没有被允许的权限
            showPermissionDialog();
        } else {
            //权限已经都通过了，可以将程序继续打开了
            Log.e("=======", "权限请求完成B");
        }


    }


    /**
     * 6.不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;
    String mPackName = "crazystudy.com.crazystudy";

    private void showPermissionDialog() {
        mPackName = HellpEditActivity.this.getPackageName();
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.string_prmiss_close))
                    .setPositiveButton(getResources().getString(R.string.menu_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.
                                    ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();
                            //SplashActivity.this.finish();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }


    GPSGoogleUtils instance;

    void getGpsGoogle() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean b = instance.startLocationUpdates(HellpEditActivity.this);
                if (!b) {
                    getGpsGoogle();
                }
            }
        }, 3000);
    }

    void getGps() {

//      GPSGaoDeUtils.getInstance(HellpEditActivity.this);
//        boolean zh = VerifyUtil.isZh(HellpEditActivity.this);
//        if (zh) {
//            GPSGaoDeUtils.getInstance(HellpEditActivity.this);
//        } else {
//            instance = GPSGoogleUtils.getInstance(HellpEditActivity.this);
//            getGpsGoogle();
//        }
        boolean zh = VerifyUtil.isZh(MyApp.getInstance());
        if (zh) {
            Boolean zhonTW = getResources().getConfiguration().locale.getCountry().equals("TW");
            Log.e("======", zh + "====" + zhonTW);
            if (zhonTW) {
                instance = GPSGoogleUtils.getInstance(MyApp.getInstance());
                getGpsGoogle();
            } else {
                GPSGaoDeUtils.getInstance(MyApp.getInstance());
            }
        } else {
            instance = GPSGoogleUtils.getInstance(MyApp.getInstance());
            getGpsGoogle();
        }
    }


    //点击事件调用的类

    protected void call(final String tel) {

        //直接拨打
//                        Log.d("GPS", "call:" + tel);
        Uri uri = Uri.parse("tel:" + tel);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (ActivityCompat.checkSelfPermission(HellpEditActivity.this, Manifest.permission.CALL_PHONE) != PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }


    // 显示对话框
    public void showWaiterAuthorizationDialog(final int type) {

        showKeyboard(true);
        // LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化
        LayoutInflater factory = LayoutInflater.from(HellpEditActivity.this);
        // 把布局文件中的控件定义在View中
        final View textEntryView = factory.inflate(R.layout.chage_phone_item, null);
        // 获取用户输入的验证码
        // 注意：textEntryView.findViewById很重要，因为上面factory.inflate(R.layout.activity_mobile_authentication_check_code,
        // null)将页面布局赋值给了textEntryView了
        final EditText name = (EditText) textEntryView.findViewById(R.id.user_names);
        final EditText phone = (EditText) textEntryView.findViewById(R.id.user_phones);
        final EditText content = (EditText) textEntryView.findViewById(R.id.help_content);
//        String title = getResources().getString(R.string.string_edit_emergency_messge);
        String title = "请编辑紧急信息";
        if (type == 0) {
            title = getResources().getString(R.string.string_edit_emergency_messge);
//            title = "请编辑紧急信息";
            content.setVisibility(View.VISIBLE);

            name.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
        } else {
            title = getResources().getString(R.string.string_edit_emergency_person);
//            title = "请编辑紧急联系人";
            name.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);

            content.setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(textEntryView)
                //确定
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String userPhone = phone.getText().toString().trim().replace(" ", "");
                        String userNames = name.getText().toString().trim();
                        switch (type) {
                            case 0:
                                textContent.setText(content.getText().toString().trim());
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "personContent", textContent.getText().toString().trim());

                                break;
                            case 1:
                                if ((!WatchUtils.isEmpty(userPhone) && !WatchUtils.isEmpty(userNames))
                                        || (!WatchUtils.isEmpty(userPhone) && WatchUtils.isEmpty(userNames))) {

                                    if (WatchUtils.isEmpty(userNames)) userNames = "RaceFitPro";
                                } else {
                                    userPhone = "";
                                    userNames = "";
                                }
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "personOne", userPhone);
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameOne", userNames);
                                textOne.setText(userPhone);
                                textNameOne.setText(userNames);
                                break;
                            case 2:
                                if ((!WatchUtils.isEmpty(userPhone) && !WatchUtils.isEmpty(userNames))
                                        || (!WatchUtils.isEmpty(userPhone) && WatchUtils.isEmpty(userNames))) {

                                    if (WatchUtils.isEmpty(userNames)) userNames = "RaceFitPro";

                                } else {
                                    userPhone = "";
                                    userNames = "";
                                }
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "personTwo", userPhone);
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameTwo", userNames);

                                textTwo.setText(userPhone);
                                textNameTwo.setText(userNames);
//
//                                textTwo.setText(phone.getText().toString().trim());
//                                textNameTwo.setText(name.getText().toString().trim());
//                                if ((!WatchUtils.isEmpty(phone.getText().toString().trim()) && !WatchUtils.isEmpty(name.getText().toString().trim()))
//                                        || (!WatchUtils.isEmpty(phone.getText().toString().trim()) && WatchUtils.isEmpty(name.getText().toString().trim()))) {
//                                    String names = name.getText().toString().trim();
//                                    if (WatchUtils.isEmpty(names)) names = "RingmiiHX";
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "personTwo", textTwo.getText().toString().trim());
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameTwo", names);
//                                } else {
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "personTwo", "");
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameTwo", "");
//                                }

                                break;
                            case 3:
                                if ((!WatchUtils.isEmpty(userPhone) && !WatchUtils.isEmpty(userNames))
                                        || (!WatchUtils.isEmpty(userPhone) && WatchUtils.isEmpty(userNames))) {

                                    if (WatchUtils.isEmpty(userNames)) userNames = "RaceFitPro";

                                } else {
                                    userPhone = "";
                                    userNames = "";
                                }
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "personThree", userPhone);
                                SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameThree", userNames);
                                textThee.setText(userPhone);
                                textNameThee.setText(userNames);


//                                textThee.setText(phone.getText().toString().trim());
//                                textNameThee.setText(name.getText().toString().trim());
//                                if ((!WatchUtils.isEmpty(phone.getText().toString().trim()) && !WatchUtils.isEmpty(name.getText().toString().trim()))
//                                        || (!WatchUtils.isEmpty(phone.getText().toString().trim()) && WatchUtils.isEmpty(name.getText().toString().trim()))) {
//                                    String names = name.getText().toString().trim();
//                                    if (WatchUtils.isEmpty(names)) names = "RingmiiHX";
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "personThree", textThee.getText().toString().trim());
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameThree", names);
//                                } else {
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "personThree", "");
//                                    SharedPreferencesUtils.setParam(HellpEditActivity.this, "NameThree", "");
//                                }

                                break;
                        }

                        showKeyboard(false);
                    }
                })
                //取消
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showKeyboard(false);
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(16);
    }


//    public static boolean isMobileNO(String mobileNums) {
//        /**
//         * 判断字符串是否符合手机号码格式
//         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
//         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
//         * 电信号段: 133,149,153,170,173,177,180,181,189
//         * @param str
//         * @return 待检测的字符串
//         */
//        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobileNums))
//            return false;
//        else
//            return mobileNums.matches(telRegex);
//    }


    @Override
    public boolean onLongClick(final View view) {

        boolean b = initPermission();
        if (b) {
            switch (view.getId()) {
                case R.id.text_content:
                    showWaiterAuthorizationDialog(0);
                    break;
                case R.id.car_person_one:
                    showWaiterAuthorizationDialog(1);
                    break;
                case R.id.car_person_two:
                    showWaiterAuthorizationDialog(2);
                    break;
                case R.id.car_person_three:
                    showWaiterAuthorizationDialog(3);
                    break;
            }
        }
        return false;
    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (null == imm)
            return;

        if (isShow) {
            if (getCurrentFocus() != null) {
                //有焦点打开
                imm.showSoftInput(getCurrentFocus(), 0);
            } else {
                //无焦点打开
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                //有焦点关闭
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                //无焦点关闭
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }




    //读取是否获取权限
    private void readPermissStatus(){
        //判断是否有联系人的权限
        if(AndPermission.hasPermissions(HellpEditActivity.this,Manifest.permission.READ_CONTACTS)){
            sosContPermissionTv.setText(getResources().getString(R.string.string_enable));
            sosContPermission2Tv.setText(getResources().getString(R.string.string_enable));
            contactsFlag = true;
        }else{
            contactsFlag = false;
            sosContPermissionTv.setText(getResources().getString(R.string.string_disable));
            sosContPermission2Tv.setText(getResources().getString(R.string.string_disable));
        }

        //拨打电话
        if(AndPermission.hasPermissions(HellpEditActivity.this,Manifest.permission.CALL_PHONE)){
            callPhoneFlag = true;
            sosPhonePermissionTv.setText(getResources().getString(R.string.string_enable));
        }else{
            callPhoneFlag = false;
            sosPhonePermissionTv.setText(getResources().getString(R.string.string_disable));
        }

        //通话记录
        if(AndPermission.hasPermissions(HellpEditActivity.this,Manifest.permission.READ_CALL_LOG)){
            callLogFlag = true;
            sosCallLogPermissionTv.setText(getResources().getString(R.string.string_enable));
        }else {
            callLogFlag = false;
            sosCallLogPermissionTv.setText(getResources().getString(R.string.string_disable));
        }

        //短信
        if(AndPermission.hasPermissions(HellpEditActivity.this,Manifest.permission.READ_SMS)){
            smsFlag = true;
            sosSMSPermissionTv.setText(getResources().getString(R.string.string_enable));
        }else{
            smsFlag = false;
            sosSMSPermissionTv.setText(getResources().getString(R.string.string_disable));
        }
    }

    //获取相关权限
    private void getContactsPermission(String...permission){
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
                readPermissStatus();
            }
        }).start();
    }

    //打开权限设置页面
    private void openPermission(){
        AndPermission.with(HellpEditActivity.this)
                .runtime().setting().start(1001);
    }



}
