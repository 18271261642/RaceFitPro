package com.example.bozhilun.android.h9.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.B18I.b18isystemic.B18IAppSettingActivity;
import com.example.bozhilun.android.B18I.b18isystemic.B18ITargetSettingActivity;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.friend.FriendActivity;
import com.example.bozhilun.android.h9.settingactivity.H9SettingsActivity;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/27 16:35
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9MineFragment extends Fragment {

    View b18iMineView;
    Unbinder unbinder;

    //头像显示ImageView
    @BindView(R.id.userImageHead)
    ImageView userImageHead;
    //用户名称显示TextView
    @BindView(R.id.userName)
    TextView userName;
    //总公里数显示TextView
    @BindView(R.id.totalKilometers)
    TextView totalKilometers;
    //日均步数显示TextView
    @BindView(R.id.equalStepNumber)
    TextView equalStepNumber;
    //达标天数显示TextView
    @BindView(R.id.standardDay)
    TextView standardDay;
//    @BindView(R.id.privatemode_cardview)//排行榜
//            CardView privatemodeCardview;
    String userId = "9278cc399ab147d0ad3ef164ca156bf0";

    private CommonSubscriber commonSubscriber, commonSubscriber2;
    private SubscriberOnNextListener subscriberOnNextListener, subscriberOnNextListener2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        b18iMineView = inflater.inflate(R.layout.fragment_b18i_mine, container, false);
        unbinder = ButterKnife.bind(this, b18iMineView);

        b18iMineView.findViewById(R.id.findFriends).setVisibility(View.GONE);//隐藏好友功能
//        b18iMineView.findViewById(R.id.card_frend).setVisibility(View.GONE);//隐藏好友功能
//        b18iMineView.findViewById(R.id.view_card).setVisibility(View.GONE);//隐藏好友功能
        initData();
        //获取用户信息
        getUserInfoData();
        //获取数据展示
        getUserSportData();

        return b18iMineView;
    }

    private void initData() {
        //我的数据返回
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {//{"userInfo":{"birthday":"1994-12-27","image":"http://47.90.83.197/image/2017/07/31/1501490388771.jpg","nickName":"孙建华","sex":"M","weight":"60 kg","userId":"5c2b58f0681547a0801d4d4ac8465f82","phone":"15916947377","height":"175 cm"},"resultCode":"001"}
                Log.e("mine", "-----个人信息-----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                            if (myInfoJsonObject != null) {
                                userName.setText("" + myInfoJsonObject.getString("nickName") + "");
                                String imgHead = myInfoJsonObject.getString("image");
                                if (!WatchUtils.isEmpty(imgHead)) {
                                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true);

                                    Glide.with(getActivity()).load(myInfoJsonObject.getString("image"))
                                            .apply(mRequestOptions).into(userImageHead);    //头像
                                    userId = myInfoJsonObject.getString("userId");
                                }
                                String userHeight = myInfoJsonObject.getString("height");
                                if (userHeight != null) {
                                    if (userHeight.contains("cm")) {
                                        String newHeight = userHeight.substring(0, userHeight.length() - 2);
                                        SharedPreferencesUtils.setParam(getActivity(), "userheight", newHeight.trim());
                                    } else {
                                        SharedPreferencesUtils.setParam(getActivity(), "userheight", userHeight.trim());
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        //显示数据返回
        //数据返回
        subscriberOnNextListener2 = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) { //{"myInfo":{"distance":48.3,"count":2,"stepNumber":1582},"resultCode":"001"}
                Log.e("mine", "------result----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("resultCode") == 001) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("myInfo");
                            if (myInfoJsonObject != null) {
                                String distances = myInfoJsonObject.getString("distance").trim().replace(" ","");
                                if (!WatchUtils.isEmpty(distances)) {
                                    BigDecimal decimal = new BigDecimal(Double.valueOf(distances));
                                    BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                                    totalKilometers.setText(setScale.doubleValue() + "");
                                    //总公里数
//                                    totalKilometers.setText("" + distances + "");
                                }
                                String counts = myInfoJsonObject.getString("count");
                                if (!WatchUtils.isEmpty(myInfoJsonObject.getString("count"))) {
                                    //达标天数
                                    String count = myInfoJsonObject.getString("count").trim().replace(" ","");
                                    String stepNumber = myInfoJsonObject.getString("stepNumber").trim().replace(" ","");
                                    if (!WatchUtils.isEmpty(stepNumber)){
                                        BigDecimal decimal = new BigDecimal(Integer.valueOf(count));
                                        BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                                        standardDay.setText(setScale.intValue() + "");
                                    }
                                }
                                String stepNums = myInfoJsonObject.getString("stepNumber");
                                if (!WatchUtils.isEmpty(stepNums)) {
                                    //平均步数
                                    String stepNumber = myInfoJsonObject.getString("stepNumber").trim().replace(" ","");
                                    if (!WatchUtils.isEmpty(stepNumber)){
                                        BigDecimal decimal = new BigDecimal(Integer.valueOf(stepNumber));
                                        BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                                        equalStepNumber.setText(setScale.intValue() + "");
                                    }

                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    //获取用户信息
    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObj.toString());
    }

    //获取显示的数据
    private void getUserSportData() {
        String myInfoUrl = URLs.HTTPs + URLs.myInfo;
        JSONObject js = new JSONObject();
        try {
            js.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            js.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), Commont.BLEMAC));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber2 = new CommonSubscriber(subscriberOnNextListener2, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, myInfoUrl, js.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.privatemode_cardview, R.id.targetSetting,
            R.id.personalData, R.id.smartAlert, R.id.card_frend, R.id.mineSetting, R.id.userImageHead})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userImageHead:    //点击头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.privatemode_cardview://排行榜
//                startActivity(new Intent(getActivity(), B18IRankingListActivity.class).putExtra("is18i", "H9"));
                break;
            case R.id.targetSetting://目标设置
                startActivity(new Intent(getActivity(), B18ITargetSettingActivity.class).putExtra("is18i", "H9"));
                break;
            case R.id.personalData://个人资料
                //  startActivity(new Intent(getActivity(), MinePersonDataActivity.class).putExtra("is18i", "H9"));
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.smartAlert://功能设置-----改----》mine_dev_image
                if (MyCommandManager.DEVICENAME != null) {    //已连接
//                    startActivity(new Intent(getActivity(), B18ISettingActivity.class).putExtra("is18i", "H9"));
                    startActivity(new Intent(getActivity(), H9SettingsActivity.class).putExtra("is18i", "H9"));
                } else {
                    startActivity(new Intent(getContext(), NewSearchActivity.class));
                    getActivity().finish();
                }
                break;
            case R.id.card_frend://查找朋友
                if (!userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {
                    startActivity(new Intent(getActivity(), FriendActivity.class));
                } else {
                    ToastUtil.showShort(MyApp.getInstance(), getString(R.string.noright));
                }
                break;
            case R.id.mineSetting://设置
                startActivity(new Intent(getActivity(), B18IAppSettingActivity.class).putExtra("is18i", "H9"));
                break;
        }
    }
}
