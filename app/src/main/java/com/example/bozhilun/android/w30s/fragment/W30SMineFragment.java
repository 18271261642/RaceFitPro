package com.example.bozhilun.android.w30s.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.b30.B30SysSettingActivity;
import com.example.bozhilun.android.friend.FriendActivity;
import com.example.bozhilun.android.siswatch.mine.NotiMsgFragment;
import com.example.bozhilun.android.siswatch.utils.UpdateGooglePlayManager;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.BaseFragment;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.activity.W30SSettingActivity;
import com.bumptech.glide.Glide;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @aboutContent: 联系人界面
 * @author： An
 * @crateTime: 2018/3/5 17:04
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SMineFragment extends BaseFragment {

    private static final String TAG = "W30SMineFragment";

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
    @BindView(R.id.mine_mac_id)
    TextView mineMacId;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.privatemode_cardview)
    CardView privatemodeCardview;//排行榜

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;

    private UpdateManager updateManager;
    //游客的userId
    String userId = "9278cc399ab147d0ad3ef164ca156bf0";


    String bleMac = null;
    String saveUserId = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bleMac = (String) SharedPreferencesUtils.readObject(getContext(), Commont.BLEMAC);
        saveUserId = (String) SharedPreferencesUtils.readObject(getActivity(), "userId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b18i_mine, container, false);
        unbinder = ButterKnife.bind(this, view);

        initData();

        getMyInfoData();    //获取我的总数
        return view;
    }


    /**
     * 获取距离等信息，包括用户资料
     */
    private void getMyInfoData() {
        String saveBleMac = WatchUtils.getSherpBleMac(getActivity());
        if (saveBleMac == null)
            return;
        String myInfoUrl = Commont.FRIEND_BASE_URL + URLs.myInfo;
        JSONObject js = new JSONObject();
        try {
            js.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            js.put("deviceCode", saveBleMac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, myInfoUrl, js.toString());

    }



    @Override
    public void onResume() {
        super.onResume();
        showConnStatus();
        checkUpdateApp();
    }

    //显示连接状态
    private void showConnStatus(){
        if (MyCommandManager.DEVICENAME != null) {
            if (!WatchUtils.isEmpty(bleMac)) {
                String substring = bleMac.substring((bleMac.length() - 5), bleMac.length());
                if (mineMacId != null) mineMacId.setText(substring);
            }
        }else{
            if(getActivity() != null && !getActivity().isFinishing())
                mineMacId.setText(getResources().getString(R.string.string_not_coon));
        }
    }



    //检查更新
    private void checkUpdateApp() {
        if(getActivity().getPackageName() != null && getActivity().getPackageName().equals("com.example.bozhilun.android")){
            updateManager = new UpdateManager(getActivity(), Commont.FRIEND_BASE_URL + URLs.getvision);
            updateManager.checkForUpdate(true);
        }else{
            UpdateGooglePlayManager updateGooglePlayManager = new UpdateGooglePlayManager();
            updateGooglePlayManager.checkForUpdate(getContext());
        }
    }



    private void initData() {

        //数据返回
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e("mine", "----ssss--result----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 200) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("data");
                            if (myInfoJsonObject != null) {
                                String distances = myInfoJsonObject.getString("distance");
                                if (WatchUtils.judgeUnit(MyApp.getContext())) {
                                    //总公里数
                                    totalKilometers.setText(WatchUtils.resrevedDeci(distances.trim()) + "km");
                                } else {
                                    totalKilometers.setText(WatchUtils.doubleunitToImperial(Double.valueOf(distances), 0) + "mi");
                                }

                                String counts = myInfoJsonObject.getString("count");
                                if (!WatchUtils.isEmpty(myInfoJsonObject.getString("count"))) {
                                    //达标天数
                                    standardDay.setText("" + myInfoJsonObject.getString("count") + getResources().getString(R.string.data_report_day));
                                }
                                String stepNums = myInfoJsonObject.getString("stepNumber");
                                if (!WatchUtils.isEmpty(stepNums)) {
                                    //平均步数
                                    standardDay.setText("" + myInfoJsonObject.getString("stepNumber") + getResources().getString(R.string.daily_numberofsteps_default));
                                }

                                //个人资料
                                JSONObject userJson = myInfoJsonObject.getJSONObject("userInfo");
                                if (userJson != null) {
                                    //昵称
                                    userName.setText("" + userJson.getString("nickname") + "");
                                    //头像
                                    String imgHead = userJson.getString("image");
                                    if (!WatchUtils.isEmpty(imgHead)) {
                                        //头像
                                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true);
                                        Glide.with(getActivity()).load(imgHead)
                                                .apply(mRequestOptions).into(userImageHead);    //头像
                                    }


                                }


                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                };

            }

        };
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if(updateManager != null)
            updateManager.destoryUpdateBroad();
    }


    @OnClick({R.id.privatemode_cardview,
            R.id.personalData, R.id.smartAlert,
            R.id.findFriends, R.id.mineSetting,
            R.id.userImageHead,R.id.card_frend,
            R.id.mineNotiMsgCardView})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.userImageHead:    //点击头像
            case R.id.personalData://个人资料
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.privatemode_cardview://排行榜
                break;
            case R.id.smartAlert://功能设置-----改----》mine_dev_image
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    SharePeClear.sendCmdDatas(getContext());
                    startActivity(new Intent(getActivity(), W30SSettingActivity.class).putExtra("is18i", "W30S"));
                } else {
                    try {
                        String saveMac = WatchUtils.getSherpBleMac(getContext());
                        MyApp.getInstance().getW37BleOperateManager().stopScan();
                        if(!WatchUtils.isEmpty(saveMac))
                             MyApp.getInstance().getW37BleOperateManager().disBleDeviceByMac(saveMac);
                        startActivity(new Intent(getContext(), NewSearchActivity.class));
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.findFriends://查找朋友
                //startActivity(new Intent(getActivity(), FriendApplyActivity.class).putExtra("is18i", "W30S"));
                break;
            case R.id.mineSetting://设置
                //startActivity(new Intent(getActivity(), B18IAppSettingActivity.class).putExtra("is18i", "W30S"));
                startActivity(new Intent(getActivity(), B30SysSettingActivity.class));
                break;
            case R.id.card_frend://好友设置
                String saveUserId = (String) SharedPreferencesUtils.readObject(getActivity(),Commont.USER_ID_DATA);
                if (!WatchUtils.isEmpty(saveUserId) && !saveUserId.equals(userId)){
                    startActivity(new Intent(getActivity(), FriendActivity.class));
                }else {
                    ToastUtil.showShort(MyApp.getInstance(),getString(R.string.noright));
                }
                break;
            case R.id.mineNotiMsgCardView:  //消息中心
                startActivity(new Intent(getActivity(),NotiMsgFragment.class));
                break;
        }
    }
}