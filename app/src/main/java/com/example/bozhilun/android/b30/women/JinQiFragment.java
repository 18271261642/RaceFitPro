package com.example.bozhilun.android.b30.women;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IWomenDataListener;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.datas.WomenData;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.enums.EWomenStatus;
import com.veepoo.protocol.model.settings.WomenSetting;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 经期和备孕期设置
 * Created by Admin
 * Date 2018/11/12
 */
public class JinQiFragment extends Fragment {

    private static final String TAG = "JinQiFragment";

    View view;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //经期长度
    @BindView(R.id.jingqiLongTv)
    TextView jingqiLongTv;
    //间隔长度
    @BindView(R.id.b36InterTv)
    TextView b36InterTv;
    @BindView(R.id.b36JingqiAutoTogg)
    ToggleButton b36JingqiAutoTogg;
    Unbinder unbinder;

    private ArrayList<String> dateLong;

    boolean isSmartSwitch = false;

    private int argPar ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() == null)
            return;
        Bundle bundle = getArguments();
        argPar = bundle != null ? bundle.getInt("keyArg"):0;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.b36_jingqi_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();

        initData();

        return view;
    }

    private void initData() {
        dateLong = new ArrayList<>();
        if(getActivity() == null) return;
        //间隔长度
        String menesInterval = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.WOMEN_MEN_INTERVAL, "28");
        if(WatchUtils.isEmpty(menesInterval))
            menesInterval = "28";
        b36InterTv.setText(menesInterval);
        //月经持续长度
        String menseLength = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.WOMEN_MEN_LENGTH, "5");
        if(WatchUtils.isEmpty(menseLength))
            menseLength = "5";
        jingqiLongTv.setText(menseLength);

        isSmartSwitch = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,false);
        b36JingqiAutoTogg.setChecked(isSmartSwitch);


        if(MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("B36")){
            MyApp.getInstance().getVpOperateManager().readWomenState(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {

                }
            }, new IWomenDataListener() {
                @Override
                public void onWomenDataChange(WomenData womenData) {
                    Log.e(TAG,"---------读取女性状态="+womenData.toString());
                }
            });
        }

    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.personal_info));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null)
                   getActivity().finish();
            }
        });
        b36JingqiAutoTogg.setOnCheckedChangeListener(onCheckedChangeListener);

    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId() == R.id.b36JingqiAutoTogg){
                if(buttonView.isPressed()){
                    SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,isChecked);
                    String ltMen = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
                    if(WatchUtils.isEmpty(ltMen))
                        SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
                }
            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.b36JingqiRel, R.id.b36JingqiIntervalRel, R.id.b36JingqiSaveBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b36JingqiRel:     //持续天数
                addSelectDaty(0);
                break;
            case R.id.b36JingqiIntervalRel:     //间隔天数
                addSelectDaty(1);
                break;
            case R.id.b36JingqiSaveBtn:     //保存
                if(getActivity() == null)
                    return;
                saveWomenStatus();

                break;
        }
    }

    private void saveWomenStatus() {
        //间隔长度
        SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_INTERVAL, b36InterTv.getText().toString().trim());
        //月经持续长度
        SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_LENGTH, jingqiLongTv.getText().toString().trim());
        SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,isSmartSwitch);



        if(MyCommandManager.DEVICENAME != null){
            //最后一次月经的时间
            String lastMenDate = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
            if(WatchUtils.isEmpty(lastMenDate))
                lastMenDate = WatchUtils.getCurrentDate();
            int year = DateTimeUtils.getCurrYear(lastMenDate);
            int month =DateTimeUtils.getCurrMonth(lastMenDate);
            int day = DateTimeUtils.getCurrDay(lastMenDate);

            int menseLength =  Integer.valueOf(jingqiLongTv.getText().toString().trim());
            int menesInterval = Integer.valueOf(b36InterTv.getText().toString().trim());
            TimeData timeData = new TimeData(year,month,day);
            WomenSetting womenSetting;
            //备孕期需要设置baby的性别和生日
            if(argPar != 0){
                ESex eSex = ESex.MAN;
                String babyBirth = WatchUtils.getCurrentDate();
                int currYear =  DateTimeUtils.getCurrYear(babyBirth);
                int currMonth = DateTimeUtils.getCurrMonth(babyBirth);
                int currDay = DateTimeUtils.getCurrDay(babyBirth);

                 womenSetting = new WomenSetting(EWomenStatus.PREREADY,
                        menseLength,menesInterval ,timeData,ESex.MAN,new TimeData(currYear,currMonth,currDay) );
            }else{
                womenSetting = new WomenSetting(EWomenStatus.MENES,
                        menseLength,menesInterval ,timeData);
            }


            Log.e(TAG,"---WomenSetting="+womenSetting.toString());


            B36SetWomenDataServer.setB30WomenData(argPar);





            boolean isB36Noti = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.IS_B36_JINGQI_NOTI,false);

            if(isB36Noti && MyCommandManager.DEVICENAME.equals("B36")){
                MyApp.getInstance().getVpOperateManager().settingWomenState(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new IWomenDataListener() {
                    @Override
                    public void onWomenDataChange(WomenData womenData) {
                        Log.e(TAG,"-------womenData="+womenData.toString());
                    }
                }, womenSetting);
            }

        }


        getActivity().finish();

    }


    private void addSelectDaty(final int flag){
        int defaultDay = 0;
        dateLong.clear();
        if(flag == 0){  //经期长度
            defaultDay = 5;
            for(int i = 2;i<14;i++){
                dateLong.add(i+"");
            }
        }else if(flag == 1){
            defaultDay = 28;
            for(int j = 15;j<=100;j++){
                dateLong.add(j+"");
            }
        }


        ProfessionPick weightPopWin = new ProfessionPick.Builder(getActivity(), new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                if(flag == 0){  //经期长度
                    jingqiLongTv.setText(profession);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_LENGTH,profession);
                }else{  //间隔长度
                    b36InterTv.setText(profession);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_INTERVAL,profession);
                }
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle))
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(dateLong) //min year in loop
                .dateChose(defaultDay+"") // date chose when init popwindow
                .build();
        weightPopWin.showPopWin(getActivity());


    }
}
