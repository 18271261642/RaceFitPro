package com.example.bozhilun.android.recommend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.view.LazyFragment;

/**
 * Created by Admin
 * Date 2020/4/1
 */
public class SquareFragment extends LazyFragment {

    private View squareView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        squareView = inflater.inflate(R.layout.fragment_recommend_square_layout,container,false);


        return squareView;
    }
}
