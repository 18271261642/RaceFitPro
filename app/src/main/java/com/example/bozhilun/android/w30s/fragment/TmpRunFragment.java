package com.example.bozhilun.android.w30s.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.w30s.BaseFragment;

/**
 * Created by Administrator on 2018/6/29.
 */

public class TmpRunFragment extends BaseFragment {

    View tmpRunView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tmpRunView = inflater.inflate(R.layout.fragment_tem_run_layout,container,false);

        return tmpRunView;
    }
}
