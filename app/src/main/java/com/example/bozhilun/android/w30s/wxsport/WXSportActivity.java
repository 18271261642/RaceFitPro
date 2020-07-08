package com.example.bozhilun.android.w30s.wxsport;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 微信运动
 */
public class WXSportActivity extends WatchBaseActivity implements RequestView {

    private static final String TAG = "WXSportActivity";

    //B30的APPID
    private static final String B30_APP_ID = "wx076e40e288205e3e";
    //B30的AppSecret
    private static final String B30_APPSECRET = "5f31743be3afdfcb76c9be748859aac5";


    //B30的Product_id
    private static final String B30_PRODUCT_ID = "49936";

    //B31的Product_id
    private static final String B31_PRODUCT_ID = "51635";

    //B36
    private static final String B36_PRODUCT_ID = "50786";

    //W30
    private static final String W30_PRODUCT_ID = "47728";

    //xWatch
    private static final String X_WATCH_PRODUCT_ID = "56201";

    private String COMM_DEVICE_PRODUCT_ID = null;


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.wxSportViewPager)
    ViewPager wxSportViewPager;
    @BindView(R.id.linPointLin)
    LinearLayout linPointLin;
    @BindView(R.id.wxSportShowStepTv)
    TextView wxSportShowStepTv;
    @BindView(R.id.wxSportBindBtn)
    Button wxSportBindBtn;
    private WXSportViewPagerAdapter wxSportViewPagerAdapter;


    //图集合
    Integer[] urlStr = new Integer[]{R.drawable.wx_sport1, R.drawable.wx_sport2,
            R.drawable.wx_sport3, R.drawable.wx_sport4, R.drawable.wx_sport5,
            R.drawable.wx_sport6};


    private String showStepTxt ="点击下方“绑定”按钮，绑定设备。绑定成功后，状态会更改为“已绑定”，则进行下一步";
    private RequestPressent requestPressent;
    private String tokenStr = null;

    private String bleName = null;

    String bleMac = null;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_sport);
        ButterKnife.bind(this);


        initViews();
        bleMac = (String) SharedPreferencesUtils.readObject(WXSportActivity.this,Commont.BLEMAC);
        initData();
        bleName = getIntent().getStringExtra("bleName");
        if(!WatchUtils.isEmpty(bleName)){
            verticalBleDevice(bleName);
        }
        showVisOrInvis();

    }

    private void verticalBleDevice(String bleName) {
        if(bleName.equals("B30")){
            COMM_DEVICE_PRODUCT_ID = B30_PRODUCT_ID;
            return;
        }

        if(bleName.equals("B31")){
            COMM_DEVICE_PRODUCT_ID = B31_PRODUCT_ID;
            return;
        }
        if(bleName.equals("B36") || bleName.equals("B36M")){
            COMM_DEVICE_PRODUCT_ID = B36_PRODUCT_ID;
            return;
        }

        if(bleName.equals("W30") || bleName.equals("W31") || bleName.equals("W37")){
            COMM_DEVICE_PRODUCT_ID = W30_PRODUCT_ID;
            return;
        }
        if(bleName.equals("XWatch")){
            COMM_DEVICE_PRODUCT_ID = W30_PRODUCT_ID;//X_WATCH_PRODUCT_ID;
        }
    }

    private void showVisOrInvis() {
        String jsonStr = (String) SharedPreferencesUtils.getParam(WXSportActivity.this,"wx_bind","");
        if(WatchUtils.isEmpty(jsonStr)){
            wxSportBindBtn.setEnabled(true);
            return;
        }

        try {
            JSONObject jso = new JSONObject(jsonStr);
            if(!jso.has(bleMac)){
                wxSportBindBtn.setEnabled(true);
                return;
            }
            boolean isBind = jso.getBoolean(bleMac);
            if(isBind){
                wxSportBindBtn.setEnabled(false);
                wxSportBindBtn.setText(getResources().getString(R.string.bangdinged));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        wxSportViewPagerAdapter = new WXSportViewPagerAdapter(verLanShowImg());
        wxSportViewPager.setAdapter(wxSportViewPagerAdapter);
        wxSportViewPager.setCurrentItem(0);
        wxSportShowStepTv.setText(verLanguage(0));
        wxSportViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changePoint(position);
                showStepTxt = verLanguage(position);
                wxSportShowStepTv.setText(showStepTxt);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    private void changePoint(int position) {
        wxSportViewPager.setCurrentItem(position);
        for (int j = 0; j < urlStr.length; j++) {
            ImageView childAt1 = (ImageView) linPointLin.getChildAt(j);
            childAt1.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
            childAt1.setMaxHeight(1);
            childAt1.setMaxWidth(1);
        }
        ImageView childAt = (ImageView) linPointLin.getChildAt(position);
        childAt.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
        childAt.setMaxHeight(1);
        childAt.setMaxWidth(1);
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.weixinpaizhao));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < urlStr.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setPadding(3, 0, 3, 0);
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img));
            if (i == 0)
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.point_img_s));
            imageView.setMaxHeight(1);
            imageView.setMaxWidth(1);
            imageView.setMinimumHeight(1);
            imageView.setMinimumWidth(1);
            linPointLin.addView(imageView);
        }


    }

    @OnClick(R.id.wxSportBindBtn)
    public void onClick() {
        getWxTokenFirst();

    }

    //第一步，获取微信的token
    private void getWxTokenFirst() {
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+B30_APP_ID+"&secret="+B30_APPSECRET;
        if(requestPressent != null)
            requestPressent.getRequestJSONObject(1,tokenUrl,WXSportActivity.this,1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        //Log.e(TAG,"-----succ="+object.toString());
        if(what == 1){  //获取token
            try {
                JSONObject jsonObject = new JSONObject((String)object);
                String tokenData = jsonObject.getString("access_token");
                //Log.e(TAG,"--------token="+tokenData);
                if(!WatchUtils.isEmpty(tokenData)){
                    this.tokenStr = tokenData;
                    requestGrcode(tokenData);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(what == 2){
            try {
                JSONObject jsonObject = new JSONObject((String)object);
                if(!jsonObject.has("deviceid"))
                    return;
                String deviceId = jsonObject.getString("deviceid");
                if(!WatchUtils.isEmpty(deviceId))
                    postSportDataDeviceToWX(deviceId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(what == 3){
            try {
                JSONObject jsonObject = new JSONObject((String)object);
                if(!jsonObject.has("resp"))
                    return;
                JSONArray jsonArray = jsonObject.getJSONArray("resp");
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                int errorCode = jsonObject1.getInt("errcode");
                if(errorCode == 0){
                    ToastUtil.showToast(WXSportActivity.this,"绑定成功!");
                    wxSportBindBtn.setEnabled(false);
                    wxSportBindBtn.setText(getResources().getString(R.string.bangdinged));
                    if(!WatchUtils.isEmpty(bleMac)){
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put(bleMac,true);
                        SharedPreferencesUtils.setParam(WXSportActivity.this,"wx_bind",jsonObject2.toString());
                    }

                }else{
                    ToastUtil.showToast(WXSportActivity.this,jsonObject1.getString("errmsg"));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    //第三步通过调用微信的授权接口将设备MAC更新到设备编号上。
    private void postSportDataDeviceToWX(String deviceID){
        String deviceUrl = "https://api.weixin.qq.com/device/authorize_device?access_token="+tokenStr;
        if(requestPressent != null){
            String bleMacData = null;
            JSONArray jsonArray = new JSONArray();
            Gson gson = new Gson();
            JSONObject jsonMap = new JSONObject();
            //String bleMac = (String) SharedPreferencesUtils.readObject(WXSportActivity.this,Commont.BLEMAC);
            if(!WatchUtils.isEmpty(bleMac)){
                bleMacData = bleMac;

            }
            String tmpMac = (String) SharedPreferencesUtils.readObject(WXSportActivity.this,Commont.BLEMAC);
            Log.e(TAG,"----mac="+bleMac);
            if(!WatchUtils.isEmpty(tmpMac))
                bleMacData = tmpMac;

            String resultStr = StringUtils.remove(bleMacData,":");
            //Log.e(TAG,"----resultStr="+resultStr);
            try{
                jsonMap.put("id",deviceID);
                jsonMap.put("mac",resultStr);
                jsonMap.put("connect_protocol","1");
                jsonMap.put("auth_key","");
                jsonMap.put("close_strategy","1");
                jsonMap.put("conn_strategy","4");
                jsonMap.put("crypt_method","0");
                jsonMap.put("auth_ver","0");
                jsonMap.put("manu_mac_pos","-1");
                jsonMap.put("ser_mac_pos","-2");
                jsonMap.put("ble_simple_protocol","1");
            }catch (Exception e){
                e.printStackTrace();
            }
            jsonArray.put(jsonMap);
            //Log.e(TAG,"----jsonMap="+gson.toJson(jsonMap));
            JSONObject mps = new JSONObject();
            try{
                mps.put("device_num","1");
                mps.put("device_list",jsonArray);
                mps.put("op_type","1");
                mps.put("product_id",COMM_DEVICE_PRODUCT_ID);
            }catch (Exception e){
                e.printStackTrace();
            }

            //Log.e(TAG,"------mpsStr="+mps.toString());
            requestPressent.getRequestJSONObject(3,deviceUrl,WXSportActivity.this,mps.toString(),1);

        }
    }



    //第二步，获取grcode
    private void requestGrcode(String tokenData) {
        String url = "https://api.weixin.qq.com/device/getqrcode?access_token="
                +tokenData+"&product_id="+COMM_DEVICE_PRODUCT_ID;//+B30_PRODUCT_ID;
        if(requestPressent != null){
            requestPressent.getRequestJSONObject(2,url,this,2);
        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        if(e != null)
            ToastUtil.showToast(WXSportActivity.this,""+e.getMessage());
    }

    @Override
    public void closeLoadDialog(int what) {

    }


    //ViewPager的适配器
    private class WXSportViewPagerAdapter extends PagerAdapter {

        private Integer[] urlList;

        public WXSportViewPagerAdapter(Integer[] urlList) {
            this.urlList = urlList;
        }

        @Override
        public int getCount() {
            return urlList.length;
        }


        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
            container.removeView((View) object);
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int url = urlList[position];
            ImageView imageView = new ImageView(WXSportActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            container.addView(imageView);
            imageView.setImageResource(url);

            return imageView;
        }
    }


    //判断系统语言
    private String verLanguage(int postion){
        //语言
        String locals = Locale.getDefault().getLanguage();
        //地区
        Locale locale = getResources().getConfiguration().locale;
        if(!WatchUtils.isEmpty(locals)){
            if(locals.equals("zh")){
                if(locale.getCountry().equals("TW")){
                    return descTwStr[postion];
                }else {
                    return descZhStr[postion];
                }
            }else{
                return descEnStr[postion];
            }
        }else{
            return descEnStr[postion];
        }

    }

    private Integer[] verLanShowImg(){
        //语言
        String locals = Locale.getDefault().getLanguage();
        //地区
        Locale locale = getResources().getConfiguration().locale;
        if(!WatchUtils.isEmpty(locals)){
            if(locals.equals("zh")){
                if(locale.getCountry().equals("TW")){
                    return twImg;
                }else {
                    return zhImg;
                }
            }else{
                return enImg;
            }
        }else{
            return enImg;
        }
    }





    String zhDesc1 = "点击下方“绑定”按钮，绑定设备。绑定成功后，状态会更改为“已绑定”，RaceFitPro APP断开设备连接，则进行下一步";
    String zhDesc2 = "打开“微信”，搜索进入“微信运动”公众号，点击右上角“图标”";
    String zhDesc3 = "点击“数据来源”";
    String zhDesc4 = "点击“添加数据来源”";
    String zhDesc5 = "在搜索结果中，点击“RaceFit”添加设备";
    String zhDesc6 = "设备添加成功，“数据来源”会显示“RaceFit”，进入“RaceFit”公众号，则设备的数据会自动同步至微信运动";

    String twDesc1 = "點擊下方“綁定”按鈕，綁定設備。綁定成功後，狀態會更改為“已綁定”，RaceFitPro APP斷開設備連接，則進行下一步";
    String twDesc2 = "打開“微信”，搜索進入“微信運動”公眾號，點擊右上角“圖標”";
    String twDesc3 = "點擊“數據來源”";
    String twDesc4 = "點擊“添加數據來源”";
    String twDesc5 = "在搜索結果中，點擊“RaceFit”添加設備";
    String twDesc6 = "設備添加成功，“數據來源”會顯示“RaceFit”，進入“RaceFit”公眾號，則設備的數據會自動同步至微信運動";

    String enDesc1 = "Click the ”Bind“ button below to bind the device. After the binding is successful, The RaceFitPro APP disconnects the device，the status changes to ”Binded“ and the next step is taken.";
    String enDesc2 = "Open ”WeChat“, search for the “WeChat Sports” public number, click on the ”icon“ in the upper right corner";
    String enDesc3 = "Click on ”Data Sources“";
    String enDesc4 = "Click on “Add Data Source“";
    String enDesc5 = "In the search results, click on ”RaceFit“ to add the device.";
    String enDesc6 = "If the device is added successfully, “Data Source” will display “RaceFit”, Enter the 'RaceFit' public number the device data will be automatically synchronized to WeChat Sports.";

    //简体中文的图片
    private Integer[] zhImg = new Integer[]{R.drawable.wx_sport1, R.drawable.wx_sport2,
            R.drawable.wx_sport3, R.drawable.wx_sport4, R.drawable.wx_sport5,
            R.drawable.wx_sport6};
    //描述
    private String[] descZhStr = new String[]{zhDesc1,zhDesc2,zhDesc3,zhDesc4,zhDesc5,zhDesc6};


    //繁体中文的图片
    private Integer[] twImg = new Integer[]{R.drawable.wx_sport1, R.drawable.wx_sport2_tw,
            R.drawable.wx_sport3_tw, R.drawable.wx_sport4_tw, R.drawable.wx_sport5_tw,
            R.drawable.wx_sport6_tw};
    private String[] descTwStr = new String[]{twDesc1,twDesc2,twDesc3,twDesc4,twDesc5,twDesc6};


    //英文的图片
    private Integer[] enImg = new Integer[]{R.drawable.wx_sport1, R.drawable.wx_sport2_en,
            R.drawable.wx_sport3_en, R.drawable.wx_sport4_en, R.drawable.wx_sport5_en,
            R.drawable.wx_sport6_en};
    private String[] descEnStr = new String[]{enDesc1,enDesc2,enDesc3,enDesc4,enDesc5,enDesc6};

}
