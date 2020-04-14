package com.example.bozhilun.android.recommend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.view.LazyFragment;

/**
 * Created by Admin
 * Date 2020/4/1
 */
public class RecommendMineFragment extends LazyFragment {

    View mineView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mineView = inflater.inflate(R.layout.fragment_recommend_mine_layout,container,false);


        return mineView;
    }
}
