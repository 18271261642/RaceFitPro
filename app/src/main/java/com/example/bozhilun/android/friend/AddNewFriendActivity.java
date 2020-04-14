package com.example.bozhilun.android.friend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.friend.bean.PhoneBean;
import com.example.bozhilun.android.friend.bean.PhoneDto;
import com.example.bozhilun.android.friend.bean.PhoneitemBean;
import com.example.bozhilun.android.friend.views.CharPortraitView;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @aboutContent: 添加好友
 * @author： An
 * @crateTime: 2018/3/9 18:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class AddNewFriendActivity   extends WatchBaseActivity  implements View.OnTouchListener, TextWatcher, View.OnClickListener,
        TextView.OnEditorActionListener, RequestView {
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.edit_seach)
    EditText editSeach;
    @BindView(R.id.frend_find_ok)
    TextView frendFindOk;
    @BindView(R.id.toolbar_normal)
    Toolbar mNormalToolbar;
    @BindView(R.id.imahe_list_heard)
    CircleImageView imaheListHeard;
    @BindView(R.id.user_names)
    TextView userNames;
    @BindView(R.id.frend_sex)
    TextView frendSex;
    @BindView(R.id.frend_birthday)
    TextView frendBirthday;
    @BindView(R.id.find_frend_item)
    LinearLayout findFrendItem;
    @BindView(R.id.btn_find)
    Button btnAdd;
    @BindView(R.id.frend_list_phone)
    ListView frendListPhone;
    private RequestPressent requestPressent;
    private List<PhoneDto> phoneDtos;

    private MyAdapter myAdapter;
    List<PhoneBean.CheckRegisterBean> tmpList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fredens_add);
        ButterKnife.bind(this);


        inEdit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        check();
    }

    /**
     * 检查权限
     */
    private void check() {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(AddNewFriendActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(AddNewFriendActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(AddNewFriendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewFriendActivity.this, new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        } else {
            initViews();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            initViews();
        }
    }


    /**
     * 将 list 转换 为 JSONArray
     *
     * @param list
     * @return
     */
    public JSONArray ProLogListJson(List<PhoneitemBean> list) {
        JSONArray json = new JSONArray();
        for (PhoneitemBean pLog : list) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("phone", pLog.getPhone());
                jo.put("contacts", pLog.getContacts());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json.put(jo);
        }
        return json;
    }


    List<PhoneitemBean> phoneitemBeans = null;
    List<String> stringsNumber = null;


    private void initViews() {
        PhoneUtil phoneUtil = new PhoneUtil(this);
        phoneDtos = phoneUtil.getPhone();

//        String phoneUser = "";
//        for (int i = 0; i < phoneDtos.size(); i++) {
//            phoneUser += phoneDtos.get(i).getTelPhone() + ",";
//        }
        // Log.e("====返回=====", phoneDtos.toString() + "==" + phoneDtos.size());
        if (phoneitemBeans == null) phoneitemBeans = new ArrayList<>();
        else phoneitemBeans.clear();
        if (stringsNumber == null) stringsNumber = new ArrayList<>();
        else stringsNumber.clear();
        if(phoneDtos == null)
            return;
        for (int i = 0; i < phoneDtos.size(); i++) {
            if (!stringsNumber.contains(phoneDtos.get(i).getTelPhone())) {
                PhoneitemBean phoneitemBean = new PhoneitemBean(phoneDtos.get(i).getTelPhone(), phoneDtos.get(i).getName());
                phoneitemBeans.add(phoneitemBean);
                stringsNumber.add(phoneDtos.get(i).getTelPhone());
                //Log.e("====好友=====", i + "==" + phoneDtos.get(i).getTelPhone() + "==" + phoneDtos.get(i).getName());
            }
        }
        // Log.e("====返回2=====", phoneitemBeans.toString() + "==" + phoneitemBeans.size());
        JSONArray jsonArray = ProLogListJson(phoneitemBeans);
        checkExitRegister(jsonArray);

    }


    @SuppressLint("ClickableViewAccessibility")
    private void inEdit() {

        tmpList = new ArrayList<>();
        myAdapter = new MyAdapter(tmpList);
        frendListPhone.setAdapter(myAdapter);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        /**
         * 去掉二维码步骤一
         */
        mNormalToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_erweima));
        setSupportActionBar(mNormalToolbar);

        //设置标题
        barTitles.setText(getResources().getString(R.string.add_frendes));

        mNormalToolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        mNormalToolbar.setNavigationOnClickListener(this);//右边返回按钮点击事件


        //rwb：取消EditText焦点，并且隐藏输入法。
        editSeach.setOnTouchListener(this);
        editSeach.addTextChangedListener(this);//内容改变监听
        editSeach.setHint(getResources().getString(R.string.ssdk_sms_phone) + "/" + getResources().getString(R.string.ssdk_instapaper_email));//电话/邮箱
        editSeach.setOnEditorActionListener(this);
    }

    /**
     * 去掉二维码步骤二
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frend_menu_er, menu);
        return true;
    }


    private int REQUEST_CODE_SCAN = 0x08;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.action_mine_er) {
            Log.d("-----------", "我的二维码");
            Intent intent = new Intent(AddNewFriendActivity.this, ErCodeActity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_frend_er) {
            Log.d("-----------", "去扫锚啊");


            Intent intent = new Intent(AddNewFriendActivity.this, CaptureActivity.class);
            /*ZxingConfig是配置类
             *可以设置是否显示底部布局，闪光灯，相册，
             * 是否播放提示音  震动
             * 设置扫描框颜色等
             * 也可以不传这个参数
             * */
            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(true);//是否播放扫描声音 默认为true
            config.setShake(true);//是否震动  默认为true
            config.setDecodeBarCode(false);//是否扫描条形码 默认为true
            config.setReactColor(R.color.new_colorAccent);//设置扫描框四个角的颜色 默认为白色
            config.setFrameLineColor(R.color.new_colorAccent);//设置扫描框边框颜色 默认无色
            config.setScanLineColor(R.color.new_colorAccent);//设置扫描线的颜色 默认白色
            config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                //ToastUtil.showShort(AddNewFriendActivity.this, "扫描结果为：" + content);
                String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
                if (!TextUtils.isEmpty(content)
                        && !TextUtils.isEmpty(userId)) {
                    closeKey();
                    findFrendItem(content, userId);
                } else {
                    ToastUtil.showShort(this, getResources().getString(R.string.string_network_error));//输入不能为空
                }
            }
        }
    }

    /**
     * 取消EditText焦点，并且隐藏输入法。
     *
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        editSeach.setFocusable(true);
        editSeach.setFocusableInTouchMode(true);
        editSeach.requestFocus();
        closeKey();
        return false;
    }

    /**
     * 隐藏输入法。
     */
    void closeKey() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputmanger != null)
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 输入改变监听
     *
     * @param charSequence
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        //这个方法被调用，说明在charSequence字符串中，从start位置开始的count个字符即将被长度为after的新文本所取代。在这个方法里面改变s，会报错。
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        //这个方法被调用，说明在charSequence字符串中，从start位置开始的count个字符刚刚取代了长度为before的旧文本。在这个方法里面改变charSequence，会报错。
        if (charSequence.toString().length() > 0) {
            frendFindOk.setVisibility(View.VISIBLE);
        } else {
            frendFindOk.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //这个方法被调用，那么说明editable字符串的某个地方已经被改变。
    }

    /**
     * 右边返回按钮点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_SEND ||
                (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

            String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
            if (editSeach != null && !TextUtils.isEmpty(editSeach.getText().toString().trim())
                    && !TextUtils.isEmpty(userId)) {
                closeKey();
                String frendId = editSeach.getText().toString().trim();
                findFrendItem(frendId, userId);
            } else {
                ToastUtil.showShort(this, getResources().getString(R.string.ssdk_sms_top_text) + "/" + getResources().getString(R.string.ssdk_instapaper_email));//输入不能为空
            }
            return true;
        }
        return false;

    }


    /************************************************/


    /**
     * 查找朋友
     *扫描的参数为
     * @param userId
     */
    public void findFrendItem(String phone, String userId) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FindFrend;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("phone", phone);
            sleepJson.put("userId", userId);
            Log.d("-----------朋友--", " 搜索好友参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x01, sleepUrl, AddNewFriendActivity.this, sleepJson.toString(), 0);
        }
    }

    void checkExitRegister(JSONArray jsonArray) {

        String userId = (String) SharedPreferencesUtils.readObject(AddNewFriendActivity.this, "userId");
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.PhoneIsRegister;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("contactsList", jsonArray);
            Log.e("-----------朋友--", " 检查手机号是否注册--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x03, sleepUrl, AddNewFriendActivity.this, sleepJson.toString(), 0);
        }
    }


    /**
     * 添加朋友
     *
     * @param userId
     */
    public void addFrendItem(String userId, String frendUserID) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.ApplyAddFind;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("applicant", frendUserID);
            Log.d("-----------朋友-", "-----添加好友参数---" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, sleepUrl, AddNewFriendActivity.this, sleepJson.toString(), 0);
        }
    }

    private String frendUerID = "";


    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    /**
     * @param object
     * @param daystag
     */

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object == null || TextUtils.isEmpty(object.toString().trim()) || object.toString().contains("<html>"))
            return;
        switch (what) {
            case 0x01:  //搜索好友返回
                analysisSearchFriendBack(object.toString());
                break;
            case 0x02:  //添加好友请求返回
                Log.d("-----------添加朋友返回--", object.toString());
                analysisAddFriendBack(object.toString());
                break;
            case 0x03:
                Log.d("-----------手机号检测返回--", object.toString());
                analysisCheckPhone(object.toString());
                break;
        }

    }



    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    @OnClick({R.id.frend_find_ok, R.id.btn_find})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.frend_find_ok://清除去搜索
                editSeach.setText("");
                closeKey();
                if (findFrendItem != null) findFrendItem.setVisibility(View.GONE);
                break;
            case R.id.btn_find://添加好友
                String userId = (String) SharedPreferencesUtils.readObject(this, "userId");
                if (!TextUtils.isEmpty(frendUerID) && !TextUtils.isEmpty(userId)) {
                    addFrendItem(userId, frendUerID);
                }
                break;
        }
    }




    //检查手机号返回
    private void analysisCheckPhone(String data) {
        tmpList.clear();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if(jsonObject.getInt("code") == 200){
                String phoneData = jsonObject.getString("data");
                if(!WatchUtils.isEmpty(phoneData)){
                    List<PhoneBean.CheckRegisterBean> phoneList = new Gson().fromJson(phoneData,
                            new TypeToken<List<PhoneBean.CheckRegisterBean>>(){}.getType());
                    if(phoneList == null || phoneList.isEmpty())
                        return;
                    for(PhoneBean.CheckRegisterBean pc : phoneList){
                        if(pc.getIsFriend() == 0 && pc.getUser() != null){
                            tmpList.add(pc);
                        }
                    }

                    myAdapter.notifyDataSetChanged();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //搜索好友返回
    private void analysisSearchFriendBack(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if(jsonObject.getInt("code") == 200){
                if (findFrendItem != null) findFrendItem.setVisibility(View.VISIBLE);
                UserInfoBean userInfo = new Gson().fromJson(jsonObject.getString("data"),UserInfoBean.class);
                frendUerID = userInfo.getUserid();
                String nickName = userInfo.getNickname();
                String phone = userInfo.getPhone();
                String image = (String) userInfo.getImage();
                String birthday = userInfo.getBirthday();
                String sex = userInfo.getSex();
                int friendStatus = userInfo.getFriendStatus();
                if (friendStatus == 0) {//陌生人
                    btnAdd.setText(getResources().getString(R.string.string_apply_added));//申请添加
                    btnAdd.setEnabled(true);
                    btnAdd.setBackgroundColor(getResources().getColor(R.color.new_colorAccent));
                } else {//已经是好友
                    btnAdd.setText(getResources().getString(R.string.string_wite_added));//已添加
                    btnAdd.setEnabled(false);
                    btnAdd.setBackgroundColor(Color.GRAY);
                }
                if (!TextUtils.isEmpty(nickName) && userNames != null) {
                    userNames.setText(nickName);
                } else if (!TextUtils.isEmpty(phone) && userNames != null) {
                    userNames.setText(phone);
                }
                if (!TextUtils.isEmpty(image) && imaheListHeard != null) {
                    Glide.with(AddNewFriendActivity.this).load(image)
                            .into(imaheListHeard);
                } else {
                    Glide.with(AddNewFriendActivity.this).load(R.mipmap.bg_img).into(imaheListHeard);
                }
                if (!TextUtils.isEmpty(sex)) {
                    if (sex.equals("M") || sex.equals("男")) {
                        frendSex.setText(getResources().getString(R.string.sex_nan));
                    } else {
                        frendSex.setText(getResources().getString(R.string.sex_nv));
                    }
                } else {
                    frendSex.setText("");
                }
                if (!TextUtils.isEmpty(birthday)) {
                    int ageFromBirthTime = WatchUtils.getAgeFromBirthTime(birthday);
                    frendBirthday.setText("" + ageFromBirthTime);
                }
            }else{
                ToastUtil.showToast(AddNewFriendActivity.this,jsonObject.getString("msg")+"");
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //添加好友返回
    private void analysisAddFriendBack(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("code")) {
                int code = jsonObject.getInt("code");
                if (code != 200) {

                    if (code == 3002) {   //已经申请过了
                        btnAdd.setText(getResources().getString(R.string.string_wite_verified));//已添加
                        btnAdd.setEnabled(false);
                        btnAdd.setBackgroundColor(Color.GRAY);
                    }
                    ToastUtil.showToast(this, jsonObject.getString("msg"));


                } else {
                    btnAdd.setText(getResources().getString(R.string.string_wite_verified));//已添加等待验证
                    btnAdd.setEnabled(false);
                    btnAdd.setBackgroundColor(Color.GRAY);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //自定义适配器,查看通讯录
    private class MyAdapter extends BaseAdapter {
        List<PhoneBean.CheckRegisterBean> checkRegister;
        private LayoutInflater layoutInflater;


        public MyAdapter(List<PhoneBean.CheckRegisterBean> phoneDtos) {
            this.checkRegister = phoneDtos;
            layoutInflater = LayoutInflater.from(AddNewFriendActivity.this);
        }

        @Override
        public int getCount() {
            if (checkRegister == null) return 0;
            else return checkRegister.size();
        }

        @Override
        public Object getItem(int position) {
            return checkRegister.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.phone_frend_list_item,parent,false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.tv_name_list);
                holder.portrait = convertView.findViewById(R.id.cv_portrait);
                holder.btnAdd = convertView.findViewById(R.id.btn_find_list);
                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }


            final PhoneBean.CheckRegisterBean checkRegisterBean = checkRegister.get(position);
            String name = checkRegisterBean.getContacts();
            String telPhone = checkRegisterBean.getPhone();
            if (!WatchUtils.isEmpty(telPhone)) {
                holder.portrait.setContent(name).setHead(false);
                holder.tvName.setText(telPhone);
            }

            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String trim = checkRegisterBean.getPhone().trim();
                    String userId = (String) SharedPreferencesUtils.readObject(AddNewFriendActivity.this, "userId");
                    if (!TextUtils.isEmpty(trim)
                            && !TextUtils.isEmpty(userId)) {
                        closeKey();
                        findFrendItem(trim, userId);
                    } else {
                        ToastUtil.showShort(AddNewFriendActivity.this, getResources().getString(R.string.ssdk_sms_top_text) + "/" + getResources().getString(R.string.ssdk_instapaper_email));//输入不能为空
                    }
                    frendListPhone.smoothScrollToPosition(0);

                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


        class ViewHolder{
            Button btnAdd;
            TextView tvName;
            CharPortraitView portrait;
        }
    }
}
