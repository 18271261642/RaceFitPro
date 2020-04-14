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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.DateTimeUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IWomenDataListener;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.datas.WomenData;
import com.veepoo.protocol.model.enums.EWomenStatus;
import com.veepoo.protocol.model.settings.WomenSetting;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 怀孕期
 * Created by Admin
 * Date 2018/11/15
 */
public class PregnancyFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "PregnancyFragment";

    View view;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.expeDateTv)
    TextView expeDateTv;
    @BindView(R.id.lastJingqiStartTv)
    TextView lastJingqiStartTv;
    //智能预测开关
    @BindView(R.id.babyBornSmartTogg)
    ToggleButton babyBornSmartTogg;
    Unbinder unbinder;
    @BindView(R.id.lastJingqiRel)
    RelativeLayout lastJingqiRel;

    private Calendar calendar;
    boolean isSmartSwitch = false;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b36_pregnancy, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();
        initData();

        return view;
    }

    private void initData() {
        calendar = Calendar.getInstance();
        if (getActivity() == null)
            return;
        //预产期日期
        String babyBorn = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.BABY_BORN_DATE, WatchUtils.getCurrentDate());
        if (WatchUtils.isEmpty(babyBorn))
            babyBorn = WatchUtils.getCurrentDate();
        expeDateTv.setText(babyBorn);

    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.personal_info));
        toolbar.setNavigationIcon(R.mipmap.backs);
        babyBornSmartTogg.setOnCheckedChangeListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        //最后一次月经的开始日
        String ltMen = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
        if(WatchUtils.isEmpty(ltMen))
            ltMen = WatchUtils.getCurrentDate();
        lastJingqiStartTv.setText(ltMen);
        //判断智能预测开关是否打开
        isSmartSwitch = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,false);
        Log.e(TAG,"--------isSmartSwitch="+isSmartSwitch);
        babyBornSmartTogg.setChecked(isSmartSwitch);
        if(isSmartSwitch){
            lastJingqiRel.setVisibility(View.VISIBLE);
            lastJingqiStartTv.setText(ltMen);
        }else{
            lastJingqiRel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.b36PregnRel, R.id.lastJingqiRel, R.id.b36PregnSaveBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b36PregnRel:  //预产期
                readyBabyDate();
                break;
            case R.id.lastJingqiRel:    //最后一次经期开始日
                lastJingqiDate();
                break;
            case R.id.b36PregnSaveBtn:  //保存
                //预产期的日期
                String babyBornDate = expeDateTv.getText().toString().trim();
                if(!WatchUtils.comPariDate(WatchUtils.getCurrentDate(),babyBornDate)){
                    ToastUtil.showToast(getActivity(),"预产期需大于当前日期!");
                    return;
                }
                doWatchOper();
                break;
        }
    }

    private void doWatchOper() {
        SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
        //判断智能预测开关是否打开
        SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,isSmartSwitch);


        String resultBabyDay = null;    //预产期
        String resultLastManDay = null; //最后一次经期
        try{
            if(isSmartSwitch){      //智能预测，输入最后一次经期开始日
                String ltMen = lastJingqiStartTv.getText().toString().trim();
                SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,ltMen.trim());
                resultLastManDay = ltMen;
                //根据最后一次经期推算预产期
                long longBabyBornD = sdf.parse(ltMen).getTime();
                //预产期
                resultBabyDay = WatchUtils.getLongToDate("yyyy-MM-dd", longBabyBornD + (86400000L * 280));


            }else{
                //预产期的日期
                String babyBornDate = expeDateTv.getText().toString().trim();
                resultBabyDay = babyBornDate;
                long longBabyD = sdf.parse(babyBornDate).getTime();
                //推算的最后一次经期
                resultLastManDay = WatchUtils.getLongToDate("yyyy-MM-dd", longBabyD - (86400000L * 280));
            }
            SharedPreferencesUtils.setParam(getActivity(),Commont.BABY_BORN_DATE,resultBabyDay);

            if(MyCommandManager.DEVICENAME != null){
                //预产期的年月日
                //计算年月日2018-11-26
                int babyYear = DateTimeUtils.getCurrYear(resultBabyDay);
                //月
                int babyMonth = DateTimeUtils.getCurrMonth(resultBabyDay);
                //日
                int babyDay = DateTimeUtils.getCurrDay(resultBabyDay);
                TimeData babyPeringTimeData = new TimeData(babyYear,babyMonth,babyDay);
                //最后一次月经的年月日

                int ltYear = DateTimeUtils.getCurrYear(resultLastManDay);
                //月
                int ltMonth = DateTimeUtils.getCurrMonth(resultLastManDay);
                //日
                int ltDay = DateTimeUtils.getCurrDay(resultLastManDay);
                TimeData lastMenTimeData = new TimeData(ltYear,ltMonth,ltDay);
                boolean isB36Noti = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.IS_B36_JINGQI_NOTI,false);
                if(isB36Noti){
                    WomenSetting womenSetting = new WomenSetting(EWomenStatus.PREING,babyPeringTimeData,lastMenTimeData);
                    MyApp.getInstance().getVpOperateManager().settingWomenState(iBleWriteResponse, new IWomenDataListener() {
                        @Override
                        public void onWomenDataChange(WomenData womenData) {
                            Log.e(TAG,"------womenData="+womenData.toString());
                        }
                    }, womenSetting);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        getActivity().finish();

    }

    //最后一次经期时间
    private void lastJingqiDate() {
        DatePick pickerPopWin = new DatePick.Builder(getActivity(), new DatePick.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                if(WatchUtils.comPariDate(WatchUtils.getCurrentDate(),dateDesc)){
                    ToastUtil.showToast(getActivity(),getResources().getString(R.string.b36_future));
                    return;
                }

                lastJingqiStartTv.setText(dateDesc);
                String lastMen = dateDesc.trim();
                Log.e(TAG,"---------lastMen="+lastMen);
                SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,lastMen.trim());
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1920) //min year in loop
                .maxYear(calendar.get(Calendar.YEAR)) // max year in loop
                .dateChose(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH)+1 + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "") // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(getActivity());
    }

    //预产期日期选择
    private void readyBabyDate() {
        DatePick pickerPopWin = new DatePick.Builder(getActivity(), new DatePick.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                if(!WatchUtils.comPariDate(WatchUtils.getCurrentDate(),dateDesc)){
                    ToastUtil.showToast(getActivity(),"预产期需大于当前日期!");
                    return;
                }

                expeDateTv.setText(dateDesc);
                //预产期的日期
                String babyBornDate = expeDateTv.getText().toString().trim();
                SharedPreferencesUtils.setParam(getActivity(), Commont.BABY_BORN_DATE, babyBornDate);


            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(calendar.get(Calendar.YEAR)) //min year in loop
                .maxYear(calendar.get(Calendar.YEAR) + 1) // max year in loop
                .dateChose(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH)) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(getActivity());
    }


    //开关
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.babyBornSmartTogg) {
           if(babyBornSmartTogg.isPressed()){
               isSmartSwitch = isChecked;
               if (isChecked) {
                   lastJingqiRel.setVisibility(View.VISIBLE);
               } else {
                   lastJingqiRel.setVisibility(View.GONE);
               }
           }


        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
