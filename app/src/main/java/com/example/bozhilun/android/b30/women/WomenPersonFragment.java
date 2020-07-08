package com.example.bozhilun.android.b30.women;


import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 个人信息fragment
 * Created by Admin
 * Date 2018/11/12
 */
public class WomenPersonFragment extends Fragment {

    View personView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Unbinder unbinder;
    //经期
    @BindView(R.id.womenStatusImg1)
    ImageView womenStatusImg1;
    //备孕期
    @BindView(R.id.womenStatusImg2)
    ImageView womenStatusImg2;
    //怀孕期
    @BindView(R.id.womenStatusImg3)
    ImageView womenStatusImg3;
    //宝妈期
    @BindView(R.id.womenStatusImg4)
    ImageView womenStatusImg4;
    @BindView(R.id.b36PreRel)
    RelativeLayout b36PreRel;

    private FragmentManager fragmentManager;

    int womenStatus = 0;


    private AlertDialog.Builder alertDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        personView = inflater.inflate(R.layout.b36_women_person_layout, container, false);
        unbinder = ButterKnife.bind(this, personView);

        initViews();

        initData();

        return personView;
    }

    private void initData() {
        if (getActivity() != null)
            fragmentManager = getActivity().getSupportFragmentManager();

        String bleName = (String) SharedPreferencesUtils.readObject(getActivity(), Commont.BLENAME);
        if (WatchUtils.isEmpty(bleName))
            return;
        b36PreRel.setVisibility(bleName.equals("B18") || bleName.equals("B50") || bleName.equals("B16")?View.GONE : View.VISIBLE);

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

        womenStatus = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.WOMEN_PHYSIOLOGY_STATUS, 0);
        doImgStatus(womenStatus);
    }


    private void doImgStatus(int womenStatus) {
        if (getActivity() == null)
            return;
        switch (womenStatus) {
            case 0:
                womenStatusImg1.setBackground(getActivity().getResources().getDrawable(R.drawable.register_menes));
                break;
            case 1:
                womenStatusImg2.setBackground(getActivity().getResources().getDrawable(R.drawable.register_menes_preready));
                break;
            case 2:
                womenStatusImg3.setBackground(getActivity().getResources().getDrawable(R.drawable.register_preing));
                break;
            case 3:
                womenStatusImg4.setBackground(getActivity().getResources().getDrawable(R.drawable.register_mamami));
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.b36JinQiRel, R.id.b36ReadyRel, R.id.b36PreRel, R.id.b36BabyRel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b36JinQiRel:  //经期
                showAlertDialogMsg(0);
                break;
            case R.id.b36ReadyRel:  //备孕期
                showAlertDialogMsg(1);
                break;
            case R.id.b36PreRel:    //怀孕期
                showAlertDialogMsg(2);
                break;
            case R.id.b36BabyRel:   //宝妈期
                showAlertDialogMsg(3);
                break;
        }

    }

    //显示提示框
    private void showAlertDialogMsg(final int code) {
        if (getActivity() == null)
            return;
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.confirm))
                .setMessage(getResources().getString(R.string.b36_update_women_status) + " " + statusData(code))
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doImgStatus(code);
                        doIntentClick(code);
                    }
                });
        alertDialog.create().show();
    }

    private void doIntentClick(int code) {
        if (getActivity() == null)
            return;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (code) {
            case 0:
                JinQiFragment jinQiFragment = new JinQiFragment();
                SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_PHYSIOLOGY_STATUS, 0);
                Bundle bundle = new Bundle();
                bundle.putInt("keyArg", 0);
                jinQiFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.womenCont, jinQiFragment, null);
                break;
            case 1:
                JinQiFragment jinQiFragment2 = new JinQiFragment();
                SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_PHYSIOLOGY_STATUS, 1);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("keyArg", 1);
                jinQiFragment2.setArguments(bundle2);
                fragmentTransaction.replace(R.id.womenCont, jinQiFragment2, null);
                break;
            case 2:
                SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_PHYSIOLOGY_STATUS, 2);
                fragmentTransaction.replace(R.id.womenCont, new PregnancyFragment(), null);
                break;
            case 3:
                SharedPreferencesUtils.setParam(getActivity(), Commont.WOMEN_PHYSIOLOGY_STATUS, 3);
                fragmentTransaction.replace(R.id.womenCont, new BabyTimeFragment(), null);
                break;
        }

        fragmentTransaction.commit();
    }


    private String statusData(int sta) {
        String txt = "";

        switch (sta) {
            case 0:
                txt = getActivity().getResources().getString(R.string.b36_record_menstruation);
                break;
            case 1:
                txt = getActivity().getResources().getString(R.string.b36_pre_for_pre);
                break;
            case 2:
                txt = getActivity().getResources().getString(R.string.b36_pregnancy);
                break;
            case 3:
                txt = getActivity().getResources().getString(R.string.b36_monther);
                break;
        }
        return txt;
    }

}
