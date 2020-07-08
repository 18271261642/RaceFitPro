package com.example.bozhilun.android.b18.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.MessageHelpActivity;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * B18消息提醒页面
 * Created by Admin
 * Date 2019/11/21
 */
public class B18MessageAlertFragment extends LazyFragment {

    View view;
    @BindView(R.id.newSearchRightImg1)
    ImageView newSearchRightImg1;
    @BindView(R.id.newSearchTitleLeft)
    FrameLayout newSearchTitleLeft;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;
    @BindView(R.id.b30SkypeTogg)
    ToggleButton b30SkypeTogg;
    @BindView(R.id.b30WhatsAppTogg)
    ToggleButton b30WhatsAppTogg;
    @BindView(R.id.b30FacebookTogg)
    ToggleButton b30FacebookTogg;
    @BindView(R.id.b30LinkedTogg)
    ToggleButton b30LinkedTogg;
    @BindView(R.id.b30TwitterTogg)
    ToggleButton b30TwitterTogg;
    @BindView(R.id.b30ViberTogg)
    ToggleButton b30ViberTogg;
    @BindView(R.id.b30LineTogg)
    ToggleButton b30LineTogg;
    @BindView(R.id.b30SnapchartTogg)
    ToggleButton b30SnapchartTogg;
    @BindView(R.id.b30InstagramTogg)
    ToggleButton b30InstagramTogg;
    @BindView(R.id.b30GmailTogg)
    ToggleButton b30GmailTogg;
    @BindView(R.id.b30WechatTogg)
    ToggleButton b30WechatTogg;
    @BindView(R.id.b30QQTogg)
    ToggleButton b30QQTogg;
    @BindView(R.id.b30MessageTogg)
    ToggleButton b30MessageTogg;
    @BindView(R.id.b30PhoneTogg)
    ToggleButton b30PhoneTogg;
    @BindView(R.id.b30OhterTogg)
    ToggleButton b30OhterTogg;
    Unbinder unbinder;

    private Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_b30_msgalert, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        return view;
    }


    private void initViews() {
        newSearchTitleTv.setText(getResources().getString(R.string.Messagealert));
        newSearchRightImg1.setVisibility(View.VISIBLE);
        b30SkypeTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30WhatsAppTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30FacebookTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30LinkedTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30TwitterTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30ViberTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30LineTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30SnapchartTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30InstagramTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30GmailTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30WechatTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30QQTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30MessageTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30PhoneTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        b30OhterTogg.setOnCheckedChangeListener(onCheckedChangeListener);



        boolean isSkype = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISSkype, false);
        b30SkypeTogg.setChecked(isSkype);

        boolean isWhat = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISWhatsApp, false);
        b30WhatsAppTogg.setChecked(isWhat);

        boolean isFacebook = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISFacebook, false);
        b30FacebookTogg.setChecked(isFacebook);

        boolean isLinked = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISLinkendln, false);
        b30LinkedTogg.setChecked(isLinked);


        boolean isTwitter = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISTwitter, false);
        b30TwitterTogg.setChecked(isTwitter);

        boolean isLine = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISLINE, false);
        b30LineTogg.setChecked(isLine);


        boolean isWx = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISWechart, false);
        b30WechatTogg.setChecked(isWx);


        boolean isQQ = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISQQ, false);
        b30QQTogg.setChecked(isQQ);


        boolean isMsg = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISMsm, false);
        b30MessageTogg.setChecked(isMsg);


        boolean isPhone = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISPhone, false);
        //SharedPreferencesUtils.setParam(getmContext(), Commont.ISCallPhone, isChecked);
        b30PhoneTogg.setChecked(isPhone);


        boolean isInstagram = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISInstagram, false);
        b30InstagramTogg.setChecked(isInstagram);


        boolean isGmail = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISGmail, false);
        b30GmailTogg.setChecked(isGmail);


        boolean isSnap = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISSnapchart, false);
        b30SnapchartTogg.setChecked(isSnap);

        boolean isOther = (boolean) SharedPreferencesUtils.getParam(getmContext(), Commont.ISOhter, false);
        b30OhterTogg.setChecked(isOther);

    }

    private Context getmContext(){
        return mContext == null ? MyApp.getContext() : mContext;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1,
            R.id.msgOpenNitBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.newSearchRightImg1:
                startActivity(new Intent(getmContext(),MessageHelpActivity.class));
                break;
            case R.id.msgOpenNitBtn:
                Intent intents = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intents, 1001);
                break;
        }
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed())
                return;
            switch (buttonView.getId()) {
                case R.id.b30SkypeTogg: //skype
                    b30SkypeTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISSkype, isChecked);
                    break;
                case R.id.b30WhatsAppTogg:  //whatspp
                    b30WhatsAppTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISWhatsApp, isChecked);
                    break;
                case R.id.b30FacebookTogg:  //facebook
                    b30FacebookTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISFacebook, isChecked);
                    break;
                case R.id.b30LinkedTogg:    //linked
                    b30LinkedTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISLinkendln, isChecked);
                    break;
                case R.id.b30TwitterTogg:   //twitter
                    b30TwitterTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISTwitter, isChecked);
                    break;
                case R.id.b30LineTogg:  //line
                    b30LineTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISLINE, isChecked);
                case R.id.b30WechatTogg:    //wechat
                    b30WechatTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISWechart, isChecked);
                    break;
                case R.id.b30QQTogg:    //qq
                    b30QQTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISQQ, isChecked);
                    break;
                case R.id.b30MessageTogg:   //msg
                    requestPermiss(Permission.READ_SMS);
                    b30MessageTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISMsm, isChecked);
                    break;
                case R.id.b30PhoneTogg: //phone
                    requestPermiss(Permission.READ_PHONE_STATE,Permission.READ_CALL_LOG,Permission.READ_CONTACTS);
                    b30PhoneTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISPhone, isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISCallPhone, isChecked);
                    break;
                /**
                 * b30InstagramTogg
                 * b30GmailTogg
                 * b30SnapchartTogg
                 * b30OhterTogg
                 */
                case R.id.b30InstagramTogg:
                    b30InstagramTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISInstagram, isChecked);
                    break;
                case R.id.b30GmailTogg:
                    b30GmailTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISGmail, isChecked);
                    break;
                case R.id.b30SnapchartTogg:
                    b30SnapchartTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISSnapchart, isChecked);
                    break;
                case R.id.b30OhterTogg:
                    b30OhterTogg.setChecked(isChecked);
                    SharedPreferencesUtils.setParam(getmContext(), Commont.ISOhter, isChecked);
                    break;

            }
        }
    };



    private void requestPermiss(String...permission){
        AndPermission.with(B18MessageAlertFragment.this).runtime().permission(permission).start();
    }

}
