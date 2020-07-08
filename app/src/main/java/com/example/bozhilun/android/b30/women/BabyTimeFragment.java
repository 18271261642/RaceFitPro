package com.example.bozhilun.android.b30.women;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IWomenDataListener;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.datas.WomenData;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.enums.EWomenStatus;
import com.veepoo.protocol.model.settings.WomenSetting;
import java.util.ArrayList;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * baby的出生日fragment
 * Created by Admin
 * Date 2018/11/15
 */
public class BabyTimeFragment extends Fragment {

    private static final String TAG = "BabyTimeFragment";

    View view;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.babySexTv)
    TextView babySexTv;
    @BindView(R.id.babyBirtyTv)
    TextView babyBirtyTv;
    @BindView(R.id.babyJingqiLongTv)
    TextView babyJingqiLongTv;
    @BindView(R.id.b36BabyInterTv)
    TextView b36BabyInterTv;
    @BindView(R.id.b36BabyJingqiAutoTogg)
    ToggleButton b36BabyJingqiAutoTogg;
    Unbinder unbinder;

    //宝宝性别的选择
    private AlertDialog.Builder alertDialog;
    private ArrayList<String> dateLong;
    private Calendar calendar;

    boolean isSmartAuto = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b36_babytime, container, false);
        unbinder = ButterKnife.bind(this, view);

        initViews();
        initData();


        return view;
    }

    private void initData() {
        dateLong = new ArrayList<>();
        calendar = Calendar.getInstance();
        //判断性别
        String babySex = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_BABY_SEX, "M");
        if(WatchUtils.isEmpty(babySex))
            babySex = "M";
        if(babySex.equals("M")){
            babySexTv.setText("男");

        }else{
            babySexTv.setText("女");
        }

        //生日
        String babyBirth = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_BABY_BIRTHDAY,WatchUtils.getCurrentDate());
        if(WatchUtils.isEmpty(babyBirth))
            babyBirth = WatchUtils.getCurrentDate();
        babyBirtyTv.setText(babyBirth);
        //间隔长度
        String menesInterval = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.WOMEN_MEN_INTERVAL, "28");
        if(WatchUtils.isEmpty(menesInterval))
            menesInterval = "28";
        b36BabyInterTv.setText(menesInterval);
        //月经持续长度
        String menseLength = (String) SharedPreferencesUtils.getParam(getActivity(), Commont.WOMEN_MEN_LENGTH, "5");
        if(WatchUtils.isEmpty(menseLength))
            menseLength = "5";
        babyJingqiLongTv.setText(menseLength);



    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.personal_info));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        b36BabyJingqiAutoTogg.setOnCheckedChangeListener(onCheckedChangeListener);
        //智能预测开关是否打开
        isSmartAuto = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,false);
        b36BabyJingqiAutoTogg.setChecked(isSmartAuto);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.b36BabySexRel, R.id.b36BabyBirtyRel, R.id.b36BabyJingqiRel,
            R.id.b36BabyJingqiIntervalRel, R.id.b36BabyJingqiSaveBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b36BabySexRel:    //性别选择
                chooseBabySex();
                break;
            case R.id.b36BabyBirtyRel:  //出生日期选择
                chooseBabyBirty();
                break;
            case R.id.b36BabyJingqiRel: //经期长度选择
                addSelectDaty(0);
                break;
            case R.id.b36BabyJingqiIntervalRel: //经期间隔
                addSelectDaty(1);
                break;
            case R.id.b36BabyJingqiSaveBtn:     //保存
                saveWomenStatus();
                break;
        }
    }

    private void saveWomenStatus() {
        if(getActivity() == null)
            return;
        SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_BABY_SEX, babySexTv.getText().toString().trim());
        SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_INTERVAL, b36BabyInterTv.getText().toString().trim());
        SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_LENGTH, babyJingqiLongTv.getText().toString().trim());
        //保存下自动预测的最后一次月经
        if(isSmartAuto){
            String ltMen = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
            if(WatchUtils.isEmpty(ltMen))
                SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
        }

        SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_LAST_MEN_STATUS,isSmartAuto);


        if(MyCommandManager.DEVICENAME != null){
            //baby的性别
            String baby_sex = babySexTv.getText().toString().trim();
            //最后一次月经的时间
            String lastMenDate = (String) SharedPreferencesUtils.getParam(getActivity(),Commont.WOMEN_LAST_MENSTRUATION_DATE,WatchUtils.getCurrentDate());
            if(WatchUtils.isEmpty(lastMenDate))
                lastMenDate = WatchUtils.getCurrentDate();
            int year = Integer.valueOf(lastMenDate.substring(0,4).trim());
            int month = Integer.valueOf(lastMenDate.substring(5,7).trim());
            int day = Integer.valueOf(lastMenDate.substring(8,lastMenDate.length()).trim());

            //baby的生日
            String baby_birth = babyBirtyTv.getText().toString().trim();
            int birthYear = Integer.valueOf(baby_birth.substring(0,4).trim());
            int birthMonth = Integer.valueOf(baby_birth.substring(5,7).trim());
            int birthDay = Integer.valueOf(baby_birth.substring(8,baby_birth.length()).trim());

            WomenSetting womenSetting = new WomenSetting(EWomenStatus.MAMAMI,
                    Integer.valueOf(b36BabyInterTv.getText().toString().trim()),
                    Integer.valueOf(babyJingqiLongTv.getText().toString().trim())
                    ,new TimeData(year,month,day),baby_sex.equals("男")?ESex.MAN:ESex.WOMEN,new TimeData(birthYear,birthMonth,birthDay));


            boolean isB36Noti = (boolean) SharedPreferencesUtils.getParam(getActivity(),Commont.IS_B36_JINGQI_NOTI,false);
            if(isB36Noti){
                MyApp.getInstance().getVpOperateManager().settingWomenState(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int i) {

                    }
                }, new IWomenDataListener() {
                    @Override
                    public void onWomenDataChange(WomenData womenData) {
                        Log.e(TAG,"--------宝妈期="+womenData.toString());
                    }
                }, womenSetting);
            }

        }

        getActivity().finish();





    }

    //选择宝宝性别
    private void chooseBabySex() {
        if (getActivity() == null)
            return;
        String[] babySex = new String[]{getResources().getString(R.string.sex_nan), getResources().getString(R.string.sex_nv)};
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("请选择性别")
                .setItems(babySex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                babySexTv.setText(getResources().getString(R.string.sex_nan));
                                break;
                            case 1:
                                babySexTv.setText(getResources().getString(R.string.sex_nv));
                                break;
                        }
                    }
                }).setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.create().show();
    }


    private void addSelectDaty(final int flag) {
        int defaultDaty = 0;
        dateLong.clear();
        if (flag == 0) {  //经期长度
            defaultDaty = 5;
            for (int i = 2; i < 14; i++) {
                dateLong.add(i + "");
            }
        } else if (flag == 1) {
            defaultDaty = 28;
            for (int j = 15; j <= 100; j++) {
                dateLong.add(j + "");
            }
        }


        ProfessionPick weightPopWin = new ProfessionPick.Builder(getActivity(), new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                if (flag == 0) {  //经期长度
                    babyJingqiLongTv.setText(profession);
                    SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_MEN_LENGTH,profession);
                } else {  //间隔长度
                    b36BabyInterTv.setText(profession);
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
                .dateChose(defaultDaty+"") // date chose when init popwindow
                .build();
        weightPopWin.showPopWin(getActivity());


    }

    //选择baby的出生日期
    private void chooseBabyBirty() {
        DatePick pickerPopWin = new DatePick.Builder(getActivity(), new DatePick.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                if(getActivity() == null)
                    return;
                if(WatchUtils.comPariDate(WatchUtils.getCurrentDate(),dateDesc)){
                    ToastUtil.showToast(getActivity(),getResources().getString(R.string.b36_future));
                    return;
                }
                babyBirtyTv.setText(dateDesc);
                SharedPreferencesUtils.setParam(getActivity(),Commont.WOMEN_BABY_BIRTHDAY,dateDesc);
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1920) //min year in loop
                .maxYear(calendar.get(Calendar.YEAR)) // max year in loop
                .dateChose(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + 1 + "-" + calendar.get(Calendar.DAY_OF_MONTH)) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(getActivity());
    }



    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId() == R.id.b36BabyJingqiAutoTogg){
                if(b36BabyJingqiAutoTogg.isPressed()){
                    isSmartAuto = isChecked;

                }
            }
        }
    };


}
